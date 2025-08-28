package com.hanqf.config;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

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



}
