package com.hanqf.demo.config;

import org.apache.shardingsphere.driver.api.yaml.YamlShardingSphereDataSourceFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

/**
 * DataSourceConfig
 * Created by hanqf on 2025/9/2 10:39.
 */


@Configuration
public class DataSourceConfig {

    @Bean(name = "shardingDataSource")
    public DataSource shardingDataSource() throws SQLException, IOException {
        // ShardingSphere 提供的工厂方法，根据配置构建 DataSource
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("sharding.yaml")) {
            if (inputStream == null) {
                throw new IllegalStateException("Cannot find sharding.yaml in classpath");
            }
            byte[] yamlBytes = inputStream.readAllBytes();
            return YamlShardingSphereDataSourceFactory.createDataSource(yamlBytes);
        }
    }

}
