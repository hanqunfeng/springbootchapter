package com.hanqf.config;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.weaviate.WeaviateEmbeddingStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <h1></h1>
 * Created by hanqf on 2024/6/18 11:25.
 */

@Configuration
public class WeaviateConfig {


    @Bean
    public EmbeddingStore<TextSegment> embeddingStore() {
        return WeaviateEmbeddingStore.builder()
                .host("10.10.2.45")
                .port(8080)
                .avoidDups(true)
                .scheme("http")
                .consistencyLevel("ONE")
                .build();
    }
}
