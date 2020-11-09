package com.example.jwtresourcesdemo.security;

import com.example.jwtresourcesdemo.utils.JksKeyUtil;
import com.example.jwtresourcesdemo.utils.RsaUtil;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Data;
import lombok.SneakyThrows;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * <h1></h1>
 * Created by hanqf on 2020/11/9 15:05.
 */

@Data
@ConfigurationProperties(prefix = "jwt")
@Component
public class JwtProperties {

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


    public SecretKey getSecretKey() {
        byte[] encodeKey = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(encodeKey);
    }

    @SneakyThrows
    public PublicKey rsaPublicKey(){
        return RsaUtil.getPublicKey(rsaPubKeyFile);
    }

    @SneakyThrows
    public PrivateKey rsaPrivateKey(){
        return RsaUtil.getPrivateKey(rsaPriKeyFile);
    }

    @SneakyThrows
    public PublicKey jksPublicKey(){
        if(StringUtils.hasText(jksPubKeyFile)){
            return JksKeyUtil.getPublicKey(jksPubKeyFile);
        }else {
            System.out.println("jksPublicKey_format: " + jksKeyPair().getPublic().getFormat());
            System.out.println("jksPublicKey_Algorithm: " + jksKeyPair().getPublic().getAlgorithm());
            return jksKeyPair().getPublic();
        }
    }

    public PrivateKey jksPrivateKey(){
        System.out.println("jksPrivateKey_format: " + jksKeyPair().getPrivate().getFormat());
        System.out.println("jksPrivateKey_Algorithm: " + jksKeyPair().getPrivate().getAlgorithm());
        return jksKeyPair().getPrivate();
    }

    public KeyPair jksKeyPair(){
        return JksKeyUtil.getKeyPair(jksFile,jksAlias,jksStorePass,jksKeyPass);
    }

}
