package com.hanqf.config;

import io.milvus.client.MilvusServiceClient;
import io.milvus.param.IndexType;
import io.milvus.param.MetricType;
import org.springframework.ai.autoconfigure.vectorstore.milvus.MilvusServiceClientProperties;
import org.springframework.ai.autoconfigure.vectorstore.milvus.MilvusVectorStoreProperties;
import org.springframework.ai.embedding.EmbeddingClient;
import org.springframework.ai.vectorstore.MilvusVectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <h1>MilvusVectorStoreAutoConfiguration</h1>
 * Created by hanqf on 2024/5/22 10:24.
 */

@Configuration
public class MilvusVectorStoreConfig {

    @Bean
    public MilvusVectorStore vectorStore(MilvusServiceClient milvusClient, EmbeddingClient embeddingClient,
                                         MilvusVectorStoreProperties properties, MilvusServiceClientProperties clientProperties) {

        // 这里使用个小技巧，如果是ssl连接，则认为是cloud，不设置数据库名称
        String databaseName = properties.getDatabaseName();
        if (clientProperties.isSecure()) {
            databaseName = null;
        }

        MilvusVectorStore.MilvusVectorStoreConfig config = MilvusVectorStore.MilvusVectorStoreConfig.builder()
                .withCollectionName(properties.getCollectionName())
                .withDatabaseName(databaseName)
                .withIndexType(IndexType.valueOf(properties.getIndexType().name()))
                .withMetricType(MetricType.valueOf(properties.getMetricType().name()))
                .withIndexParameters(properties.getIndexParameters())
                .withEmbeddingDimension(properties.getEmbeddingDimension())
                .build();

        return new MilvusVectorStore(milvusClient, embeddingClient, config);
    }

//    @Bean
//    public MilvusServiceClient milvusClient(MilvusVectorStoreProperties serverProperties,
//                                            MilvusServiceClientProperties clientProperties) {
//
//        var builder = ConnectParam.newBuilder()
//                .withDatabaseName(serverProperties.getDatabaseName())
//                .withUri(clientProperties.getUri())
//                .withToken(clientProperties.getToken());
//
//        return new MilvusServiceClient(builder.build());
//    }
}
