package com.example.oauth2authserverdemo.security.jwt;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.nio.file.Paths;

/**
 * Jwt工具类
*/
@Data
@ConfigurationProperties(prefix = "jwt")
@Component
public class JwtTokenProperties {

    /**
     * 密钥类型:secret，jks
     */
    private String type;

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
     * jks证书密钥库密码
    */
    private String jksStorePassword;
    /**
     * jks证书密码
     */
    private String jksKeyPassword;
    /**
     * jks证书别名
     */
    private String jksKeyAlias;


    /**
     * 获取jks证书Resource
    */
    public Resource getJksKeyFileResource(){
        Resource resource;
        if (jksKeyFile.startsWith("classpath:")) {
            jksKeyFile = jksKeyFile.replace("classpath:", "");
            resource = new ClassPathResource(jksKeyFile);
        } else {
            resource = new PathResource(Paths.get(jksKeyFile));
        }
        return resource;
    }



}
