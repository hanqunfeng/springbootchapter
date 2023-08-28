package com.example.security.keys;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;

import java.security.Key;

/**
 * <h1>Sectet Key</h1>
 * Created by hanqf on 2023/8/21 11:47.
 */


public class SecretKeyUtil {

    /**
     * SECRET 是签名密钥，只生成一次即可，生成方法：
     * Key key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
     * String secretString = Encoders.BASE64.encode(key.getEncoded()); # 使用 BASE64 编码
     */
    private static String createSecret() {
        Key key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
        return Encoders.BASE64.encode(key.getEncoded()); //本文使用 BASE64 编码
    }

    public static void main(String[] args) {
        System.out.println(createSecret());
    }
}
