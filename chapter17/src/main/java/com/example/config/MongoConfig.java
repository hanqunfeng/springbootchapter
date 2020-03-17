package com.example.config;/**
 * Created by hanqf on 2020/3/17 16:38.
 */


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.MongoTransactionManager;

/**
 * @author hanqf
 * @date 2020/3/17 16:38
 */
@Configuration
public class MongoConfig {

    //1：MongoDB的版本必须是4.0
    //2.MongoDB事务功能必须是在多副本集的情况下才能使用，否则报错"Sessions are not supported by the MongoDB cluster to which this client is connected"，4.2版本会支持分片事务。
    //3.事务控制只能用在已存在的集合中，也就是集合需要手工添加不会由jpa创建会报错"Cannot create namespace glcloud.test_user in multi-document transaction."
    @Bean(name = "mongoTransactionManager")
    MongoTransactionManager transactionManager(MongoDbFactory factory) {
        return new MongoTransactionManager(factory);
    }
}
