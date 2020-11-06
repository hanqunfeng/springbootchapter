package com.example.oauth2authserverdemo.config;

import com.example.oauth2authserverdemo.security.jwt.JwtTokenProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;

/**
 * <h1>内存配置</h1>
 * Created by hanqf on 2020/11/5 16:15.
 */

@Configuration
@EnableAuthorizationServer
@ConditionalOnProperty(prefix = "oauth2.clients.config",name = "jdbc",havingValue = "false",matchIfMissing = true)
@Slf4j
public class AuthServerConfigByMemory extends AuthServerConfig{

    @Autowired
    private JwtTokenProperties jwtTokenProperties;
    /**
     * 配置OAuth2的客户端信息：clientId、client_secret、authorization_type、redirect_url等。
     *
     * @param clients
     * @throws Exception
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                .withClient("postman")
                .secret(passwordEncoder.encode("postman"))
                .scopes("any","all") //指定用户的访问范围
                //.autoApprove(true) //设置true跳过用户确认授权操作页面直接同意，默认false，必须设置scopes
                .autoApprove("any") //指定客户端访问哪些范围时，跳过用户确认授权操作页面直接同意，必须设置scopes，请求参数必须加上&scope=any
                .authorizedGrantTypes("password", "authorization_code", "refresh_token","implicit","client_credentials")
                .redirectUris("http://localhost:8080/redirect")
                .accessTokenValiditySeconds(jwtTokenProperties.getAccessTokenValiditySeconds()) //默认12小时
                .refreshTokenValiditySeconds(jwtTokenProperties.getRefreshTokenValiditySeconds()) //默认30天
                .and()
                .withClient("demo-client")
                .secret(passwordEncoder.encode("demo-client"))
                .scopes("any")
                .authorizedGrantTypes("password", "authorization_code", "refresh_token","implicit")
                .redirectUris("http://localhost:8080/redirect");
        log.info("OAuth2的client信息基于内存");
    }
}
