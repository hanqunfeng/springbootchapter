package com.example.jwtresourcewebfluxdemo.security;

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
    private String[] permitAll;
    private String[] ignoring;
}
