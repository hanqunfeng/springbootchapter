package com.example.oauth2clientwebfluxdemo.security;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

/**
 * <h1>ServerOAuth2AuthorizedClientRepository</h1>
 * Created by hanqf on 2020/12/1 09:58.
 */

@Component
@Slf4j
public class CustomServerOAuth2AuthorizedClientRepository implements ServerOAuth2AuthorizedClientRepository {
    // @formatter:off
    private static final String COLUMN_NAMES = "client_registration_id, "
            + "principal_name, "
            + "access_token_type, "
            + "access_token_value, "
            + "access_token_issued_at, "
            + "access_token_expires_at, "
            + "access_token_scopes, "
            + "refresh_token_value, "
            + "refresh_token_issued_at";


    private static final String TABLE_NAME = "oauth2_authorized_client";
    private static final String PK_FILTER = "client_registration_id = ? AND principal_name = ?";
    // @formatter:on
    // @formatter:off
    private static final String LOAD_AUTHORIZED_CLIENT_SQL = "SELECT " + COLUMN_NAMES
            + " FROM " + TABLE_NAME
            + " WHERE " + PK_FILTER;

    // @formatter:off
    private static final String SAVE_AUTHORIZED_CLIENT_SQL = "INSERT INTO " + TABLE_NAME
            + " (" + COLUMN_NAMES + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String REMOVE_AUTHORIZED_CLIENT_SQL = "DELETE FROM " + TABLE_NAME + " WHERE " + PK_FILTER;
    // @formatter:on
    // @formatter:off
    private static final String UPDATE_AUTHORIZED_CLIENT_SQL = "UPDATE " + TABLE_NAME
            + " SET access_token_type = ?, access_token_value = ?, access_token_issued_at = ?,"
            + " access_token_expires_at = ?, access_token_scopes = ?,"
            + " refresh_token_value = ?, refresh_token_issued_at = ?"
            + " WHERE " + PK_FILTER;
    // @formatter:on
    @Resource
    private DatabaseClient databaseClient;
    @Autowired
    private ReactiveClientRegistrationRepository reactiveClientRegistrationRepository;
    // @formatter:on

    /**
     * 这里有一个注意事项：查询时不要使用.fetch()方法，其会按照字段名称的字母排序进行赋值，导致结果中key和value匹配混乱
    */
    @Override
    public <T extends OAuth2AuthorizedClient> Mono<T> loadAuthorizedClient(String clientRegistrationId, Authentication principal, ServerWebExchange exchange) {
        Assert.hasText(clientRegistrationId, "clientRegistrationId cannot be empty");
        Assert.hasText(principal.getName(), "principalName cannot be empty");

        Mono<OAuth2AuthorizedClient> oAuth2AuthorizedClientMono = databaseClient.sql(LOAD_AUTHORIZED_CLIENT_SQL)
                .bind(0, clientRegistrationId)
                .bind(1, principal.getName())
                .map(row -> {
                    String clientRegistrationId1 = row.get("client_registration_id", String.class);
                    String access_token_type = row.get("access_token_type", String.class);


                    OAuth2AccessToken.TokenType tokenType = null;
                    if (OAuth2AccessToken.TokenType.BEARER.getValue().equalsIgnoreCase(access_token_type)) {
                        tokenType = OAuth2AccessToken.TokenType.BEARER;
                    }

                    String tokenValue = new String(row.get("access_token_value", byte[].class), StandardCharsets.UTF_8);
                    Instant issuedAt = row.get("access_token_issued_at", LocalDateTime.class).atZone(ZoneId.systemDefault()).toInstant();
                    Instant expiresAt = row.get("access_token_expires_at", LocalDateTime.class).atZone(ZoneId.systemDefault()).toInstant();
                    Set<String> scopes = Collections.emptySet();
                    String accessTokenScopes = row.get("access_token_scopes", String.class);
                    if (accessTokenScopes != null) {
                        scopes = StringUtils.commaDelimitedListToSet(accessTokenScopes);
                    }
                    OAuth2AccessToken accessToken = new OAuth2AccessToken(tokenType, tokenValue, issuedAt, expiresAt, scopes);
                    OAuth2RefreshToken refreshToken = null;
                    byte[] refreshTokenValue = row.get("refresh_token_value", byte[].class);
                    if (refreshTokenValue != null) {
                        tokenValue = new String(refreshTokenValue, StandardCharsets.UTF_8);
                        issuedAt = null;
                        LocalDateTime refreshTokenIssuedAt = row.get("refresh_token_issued_at", LocalDateTime.class);
                        if (refreshTokenIssuedAt != null) {
                            issuedAt = refreshTokenIssuedAt.atZone(ZoneId.systemDefault()).toInstant();
                        }
                        refreshToken = new OAuth2RefreshToken(tokenValue, issuedAt);
                    }
                    String principalName = row.get("principal_name", String.class);

                    final OAuth2RefreshToken refreshToken1 = refreshToken;

                    Mono<ClientRegistration> clientRegistrationMono = reactiveClientRegistrationRepository
                            .findByRegistrationId(clientRegistrationId1);
                    return clientRegistrationMono
                            .switchIfEmpty(Mono.error(new DataRetrievalFailureException(
                                    "The ClientRegistration with id '" + clientRegistrationId1 + "' exists in the data source, "
                                            + "however, it was not found in the ClientRegistrationRepository.")))
                            .map(clientRegistration -> new OAuth2AuthorizedClient(clientRegistration, principalName, accessToken, refreshToken1));
                }).first().flatMap(oAuth2AuthorizedClientMono1 -> oAuth2AuthorizedClientMono1);

        return (Mono<T>) oAuth2AuthorizedClientMono.doOnNext(unused -> log.info("select client token info success!"));
    }

