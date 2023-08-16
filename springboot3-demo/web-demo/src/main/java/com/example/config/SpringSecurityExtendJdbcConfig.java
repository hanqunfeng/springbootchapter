package com.example.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.session.jdbc.JdbcIndexedSessionRepository;
import org.springframework.session.jdbc.config.annotation.web.http.EnableJdbcHttpSession;
import org.springframework.session.security.SpringSessionBackedSessionRegistry;

import javax.sql.DataSource;

/**
 * <h1>jdbc-remember-me-config</h1>
 * Created by hanqf on 2023/8/4 15:33.
 */

@Slf4j
@Configuration
@EnableJdbcHttpSession
public class SpringSecurityExtendJdbcConfig {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcIndexedSessionRepository sessionRepository;

    /**
     * RemeberMe
     * 配置从数据库中获取token
     *
     * @return CREATE TABLE `persistent_logins` (
     * `username` varchar(64) NOT NULL,
     * `series` varchar(64) NOT NULL,
     * `token` varchar(64) NOT NULL,
     * `last_used` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
     * PRIMARY KEY (`series`)
     * ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
     */
    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        log.info("JdbcTokenRepositoryImpl");
        JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
        //自动创建表
        tokenRepository.setCreateTableOnStartup(false);
        tokenRepository.setDataSource(dataSource);
        return tokenRepository;
    }


    @Bean
    public SessionRegistry springSessionBackedSessionRegistry() {
        log.info("JdbcSessionRegistry");
        return new SpringSessionBackedSessionRegistry<>(sessionRepository);
    }
}
