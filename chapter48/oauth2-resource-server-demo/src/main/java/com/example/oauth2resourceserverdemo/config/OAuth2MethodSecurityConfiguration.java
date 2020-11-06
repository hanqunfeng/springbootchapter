package com.example.oauth2resourceserverdemo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.security.oauth2.provider.expression.OAuth2MethodSecurityExpressionHandler;

/**
 * <h1>OAuth2MethodSecurityConfiguration</h1>
 * Created by hanqf on 2020/11/7 00:28.
 *
 * 支持oauth2表达式 @PreAuthorize("#oauth2.hasScope('all')")
 * 实际上不使用这个类也可以正常使用，只不过这个类异常提示信息更准确一些
 */

@Configuration
//注意使用这个类时，@EnableGlobalMethodSecurity要配置到这里
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class OAuth2MethodSecurityConfiguration extends GlobalMethodSecurityConfiguration {

    @Override
    protected MethodSecurityExpressionHandler createExpressionHandler() {
        return new OAuth2MethodSecurityExpressionHandler();
    }
}