package com.example.langchain4j.embeding;

import com.example.langchain4j.ModelUtil;
import dev.langchain4j.community.model.dashscope.QwenEmbeddingModel;
import dev.langchain4j.community.store.embedding.redis.RedisEmbeddingStore;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingStore;
import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.UnifiedJedis;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * 向量demo
 * Created by hanqf on 2026/1/13 09:51.
 */


public class RedisVectorDemo {

    // EmbeddingModel
    private static final EmbeddingModel embeddingModel = QwenEmbeddingModel.builder()
            .apiKey(ModelUtil.DASHSCOPE_API_KEY)
            .modelName("text-embedding-v3")
            .build();

    // Redis连接信息
    private static final DefaultJedisClientConfig jedisClientConfig = DefaultJedisClientConfig.builder()
            .user("admin")
            .password("123456")
            .build();
    private static final HostAndPort hostAndPort = new HostAndPort("127.0.0.1", 6379);

    // EmbeddingStore 这里是Redis向量数据库
    private static final EmbeddingStore<TextSegment> embeddingStore = RedisEmbeddingStore.builder()
            .unifiedJedis(new UnifiedJedis(hostAndPort, jedisClientConfig))
            .dimension(1024) // 模型返回的维度
            .indexName("redis-vector-index") // 索引名称，默认 embedding-index
            .prefix("redis-vector-embedding:") // 索引前缀，默认 embedding:
            .build();

    public static void main(String[] args) {

        // 读取 rag.txt 并插入到 Redis
        insertDocumentsToRedis();

        String question = "Redis 7 如何开启向量检索功能？";
        search(question);

    }

    /**
     * 搜索向量
     * @param question
     */
    public static void search(String question){
        TextSegment textSegment = TextSegment.from(question);
        Response<Embedding> embed = embeddingModel.embed(textSegment);
        final Embedding queryEmbedding = embed.content();

        EmbeddingSearchRequest embeddingSearchRequest = EmbeddingSearchRequest.builder()
                .queryEmbedding(queryEmbedding)
                .maxResults(3)
                .build();
        List<EmbeddingMatch<TextSegment>> matches = embeddingStore.search(embeddingSearchRequest).matches();
        for (EmbeddingMatch<TextSegment> match : matches) {
            System.out.println(match.embedded().text());
            System.out.println(match.score());
        }
    }

    /**
     * 插入文档到 Redis，初始化数据
     */
    public static void insertDocumentsToRedis() {
        try {
            String content = Files.readString(Paths.get("/Users/hanqf/idea_workspaces/springbootchapter/springboot3-demo/redis-demo/langchain4j-ai-demo/rag.txt"));

            // 简单按段落分割，也可以使用专门的文本分割工具
            String[] paragraphs = content.split("\n\n");

            for (String paragraph : paragraphs) {
                if (!paragraph.trim().isEmpty()) {
                    TextSegment textSegment = TextSegment.from(paragraph.trim());
                    Response<Embedding> embeddingResponse = embeddingModel.embed(textSegment);
                    Embedding embedding = embeddingResponse.content();

                    // 将向量和文本段添加到 Redis
                    embeddingStore.add(embedding, textSegment);
                    System.out.println("已添加段落到向量库: " + textSegment.text().substring(0, Math.min(50, textSegment.text().length())) + "...");
                }
            }

            System.out.println("所有文档已成功插入到 Redis 向量库中");

        } catch (IOException e) {
            System.err.println("读取文件失败: " + e.getMessage());
        }
    }
}
