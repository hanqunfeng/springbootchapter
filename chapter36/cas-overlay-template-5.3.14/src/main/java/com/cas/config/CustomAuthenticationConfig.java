package com.cas.config;

import com.cas.security.CustomerHandlerAuthentication;
import org.apereo.cas.authentication.AuthenticationEventExecutionPlan;
import org.apereo.cas.authentication.AuthenticationEventExecutionPlanConfigurer;
import org.apereo.cas.authentication.AuthenticationHandler;
import org.apereo.cas.authentication.principal.DefaultPrincipalFactory;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.services.ServicesManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <p></p>
 * Created by hanqf on 2020/9/22 16:06.
 */

@Configuration("CustomAuthenticationConfig")
@EnableConfigurationProperties(CasConfigurationProperties.class)
public class CustomAuthenticationConfig implements AuthenticationEventExecutionPlanConfigurer {


    @Autowired
    @Qualifier("servicesManager")
    private ServicesManager servicesManager;

    @Bean
    public AuthenticationHandler myAuthenticationHandler() {
        // 参数: name, servicesManager, principalFactory, order
        // 定义为优先使用它进行认证，但是自定义属性认证失败时还是会接着使用默认的处理器接着验证，就导致了验证通过，因为默认只验证用户名和密码
        // 所以配置文件中不要开启其它认证方式
        //return new CustomUsernamePasswordAuthentication(CustomUsernamePasswordAuthentication.class.getName(),
        //        servicesManager, new DefaultPrincipalFactory(), 1);

        return new CustomerHandlerAuthentication(CustomerHandlerAuthentication.class.getName(),
                servicesManager, new DefaultPrincipalFactory(), 1);
    }

    @Override
    public void configureAuthenticationExecutionPlan(final AuthenticationEventExecutionPlan plan) {
        plan.registerAuthenticationHandler(myAuthenticationHandler());
    }
}
