package com.example.embedding;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.embedding.DashScopeEmbeddingModel;
import com.alibaba.cloud.ai.dashscope.embedding.DashScopeEmbeddingOptions;
import com.alibaba.cloud.ai.dashscope.spec.DashScopeModel;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.TokenCountBatchingStrategy;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.redis.RedisVectorStore;
import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisPooled;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * 向量demo
 * Created by hanqf on 2026/1/13 09:51.
 */


public class RedisVectorDemo {

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


    // Redis连接信息
    private static final DefaultJedisClientConfig jedisClientConfig = DefaultJedisClientConfig.builder()
            .user("admin")
            .password("123456")
            .build();
    private static final HostAndPort hostAndPort = new HostAndPort("127.0.0.1", 6379);
    // 创建 JedisPooled
    private static final JedisPooled jedisPooled = new JedisPooled(hostAndPort, jedisClientConfig);

    // 创建 RedisVectorStore
    private static final RedisVectorStore redisVectorStore = RedisVectorStore.builder(jedisPooled, embeddingModel)
            .indexName("custom-index")                // Optional: defaults to "spring-ai-index"
            .prefix("custom-prefix:")                  // Optional: defaults to "embedding:"
            .initializeSchema(true)                   // Optional: defaults to false
            .embeddingFieldName("vector")
            .contentFieldName("text")
            .batchingStrategy(new TokenCountBatchingStrategy()) // Optional: defaults to TokenCountBatchingStrategy
            .build();

    static {
        // 创建索引，如果已经创建不会重复创建
        redisVectorStore.afterPropertiesSet();
    }

    public static void main(String[] args) {

        insertDocumentsToRedis();

        String question = "Redis 7 如何开启向量检索功能？";
        search(question);
    }

    /**
     * 搜索向量
     *
     * @param question
     */
    public static void search(String question) {
        List<Document> results = redisVectorStore.similaritySearch(SearchRequest.builder()
                .query(question)
                .topK(5)
                .build());
        results.forEach(document -> {
            System.out.println("搜索结果：" + document.getText());
            System.out.println("相似度：" + document.getScore());
        });
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
                    final Document document = Document.builder().text(paragraph.trim()).build();
                    redisVectorStore.add(List.of(document));
                    System.out.println("已添加段落到向量库: " + document.getText().substring(0, Math.min(50, document.getText().length())) + "...");
                }
            }
            System.out.println("所有文档已成功插入到 Redis 向量库中");

        } catch (IOException e) {
            System.err.println("读取文件失败: " + e.getMessage());
        }
    }
}
