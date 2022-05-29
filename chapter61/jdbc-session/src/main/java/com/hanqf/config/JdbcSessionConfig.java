package com.hanqf.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.session.jdbc.config.annotation.web.http.EnableJdbcHttpSession;

/**
 * <h1>jdbc-session</h1>
 * Created by hanqf on 2022/5/28 00:28.
 */

@Configuration
@EnableJdbcHttpSession
public class JdbcSessionConfig {
}
