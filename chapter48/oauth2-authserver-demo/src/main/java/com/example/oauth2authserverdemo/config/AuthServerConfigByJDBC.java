package com.example.oauth2authserverdemo.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;

import javax.sql.DataSource;

/**
 * <h1>jdbc配置</h1>
 * Created by hanqf on 2020/11/5 16:15.
 */
@Slf4j
@Configuration
@EnableAuthorizationServer
@ConditionalOnProperty(prefix = "oauth2.clients.config", name = "jdbc", havingValue = "true")
public class AuthServerConfigByJDBC extends AuthServerConfig {

    @Autowired
    private DataSource dataSource;

    /**
     * 配置OAuth2的客户端信息：clientId、client_secret、authorization_type、redirect_url等。
     *
     * @param clients
     * @throws Exception
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        //方式1 也可以自定义ClientDetailsService的实现类
        //JdbcClientDetailsService detailsService = new JdbcClientDetailsService(dataSource);
        //detailsService.setPasswordEncoder(passwordEncoder);
        //clients.withClientDetails(detailsService);

        //方式2 等价于方式1
        clients.jdbc(dataSource)
                //指定密码的加密算法
                .passwordEncoder(passwordEncoder);
        log.info("OAuth2的client信息基于数据库");
    }
}
