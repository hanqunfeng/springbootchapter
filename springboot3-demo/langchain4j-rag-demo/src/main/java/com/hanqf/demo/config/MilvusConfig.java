package com.hanqf.demo.config;

import com.hanqf.demo.milvus.MilvusServiceClientProperties;
import com.hanqf.demo.milvus.MilvusVectorStoreProperties;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.milvus.MilvusEmbeddingStore;
import io.milvus.param.IndexType;
import io.milvus.param.MetricType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <h1></h1>
 * Created by hanqf on 2024/6/18 11:25.
 */

@Configuration
public class MilvusConfig {

    @Autowired
    private MilvusVectorStoreProperties storeProperties;
    @Autowired
    private MilvusServiceClientProperties clientProperties;

    @Bean
    public EmbeddingStore<TextSegment> embeddingStore() {
        return MilvusEmbeddingStore.builder()
                .host(clientProperties.getHost())
                .port(clientProperties.getPort())
                .username(clientProperties.getUsername())
                .password(clientProperties.getPassword())
                .databaseName(storeProperties.getDatabaseName())
                .collectionName(storeProperties.getCollectionName())
                .dimension(storeProperties.getEmbeddingDimension())
                .indexType(IndexType.IVF_FLAT) //设置这个会导致创建索引失败，需要手工创建索引
                .metricType(MetricType.COSINE)
                .build();
    }
}
