package com.example.oauth2resourceserverdemo.config;


import com.example.oauth2resourceserverdemo.security.jwt.JwtTokenProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

/**
 * JwtTokenConfig配置类
 * 使用TokenStore将引入JwtTokenStore
 *
 * 注：Spring-Sceurity使用TokenEnhancer和JwtAccessConverter增强jwt令牌
 */
@Configuration
public class JwtTokenConfig {

    @Autowired
    private JwtTokenProperties jwtTokenProperties;

    /**
     * 同认证授权服务配置jwtTokenStore
     * @return
     */
    @Bean
    public TokenStore jwtTokenStore() {
        return new JwtTokenStore(jwtAccessTokenConverter());
    }


    /**
     * 同认证授权服务配置jwtAccessTokenConverter
     * 需要和认证授权服务设置的jwt签名相同
     * @return
     */
    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        JwtAccessTokenConverter accessTokenConverter = new JwtAccessTokenConverter();
        accessTokenConverter.setSigningKey(jwtTokenProperties.getSecret());
        accessTokenConverter.setVerifierKey(jwtTokenProperties.getSecret());
        return accessTokenConverter;
    }

}
