package com.example.oauth2resourceserverdemo.security.jwt;

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

}
