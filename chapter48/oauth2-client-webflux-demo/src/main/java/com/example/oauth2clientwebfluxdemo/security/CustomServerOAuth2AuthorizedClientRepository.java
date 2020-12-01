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

    private static final String COLUMN_NAMES_AS = "client_registration_id as 0_client_registration_id, "
            + "principal_name as 1_principal_name, "
            + "access_token_type as 2_access_token_type, "
            + "access_token_value as 3_access_token_value, "
            + "access_token_issued_at as 4_access_token_issued_at, "
            + "access_token_expires_at as 5_access_token_expires_at, "
            + "access_token_scopes as 6_access_token_scopes, "
            + "refresh_token_value as 7_refresh_token_value, "
            + "refresh_token_issued_at as 8_refresh_token_issued_at";

    private static final String TABLE_NAME = "oauth2_authorized_client";
    private static final String PK_FILTER = "client_registration_id = ? AND principal_name = ?";
    // @formatter:on
    // @formatter:off
    private static final String LOAD_AUTHORIZED_CLIENT_SQL = "SELECT " + COLUMN_NAMES
            + " FROM " + TABLE_NAME
            + " WHERE " + PK_FILTER;

    private static final String LOAD_AUTHORIZED_CLIENT_SQL_AS = "SELECT " + COLUMN_NAMES_AS
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
        //.fetch()
        //.first() //有bug，返回结果是按字段名称排序赋值的，也就是说，字段和值不匹配
        //.flatMap((Function<Map<String, Object>, Mono<OAuth2AuthorizedClient>>) stringObjectMap -> {
        //    String clientRegistrationId1 = (String) stringObjectMap.get("client_registration_id");
        //    Mono<ClientRegistration> clientRegistrationMono = reactiveClientRegistrationRepository
        //            .findByRegistrationId(clientRegistrationId1);
        //    return clientRegistrationMono
        //            .switchIfEmpty(Mono.error(new DataRetrievalFailureException(
        //                    "The ClientRegistration with id '" + clientRegistrationId1 + "' exists in the data source, "
        //                            + "however, it was not found in the ClientRegistrationRepository.")))
        //            .map(clientRegistration -> {
        //                OAuth2AccessToken.TokenType tokenType = null;
        //                if (OAuth2AccessToken.TokenType.BEARER.getValue().equalsIgnoreCase((String) stringObjectMap.get("access_token_type"))) {
        //                    tokenType = OAuth2AccessToken.TokenType.BEARER;
        //                }
        //
        //                String tokenValue = new String((byte[]) stringObjectMap.get("access_token_value"), StandardCharsets.UTF_8);
        //                Instant issuedAt = stringFrom((String)stringObjectMap.get("access_token_issued_at")).atZone(ZoneId.systemDefault()).toInstant();
        //                Instant expiresAt = stringFrom((String)stringObjectMap.get("access_token_expires_at")).atZone(ZoneId.systemDefault()).toInstant();
        //                Set<String> scopes = Collections.emptySet();
        //                String accessTokenScopes = (String) stringObjectMap.get("access_token_scopes");
        //                if (accessTokenScopes != null) {
        //                    scopes = StringUtils.commaDelimitedListToSet(accessTokenScopes);
        //                }
        //                OAuth2AccessToken accessToken = new OAuth2AccessToken(tokenType, tokenValue, issuedAt, expiresAt, scopes);
        //                OAuth2RefreshToken refreshToken = null;
        //                byte[] refreshTokenValue = (byte[]) stringObjectMap.get("refresh_token_value");
        //                if (refreshTokenValue != null) {
        //                    tokenValue = new String(refreshTokenValue, StandardCharsets.UTF_8);
        //                    issuedAt = null;
        //                    LocalDateTime refreshTokenIssuedAt = stringFrom((String)stringObjectMap.get("refresh_token_issued_at"));
        //                    if (refreshTokenIssuedAt != null) {
        //                        issuedAt = refreshTokenIssuedAt.atZone(ZoneId.systemDefault()).toInstant();
        //                    }
        //                    refreshToken = new OAuth2RefreshToken(tokenValue, issuedAt);
        //                }
        //                String principalName = (String) stringObjectMap.get("principal_name");
        //                return new OAuth2AuthorizedClient(clientRegistration, principalName, accessToken, refreshToken);
        //            });
        //});
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

    //private static final String UPDATE_AUTHORIZED_CLIENT_SQL = "UPDATE " + TABLE_NAME
    //        + " SET access_token_type = ?, access_token_value = ?, access_token_issued_at = ?,"
    //        + " access_token_expires_at = ?, access_token_scopes = ?,"
    //        + " refresh_token_value = ?, refresh_token_issued_at = ?"
    //        + " WHERE " + PK_FILTER;
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

    //private static final String SAVE_AUTHORIZED_CLIENT_SQL = "INSERT INTO " + TABLE_NAME
    //        + " (" + COLUMN_NAMES + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    //private static final String COLUMN_NAMES = "client_registration_id, "
    //        + "principal_name, "
    //        + "access_token_type, "
    //        + "access_token_value, "
    //        + "access_token_issued_at, "
    //        + "access_token_expires_at, "
    //        + "access_token_scopes, "
    //        + "refresh_token_value, "
    //        + "refresh_token_issued_at";

    //INSERT INTO springboot.oauth2_authorized_client (client_registration_id, principal_name, access_token_type, access_token_value, access_token_issued_at, access_token_expires_at, access_token_scopes, refresh_token_value, refresh_token_issued_at, created_at, update_at)
    //VALUES ('my-client2', 'admin', 'Bearer',
    //                0x65794A68624763694F694A53557A49314E694973496E523563434936496B705856434A392E65794A66616E64304C57563464434936496B70585643446D69616E6C735A586B7636486D676138694C434A316332567958323568625755694F694A685A47317062694973496E4E6A6233426C496A7062496D467565534A644C434A665958563061473979615852705A584D694F6C7437496D463164476876636D6C3065534936496C4A5054455666595752746157346966563073496C393163325679626D46745A534936496D466B62576C75496977695A586877496A6F784E6A41314D7A49774E5441784C434A686458526F62334A7064476C6C6379493657794A53543078465832466B62576C75496C3073496D703061534936496A6B33597A51334E57566B4C54686D4E7A41744E474A6B5A4331684F54646A4C5464685A6A59784F5449345A5445344D434973496D4E7361575675644639705A434936496E4276633352745957346966512E697164594A41453133746C5A774E63745A574931786C71394C484364386C54544E4B44596C68327544734A314156634A62626C336B4642623336786F4668457533333451734F4379504E6569587867715F56555157514236416A51664D6C7079415137712D7663765A74464F34544451385747724D464E657546566D365F74755052743745537844674F5A56504442594837793230514B6F735648587A36566A72484C4A56435347636157344C353377424936497169334F65702D76346778326F364257757974626C63497473626C576D39585674475933535257486A5179626F75664B7136506D56454875506C64736C4A46336C5662334D464667696F67314F736873584771454D754E79306B306E37354F62717768764E5F5F78492D2D376458486976344F696D38392D5F58793843634C4D446B465964746C302D73654C6C55326F4F786C69692D414F54707774347343613577,
    //                '2020-11-13 22:21:41', '2020-11-14 10:21:40',
    //                'any',
    //                0x65794A68624763694F694A53557A49314E694973496E523563434936496B705856434A392E65794A66616E64304C57563464434936496B70585643446D69616E6C735A586B7636486D676138694C434A316332567958323568625755694F694A685A47317062694973496E4E6A6233426C496A7062496D467565534A644C434A6864476B694F6949354E324D304E7A566C5A4330345A6A63774C5452695A47517459546B3359793033595759324D546B794F4755784F4441694C434A665958563061473979615852705A584D694F6C7437496D463164476876636D6C3065534936496C4A5054455666595752746157346966563073496C393163325679626D46745A534936496D466B62576C75496977695A586877496A6F784E6A41334F4459354D7A41784C434A686458526F62334A7064476C6C6379493657794A53543078465832466B62576C75496C3073496D703061534936496A526B4E4455304D4745794C545A684E3249744E4755344F5330355A544A684C546B304D4455324F5455325A4745345A694973496D4E7361575675644639705A434936496E4276633352745957346966512E6A6A70627A7833664D386E4367316257475F58705F696B6541554935453433373969505773307A4A6B79494F743956775A424A78445350684D4654626D5A5A5846354E694B4E63623872307867457730304A4A656651386A56496577537836646A326835465377577A5A6D6A4E5A57794634336D626E63476B61727077556D5A6E77507749624B597239453158676B716F5835467A364F5F6865546F4C614B5853643564565A3346627531334B6A7134577775394275484F74516C567731496E364F5A73794356707045363952356F76365434595145764A6F372D4F4E634A4F365535343334703568433366523975795554753579477549574E644E6231794A504E684464572D39396A75395058795F4B4D5A54667049345F6F6F31734341593475497A5952724342526544796E51353547774B2D674E672D675263306D6C5A47484644686754517048716535543638617469315077, '2020-11-13 22:21:41', '2020-11-13 22:21:41', '2020-11-13 22:21:41');
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
