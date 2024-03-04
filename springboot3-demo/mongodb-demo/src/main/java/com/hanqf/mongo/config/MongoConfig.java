package com.hanqf.mongo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.convert.*;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

/**
 * <h1></h1>
 * Created by hanqf on 2024/3/4 17:37.
 */

@Configuration
public class MongoConfig {

    /**
     * 定制TypeMapper去掉_class属性
     *
     * @param mongoDatabaseFactory
     * @param context
     * @param conversions
     * @return
     */
    @Bean
    MappingMongoConverter mappingMongoConverter(
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
}
