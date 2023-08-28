package com.example.config;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.security.oauth2.client.JdbcOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

import javax.sql.DataSource;

/**
 * ${DESCRIPTION}
 * Created by hanqf on 2018/9/5 16:18.
 */

@Configuration
@Slf4j
public class DataSourceConfig {

    /**
     * 需要依据属性引入数据源，否则不能正常加载druid
    */
    @Primary
    @Bean(name = "dataSource")
    @ConfigurationProperties("spring.datasource.druid")
    public DataSource dataSource(){
        log.info("spring.datasource.druid init");
        return DruidDataSourceBuilder.create().build();
    }

    /**
     * 这里创建基于数据库的客户端信息配置 JdbcOAuth2AuthorizedClientService ，用户登录认证后的token数据会保存到数据表中
     * 它将OAuth2客户端的访问令牌和刷新令牌存储到数据库中，并将clientId和用户名等信息作为索引存储到内存中
     * 需要现在数据库中创建 oauth2_authorized_client 表
     *
     *
     * 默认是基于 内存的 InMemoryOAuth2AuthorizedClientService
     */
    @Bean
    public OAuth2AuthorizedClientService authorizedClientService(JdbcOperations jdbcOperations, ClientRegistrationRepository clientRegistrationRepository) {
        return new JdbcOAuth2AuthorizedClientService(jdbcOperations, clientRegistrationRepository);
    }



}
