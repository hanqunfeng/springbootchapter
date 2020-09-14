package com.cas.config;/**
 * Created by hanqf on 2020/3/5 16:09.
 */


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

/**
 * @author hanqf
 * @date 2020/3/5 16:09
 */
@Configuration
//加载资源文件
@PropertySource({"classpath:application.properties"})
//开启spring-boot对配置依赖的支持，如@ConfigurationProperties
@EnableConfigurationProperties
public class DataSourceConfig {
    private static final Logger logger = LoggerFactory.getLogger(DataSourceConfig.class);
    /*
     * 绑定资源属性
     */
    @Value("${jdbc.driver}")
    String driverClass;
    @Value("${jdbc.url}")
    String url;
    @Value("${jdbc.username}")
    String userName;
    @Value("${jdbc.password}")
    String passWord;

    @Bean(name = "dataSource")
    public DataSource dataSource() {
        logger.info("DataSource");
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driverClass);
        dataSource.setUrl(url);
        dataSource.setUsername(userName);
        dataSource.setPassword(passWord);
        return dataSource;
    }


    @Bean(name="jdbcTemplate")
    public JdbcTemplate jdbcTemplate(@Autowired DataSource dataSource){
        return new JdbcTemplate(dataSource);
    }

}

