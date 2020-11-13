package com.example.oauth2authserverdemo.security.jwt;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import java.util.HashMap;
import java.util.Map;

/**
 * TokenEnhancer：在AuthorizationServerTokenServices 实现存储访问令牌之前增强访问令牌的策略。
 * 自定义TokenEnhancer的代码：把附加信息加入oAuth2AccessToken中
 *
 */
public class JWTokenEnhancer implements TokenEnhancer {

    /**
     * 重写enhance方法,将附加信息加入oAuth2AccessToken中
     * 这里只是提供附加信息的方式，没需要可以不配置这个类
     * @param oAuth2AccessToken
     * @param oAuth2Authentication
     * @return
     */
    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken oAuth2AccessToken, OAuth2Authentication oAuth2Authentication) {
        Map<String, Object> map = new HashMap<String, Object>();
        User user = (User) oAuth2Authentication.getUserAuthentication().getPrincipal();
        map.put("_username", user.getUsername());
        map.put("_authorities", user.getAuthorities());
        map.put("_jwt-ext", "JWT 扩展信息");
        ((DefaultOAuth2AccessToken) oAuth2AccessToken).setAdditionalInformation(map);
        return oAuth2AccessToken;
    }
}
