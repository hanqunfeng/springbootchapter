package com.hanqf.demo;

/**
 * <h1></h1>
 * Created by hanqf on 2024/6/13 15:03.
 */


import com.hanqf.demo.milvus.MilvusEmbeddingStoreTool;
import com.hanqf.demo.self.AssistantSelf;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.rag.query.transformer.CompressingQueryTransformer;
import dev.langchain4j.rag.query.transformer.QueryTransformer;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.filter.Filter;
import dev.langchain4j.store.embedding.filter.MetadataFilterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * This is an example of using a {@link ChatLanguageModel}, a low-level LangChain4j API.
 */
@RestController
class ChatLanguageModelController {
    @Autowired
    ChatLanguageModel chatLanguageModel;

    @Autowired
    StreamingChatLanguageModel streamingChatLanguageModel;

    @Autowired
    MilvusEmbeddingStoreTool milvusEmbeddingStoreTool;

    @Autowired
    private EmbeddingStore<TextSegment> embeddingStore;

    @Autowired
    private OpenAiEmbeddingModel embeddingModel;


    @GetMapping("/modelInit")
    public String model(@RequestParam(value = "message", defaultValue = "Hello") String message) {
        milvusEmbeddingStoreTool.init();
        return chatLanguageModel.generate(message);
    }

    @GetMapping("/modelArg")
    public String modelArg(@RequestParam(value = "message", defaultValue = "JVM 常见异常?") String message) {

        //我们将创建一个CompressingQueryTransformer，负责压缩
        //用户的查询和前面的对话合并为一个单独的查询。
        //这将大大提高检索过程的质量。
        QueryTransformer queryTransformer = new CompressingQueryTransformer(chatLanguageModel);


        ContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(2)
                .minScore(0.6)
                .build();

        //RetrievalAugmentor作为LangChain4j中RAG流的入口点。
        //它可以配置为根据您的要求自定义RAG行为。
        //在随后的示例中，我们将探索更多的自定义。
        RetrievalAugmentor retrievalAugmentor = DefaultRetrievalAugmentor.builder()
                .queryTransformer(queryTransformer)
                .contentRetriever(contentRetriever)
                .build();

        AssistantSelf assistantSelf = AiServices.builder(AssistantSelf.class)
                .chatLanguageModel(chatLanguageModel)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .retrievalAugmentor(retrievalAugmentor)
                .build();
        return assistantSelf.chat(message);
    }

    @GetMapping("/modelArgMetaData")
    public String modelArgMetaData(@RequestParam(value = "message", defaultValue = "JVM 常见异常?") String message) {

        //我们将创建一个CompressingQueryTransformer，负责压缩
        //用户的查询和前面的对话合并为一个单独的查询。
        //这将大大提高检索过程的质量。
        QueryTransformer queryTransformer = new CompressingQueryTransformer(chatLanguageModel);


        Filter filter = MetadataFilterBuilder.metadataKey("key").isEqualTo("value2");

        ContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .filter(filter)
                .maxResults(2)
                .minScore(0.6)
                .build();



        //RetrievalAugmentor作为LangChain4j中RAG流的入口点。
        //它可以配置为根据您的要求自定义RAG行为。
        //在随后的示例中，我们将探索更多的自定义。
        RetrievalAugmentor retrievalAugmentor = DefaultRetrievalAugmentor.builder()
                .queryTransformer(queryTransformer)
                .contentRetriever(contentRetriever)
                .build();

        AssistantSelf assistantSelf = AiServices.builder(AssistantSelf.class)
                .chatLanguageModel(chatLanguageModel)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .retrievalAugmentor(retrievalAugmentor)
                .build();
        return assistantSelf.chat(message);
    }


}
