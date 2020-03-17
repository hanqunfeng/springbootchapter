package com.example.config;/**
 * Created by hanqf on 2020/3/17 14:51.
 */


import com.mongodb.ConnectionString;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDbFactory;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * @author hanqf
 * @date 2020/3/17 14:51
 */
@Configuration
@EnableMongoRepositories(basePackages = "com.example.dao.one",
        mongoTemplateRef = "oneMongoTemplate")
public class MongoConfigOne {

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "spring.data.mongodb.one")
    public MongoProperties oneMongoProperties() {
        return new MongoProperties();
    }

    @Primary
    @Bean(name = "oneMongoTemplate")
    public MongoTemplate oneMongoTemplate() {
        return new MongoTemplate(oneFactory());
    }

    @Bean
    @Primary
    public MongoDbFactory oneFactory() {
        return new SimpleMongoClientDbFactory(new ConnectionString(oneMongoProperties().getUri()));
    }
}
