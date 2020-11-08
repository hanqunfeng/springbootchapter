package com.example.oauth2authserverdemo.security.jwt;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Jwt工具类
*/
@Data
@ConfigurationProperties(prefix = "jwt")
@Component
public class JwtTokenProperties {

    /**
     * 密钥
     */
    private String secret;
    /**
     * accessToken过期时间，单位秒
     */
    private Integer accessTokenValiditySeconds;

    /**
     * refreshToken过期时间，单位秒
     */
    private Integer refreshTokenValiditySeconds;

    /**
     * jks证书文件路径
     */
    private String jksKeyFile;
    /**
     * jks证书密码
     */
    private String jksKeyPassword;
    /**
     * jks证书别名
     */
    private String jksKeyAlias;



}
