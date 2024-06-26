package com.hanqf.mongo.config;

import com.mongodb.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.SpringDataMongoDB;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.core.convert.*;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

import java.util.concurrent.TimeUnit;

/**
 * <h1></h1>
 * Created by hanqf on 2024/3/4 17:37.
 */

@Configuration
public class MongoConfig {

    @Value("${mongodb.pool.maxSize}")
    private int maxSize;

    @Value("${mongodb.pool.minSize}")
    private int minSize;

    @Value("${mongodb.pool.maxConnectionLifeTime}")
    private int maxConnectionLifeTime;

    @Value("${mongodb.pool.maxConnectionIdleTime}")
    private int maxConnectionIdleTime;

    @Value("${mongodb.pool.maxWaitTime}")
    private int maxWaitTime;


    /**
     * <h2>mongodb工厂，这里是为了加入连接池信息</h2>
     */
    @Bean
    public MongoDatabaseFactory getSimpleMongoClientDatabaseFactory(MongoProperties properties) {
        MongoClientSettings.Builder builder = MongoClientSettings.builder();
        builder.applyConnectionString(new ConnectionString(properties.getUri()));
        builder.applyToConnectionPoolSettings(b -> {
            b.maxSize(maxSize);
            b.minSize(minSize);
            b.maxConnectionLifeTime(maxConnectionLifeTime, TimeUnit.SECONDS);
            b.maxConnectionIdleTime(maxConnectionIdleTime, TimeUnit.MINUTES);
            b.maxWaitTime(maxWaitTime, TimeUnit.MILLISECONDS);
        });
        String database;
        if (properties.getUri() != null) {
            database = new ConnectionString(properties.getUri()).getDatabase();
        } else {
            database = properties.getDatabase();
        }
        MongoClient mongoClient = MongoClients.create(builder.build(), SpringDataMongoDB.driverInformation());
        return new SimpleMongoClientDatabaseFactory(mongoClient, database);

    }

    /**
     * 定制TypeMapper去掉_class属性
     *
     * @param mongoDatabaseFactory
     * @param context
     * @param conversions
     * @return
     */
    @Bean
    public MappingMongoConverter mappingMongoConverter(
            MongoDatabaseFactory mongoDatabaseFactory,
            MongoMappingContext context, MongoCustomConversions conversions) {

        DbRefResolver dbRefResolver = new DefaultDbRefResolver(mongoDatabaseFactory);
        MappingMongoConverter mappingMongoConverter =
                new MappingMongoConverter(dbRefResolver, context);
        mappingMongoConverter.setCustomConversions(conversions);

        //构造DefaultMongoTypeMapper，将typeKey设置为空值
        mappingMongoConverter.setTypeMapper(new DefaultMongoTypeMapper(null));

        return mappingMongoConverter;
    }

    /**
     * 配置事务管理器
     * 本方法用于创建并配置MongoDB的事务管理器，它基于给定的Mongo数据库工厂进行配置。
     *
     * @param factory MongoDB数据库工厂，用于创建数据库实例。
     * @return 返回配置好的MongoTransactionManager实例，用于事务管理。
     */
    @Bean
    public MongoTransactionManager transactionManager(MongoDatabaseFactory factory) {
        // 构建事务选项
        TransactionOptions txnOptions = TransactionOptions.builder()
                .readPreference(ReadPreference.primaryPreferred()) // 设置读取偏好为首选主节点
                .readConcern(ReadConcern.MAJORITY) // 设置读取关注为大多数
                .writeConcern(WriteConcern.MAJORITY) // 设置写入关注为大多数
                .build();
        // 基于给定的数据库工厂和事务选项，创建并返回事务管理器
        return new MongoTransactionManager(factory, txnOptions);
    }


}
