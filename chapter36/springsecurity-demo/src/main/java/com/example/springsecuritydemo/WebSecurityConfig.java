package com.example.springsecuritydemo;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * <p></p>
 * Created by hanqf on 2020/8/10 10:42.
 */

//@Configuration
//@EnableWebSecurity
////开启方法级别的安全验证注解，参考 https://www.jianshu.com/p/41b7c3fb00e0
//@EnableGlobalMethodSecurity(prePostEnabled =true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // 测试时，可以直接用下面的方式
        // User.UserBuilder builder = User.withDefaultPasswordEncoder();
        User.UserBuilder builder = User.builder().passwordEncoder(passwordEncoder()::encode);
        auth.inMemoryAuthentication().withUser(builder.username("admin").password("123456").roles("admin").build());
        auth.inMemoryAuthentication().withUser(builder.username("guest").password("123456").roles("guest").build());
    }
}
