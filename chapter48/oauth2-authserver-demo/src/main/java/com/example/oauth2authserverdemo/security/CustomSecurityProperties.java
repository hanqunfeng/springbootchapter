package com.example.oauth2authserverdemo.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * <h1>securityproterties</h1>
 * Created by hanqf on 2020/11/4 09:32.
 */

@Data
@ConfigurationProperties(prefix = "security")
@Component
public class CustomSecurityProperties {
    /**
     * 不需要验证的路径数组
    */
    private String[] permitAll;
    /**
     * 不需要拦截的路径数组
    */
    private String[] ignoring;
}
