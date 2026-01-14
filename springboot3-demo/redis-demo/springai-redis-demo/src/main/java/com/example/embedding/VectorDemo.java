package com.example.embedding;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.embedding.DashScopeEmbeddingModel;
import com.alibaba.cloud.ai.dashscope.embedding.DashScopeEmbeddingOptions;
import com.alibaba.cloud.ai.dashscope.spec.DashScopeModel;
import com.example.util.EmbeddingUtil;
import org.apache.lucene.util.VectorUtil;
import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.embedding.EmbeddingModel;

/**
 * 向量demo
 * Created by hanqf on 2026/1/13 09:51.
 */


public class VectorDemo {

    // 初始化 ChatModel
    private static final DashScopeApi dashScopeApi = DashScopeApi.builder()
            .apiKey(System.getenv("DASHSCOPE_API_KEY"))
            .build();

    private static final DashScopeEmbeddingOptions options = DashScopeEmbeddingOptions.builder()
//            .model(DashScopeApi.DEFAULT_EMBEDDING_MODEL) // 默认是text-embedding-v2
            .model(DashScopeModel.EmbeddingModel.EMBEDDING_V3.getValue())
            .textType(DashScopeApi.DEFAULT_EMBEDDING_TEXT_TYPE)
            .build();

    // 创建 EmbeddingModel
    private static final EmbeddingModel embeddingModel = new DashScopeEmbeddingModel(dashScopeApi, MetadataMode.EMBED, options);


    public static void main(String[] args) {
        String question = "Redis 7 如何开启向量检索功能？";

        String[] texts = {
                "Redis 需要安装 RediSearch 模块才能支持向量索引和相似度搜索。",
                "Redis 7.4 可以通过 MODULE LOAD 加载向量模块。",
                "MySQL 如何建立索引提升查询性能？",
                "Spring Boot 如何配置 RedisTemplate 连接集群？"
        };

        final float[] q_vector = embeddingModel.embed(question);
        System.out.println(EmbeddingUtil.vectorAsList(q_vector));
        System.out.println(q_vector.length);

        for (String s : texts) {
            float[] vector = embeddingModel.embed(s);
            // 计算余弦相似度, 范围[0,1],越大表示越相似
            final float cosine = VectorUtil.cosine(q_vector, vector);
            System.out.println(cosine);
        }
    }
}
