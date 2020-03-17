package com.example.config;/**
 * Created by hanqf on 2020/3/17 14:51.
 */


import com.mongodb.ConnectionString;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDbFactory;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * @author hanqf
 * @date 2020/3/17 14:51
 */
@Configuration
@EnableMongoRepositories(basePackages = "com.example.dao.two",
        mongoTemplateRef = "twoMongoTemplate")
public class MongoConfigTwo {

    @Bean
    @ConfigurationProperties(prefix = "spring.data.mongodb.two")
    public MongoProperties twoMongoProperties() {
        return new MongoProperties();
    }

    @Bean(name = "twoMongoTemplate")
    public MongoTemplate twoMongoTemplate() {
        return new MongoTemplate(twoFactory());
    }

    @Bean
    public MongoDbFactory twoFactory() {
        return new SimpleMongoClientDbFactory(new ConnectionString(twoMongoProperties().getUri()));
    }
}