    @Override
    public Mono<Void> saveAuthorizedClient(OAuth2AuthorizedClient authorizedClient, Authentication principal, ServerWebExchange exchange) {

        Assert.notNull(authorizedClient, "authorizedClient cannot be null");
        Assert.notNull(principal, "principal cannot be null");

        return this.loadAuthorizedClient(authorizedClient.getClientRegistration().getRegistrationId(), principal, exchange)
                 .flatMap((Function<OAuth2AuthorizedClient, Mono<Optional<OAuth2AuthorizedClient>>>) oAuth2AuthorizedClient -> Mono.just(Optional.of(oAuth2AuthorizedClient)))
                 .defaultIfEmpty(Optional.empty())
                 .flatMap((Function<Optional<OAuth2AuthorizedClient>, Mono<Void>>) oAuth2AuthorizedClient -> {
                     if(!oAuth2AuthorizedClient.isPresent()){
                         return insertAuthorizedClient(authorizedClient,principal);
                     }else {
                         return updateAuthorizedClient(authorizedClient, principal);
                     }
                 });
    }

    private Mono<Void> updateAuthorizedClient(OAuth2AuthorizedClient authorizedClient, Authentication principal) {
        return databaseClient.sql(UPDATE_AUTHORIZED_CLIENT_SQL)
                .bind(0, authorizedClient.getAccessToken().getTokenType().getValue())
                .bind(1, authorizedClient.getAccessToken().getTokenValue().getBytes(StandardCharsets.UTF_8))
                .bind(2, timeFromInstant(authorizedClient.getAccessToken().getIssuedAt()))
                .bind(3, timeFromInstant(authorizedClient.getAccessToken().getExpiresAt()))
                .bind(4, StringUtils.collectionToCommaDelimitedString(authorizedClient.getAccessToken().getScopes()))
                .bind(5, authorizedClient.getRefreshToken().getTokenValue().getBytes(StandardCharsets.UTF_8))
                .bind(6, timeFromInstant(authorizedClient.getRefreshToken().getIssuedAt()))
                .bind(7, authorizedClient.getClientRegistration().getRegistrationId())
                .bind(8, principal.getName())
                .then()
                .doOnNext(unused -> log.info("update client token info success!"));
    }

    private Mono<Void> insertAuthorizedClient(OAuth2AuthorizedClient authorizedClient, Authentication principal) {
        return databaseClient.sql(SAVE_AUTHORIZED_CLIENT_SQL)
                .bind(0, authorizedClient.getClientRegistration().getRegistrationId())
                .bind(1, principal.getName())
                .bind(2, authorizedClient.getAccessToken().getTokenType().getValue())
                .bind(3, authorizedClient.getAccessToken().getTokenValue().getBytes(StandardCharsets.UTF_8))
                .bind(4, timeFromInstant(authorizedClient.getAccessToken().getIssuedAt()))
                .bind(5, timeFromInstant(authorizedClient.getAccessToken().getExpiresAt()))
                .bind(6, StringUtils.collectionToCommaDelimitedString(authorizedClient.getAccessToken().getScopes()))
                .bind(7, authorizedClient.getRefreshToken().getTokenValue().getBytes(StandardCharsets.UTF_8))
                .bind(8, timeFromInstant(authorizedClient.getRefreshToken().getIssuedAt()))
                .then()
                .doOnNext(unused -> log.info("insert client token info success!"));
    }

    @Override
    public Mono<Void> removeAuthorizedClient(String clientRegistrationId, Authentication principal, ServerWebExchange exchange) {

        Assert.hasText(clientRegistrationId, "clientRegistrationId cannot be empty");
        Assert.hasText(principal.getName(), "principalName cannot be empty");

        return databaseClient.sql(REMOVE_AUTHORIZED_CLIENT_SQL)
                .bind(0, clientRegistrationId)
                .bind(1, principal.getName())
                .then()
                .doOnNext(unused -> log.info("remove client token info success!"));
    }

    private String timeFromInstant(Instant instant) {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.ofInstant(instant, ZoneId.systemDefault()));
    }

}
