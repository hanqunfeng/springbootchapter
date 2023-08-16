package com.example.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.authentication.rememberme.InMemoryTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.session.HttpSessionEventPublisher;

/**
 * <h1>security扩展基于内存</h1>
 * Created by hanqf on 2023/8/11 15:03.
 */


@Slf4j
//@Configuration
public class SpringSecurityExtendCommonConfig {

    /**
     * RemeberMe 内存
     */
    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        log.info("InMemoryTokenRepositoryImpl");
        return new InMemoryTokenRepositoryImpl();
    }

    /**
     * 用于跟踪用户的会话信息，包括已经认证的用户和它们的会话（Session）。
     * 每当用户成功进行身份认证并建立了一个新的会话时，SessionRegistry将负责将该会话添加到其内部的数据结构中。
     */
    @Bean
    public SessionRegistry sessionRegistry() {
        log.info("CommonSessionRegistry");
        return new SessionRegistryImpl();
    }

    /**
     * session事件发布者
     * 如果使用SpringSessionBackedSessionRegistry，这里就不需要HttpSessionEventPublisher的Bean，而是交由SpringSession来管理
     */
    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        log.info("HttpSessionEventPublisher");
        return new HttpSessionEventPublisher();
    }
}
