package com.example.springsecuritydemo.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;

/**
 * <p>内存用户配置</p>
 * Created by hanqf on 2020/9/9 13:54.
 */

@ConditionalOnProperty(prefix = "security",name = "config",havingValue = "memory")
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfigByMemory extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // 测试时，可以直接用下面的方式
        // 内存用户的一般创建方式，不过这种方式不支持rememberMe
        User.UserBuilder builder = User.builder().passwordEncoder(PasswordEncoderFactories.createDelegatingPasswordEncoder()::encode);
        auth.inMemoryAuthentication().withUser(builder.username("admin").password("1234567").roles("admin").build());
        auth.inMemoryAuthentication().withUser(builder.username("guest").password("1234567").roles("guest").build());
    }
}
