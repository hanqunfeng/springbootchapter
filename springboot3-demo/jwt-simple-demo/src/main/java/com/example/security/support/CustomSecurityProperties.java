package com.example.security.support;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * <h1>CustomSecurityProperties</h1>
 * Created by hanqf on 2023/8/21 14:56.
 */


@Data
@ConfigurationProperties(prefix = "security")
@Component
public class CustomSecurityProperties {
    private String[] permitAll;
}
