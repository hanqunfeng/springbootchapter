package com.example.security;


import com.example.security.keys.JksKey;
import com.example.security.keys.RsaKey;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * <h1>jwt属性文件类</h1>
 * Created by hanqf on 2023/8/21 11:08.
 */

@Slf4j
@Data
@ConfigurationProperties(prefix = "jwt")
@Component
public class JwtProperties {

    /**
     * 密钥类型，可选值为jks,rsa,secret，默认secret
     */
    private String type = "secret";

    /**
     * 密钥
     */
    private String secret;
    /**
     * 过期时间，单位毫秒
     */
    private Long expiration;
    /**
     * 请求头参数名称
     */
    private String header;

    /**
     * RSA公钥文件路径
     */
    private String rsaPubKeyFile;

    /**
     * RSA私钥文件路径
     */
    private String rsaPriKeyFile;

    /**
     * jks证书路径
     */
    private String jksFile;
    /**
     * jks证书别名
     */
    private String jksAlias;
    /**
     * jks密钥库密码
     */
    private String jksStorePass;
    /**
     * jks密钥密码
     */
    private String jksKeyPass;
    /**
     * jks公钥文件路径
     */
    private String jksPubKeyFile;


    /**
     * 密钥对象
     */
    private SecretKey secretKey;

    /**
     * rsa公钥对象
     */
    private PublicKey rasPublicKey;

    /**
     * rsa私钥对象
     */
    private PrivateKey rsaPrivateKey;


    /**
     * jks公钥对象
     */
    private PublicKey jksPublicKey;

    /**
     * jks私钥对象
     */
    private PrivateKey jksPrivateKey;

    /**
     * jks密钥对
     */
    private KeyPair jksKeyPair;

    @PostConstruct
    public void postConstruct(){
        log.info("jwt_type == " + type);
    }


    public SecretKey getSecretKey() {
        if (secretKey == null) {
            byte[] encodeKey = Decoders.BASE64.decode(secret);
            secretKey = Keys.hmacShaKeyFor(encodeKey);
        }
        return secretKey;
    }

    @SneakyThrows
    public PublicKey rsaPublicKey() {
        if (rasPublicKey == null) {
            rasPublicKey = RsaKey.getPublicKey(rsaPubKeyFile);
        }
        return rasPublicKey;
    }

    @SneakyThrows
    public PrivateKey rsaPrivateKey() {
        if (rsaPrivateKey == null) {
            rsaPrivateKey = RsaKey.getPrivateKey(rsaPriKeyFile);
        }
        return rsaPrivateKey;
    }

    @SneakyThrows
    public PublicKey jksPublicKey() {
        if (jksPublicKey == null) {
            if (StringUtils.hasText(jksPubKeyFile)) {
                jksPublicKey = JksKey.getPublicKey(jksPubKeyFile);
            } else {
//                System.out.println("jksPublicKey_format: " + jksKeyPair().getPublic().getFormat());
//                System.out.println("jksPublicKey_Algorithm: " + jksKeyPair().getPublic().getAlgorithm());
                jksPublicKey = jksKeyPair().getPublic();
            }
        }
        return jksPublicKey;
    }

    public PrivateKey jksPrivateKey() {
        if (jksPrivateKey == null) {
//            System.out.println("jksPrivateKey_format: " + jksKeyPair().getPrivate().getFormat());
//            System.out.println("jksPrivateKey_Algorithm: " + jksKeyPair().getPrivate().getAlgorithm());
            jksPrivateKey = jksKeyPair().getPrivate();
        }
        return jksPrivateKey;
    }

    public KeyPair jksKeyPair() {
        if (jksKeyPair == null) {
            jksKeyPair = JksKey.getKeyPair(jksFile, jksAlias, jksStorePass, jksKeyPass);
        }
        return jksKeyPair;
    }

}
