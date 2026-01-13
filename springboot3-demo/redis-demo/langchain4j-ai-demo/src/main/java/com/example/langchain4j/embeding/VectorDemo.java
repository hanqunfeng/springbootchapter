package com.example.langchain4j.embeding;

import com.example.langchain4j.ModelUtil;
import dev.langchain4j.community.model.dashscope.QwenEmbeddingModel;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.output.Response;
import org.apache.lucene.util.VectorUtil;

/**
 * 向量demo
 * Created by hanqf on 2026/1/13 09:51.
 */


public class VectorDemo {
    public static void main(String[] args) {
        EmbeddingModel embeddingModel = QwenEmbeddingModel.builder()
                .apiKey(ModelUtil.DASHSCOPE_API_KEY)
                .modelName("text-embedding-v3")
                .build();

        String question = "Redis 7 如何开启向量检索功能？";

        String[] texts = {
                "Redis 需要安装 RediSearch 模块才能支持向量索引和相似度搜索。",
                "Redis 7.4 可以通过 MODULE LOAD 加载向量模块。",
                "MySQL 如何建立索引提升查询性能？",
                "Spring Boot 如何配置 RedisTemplate 连接集群？"
        };

        Response<Embedding> embed = embeddingModel.embed(question);
        float[] q_vector = embed.content().vector();
        System.out.println(embed.content().vectorAsList());
        System.out.println(embed.content().vector().length);

        for (String s : texts) {
            Response<Embedding> embedded = embeddingModel.embed(s);
            float[] vector = embedded.content().vector();
            // 计算余弦相似度, 范围[0,1],越大表示越相似
            final float cosine = VectorUtil.cosine(q_vector, vector);
            System.out.println(cosine);
        }

    }


}
