package com.example.oauth2authserverdemo.config;

import com.example.oauth2authserverdemo.security.jwt.JWTokenEnhancer;
import com.example.oauth2authserverdemo.security.jwt.JwtTokenProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

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

    @Bean
    public TokenStore jwtTokenStore() {
        return new JwtTokenStore(jwtAccessTokenConverter());
    }

    /**
     * JwtAccessTokenConverter：TokenEnhancer的子类,帮助程序在JWT编码的令牌值和OAuth身份验证信息之间进行转换(在两个方向上),同时充当TokenEnhancer授予令牌的时间。
     * 自定义的JwtAccessTokenConverter：把自己设置的jwt签名加入accessTokenConverter中(这里设置'demo',项目可将此在配置文件设置)
     * @return
     */
    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        JwtAccessTokenConverter accessTokenConverter = new JwtAccessTokenConverter();
        //对称加密
        //accessTokenConverter.setSigningKey(jwtTokenProperties.getSecret());
        //accessTokenConverter.setVerifierKey(jwtTokenProperties.getSecret());

        //非对称加密
        KeyStoreKeyFactory keyStoreKeyFactory =
                new KeyStoreKeyFactory(new ClassPathResource(jwtTokenProperties.getJksKeyFile()), jwtTokenProperties.getJksKeyPassword().toCharArray());
        accessTokenConverter.setKeyPair(keyStoreKeyFactory.getKeyPair(jwtTokenProperties.getJksKeyAlias()));

        return accessTokenConverter;
    }

    /**
     * 引入自定义JWTokenEnhancer:
     * 自定义JWTokenEnhancer实现TokenEnhancer并重写enhance方法,将附加信息加入oAuth2AccessToken中
     * @return
     */
    @Bean
    public TokenEnhancer jwtTokenEnhancer(){
       return new JWTokenEnhancer();
    }
}
