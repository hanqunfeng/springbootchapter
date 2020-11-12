package com.example.oauth2authserverdemo.controller;

import com.example.oauth2authserverdemo.security.jwt.JwtTokenProperties;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.endpoint.FrameworkEndpoint;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.KeyPair;
import java.security.interfaces.RSAPublicKey;
import java.util.Map;

/**
 * <h1>JwkSetEndpoint</h1>
 * Created by hanqf on 2020/11/12 09:42.
 */


@FrameworkEndpoint //@FrameworkEndpoint和@Controller相同功能，只用于框架提供的端点
public class JwkSetEndpoint {

    @Autowired
    private JwtTokenProperties jwtTokenProperties;


    @GetMapping("/.well-known/jwks.json")
    @ResponseBody
    public Map<String, Object> getKey() {
        KeyStoreKeyFactory keyStoreKeyFactory =
                new KeyStoreKeyFactory(jwtTokenProperties.getJksKeyFileResource(), jwtTokenProperties.getJksStorePassword().toCharArray());
        KeyPair keyPair = keyStoreKeyFactory.getKeyPair(jwtTokenProperties.getJksKeyAlias(), jwtTokenProperties.getJksKeyPassword().toCharArray());
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAKey key = new RSAKey.Builder(publicKey).build();
        return new JWKSet(key).toJSONObject();
    }

}


