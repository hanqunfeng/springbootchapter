package com.piaoyi.controller;

import com.piaoyi.service.MockWeatherService;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingClient;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.model.function.FunctionCallbackWrapper;
import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

/**
 * <h1></h1>
 * Created by hanqf on 2024/5/17 16:24.
 */


@RestController
public class ChatController {
    @Autowired
    private  OpenAiChatClient chatClient;
    @Autowired
    private  EmbeddingClient embeddingClient;
    @Autowired
    private VectorStore vectorStore;

    @GetMapping("/ai/generate")
    public String generate(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        return chatClient.call(message);
    }

    @GetMapping(value = "/ai/generateStream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> generateStream(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        Prompt prompt = new Prompt(new UserMessage(message));
        return chatClient.stream(prompt).map(chatResponse -> {
            if (chatResponse.getResult() != null && chatResponse.getResult().getOutput() != null && chatResponse.getResult().getOutput().getContent() != null) {
//                System.out.print(chatResponse.getResult().getOutput().getContent());
                return chatResponse.getResult().getOutput().getContent();
            } else {
                return "";
            }
        });
    }

    @GetMapping(value = "/ai/generateStream2", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ChatResponse> generateStream2(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        Prompt prompt = new Prompt(new UserMessage(message));
        return chatClient.stream(prompt);
    }

    @GetMapping("/ai/generateFunction")
    public ChatResponse generateFunction(@RequestParam(value = "message", defaultValue = "北京、石家庄的天气怎么样，适合穿什么衣服？温度保留1位小数。") String message) {

        UserMessage userMessage = new UserMessage(message);

        var promptOptions = OpenAiChatOptions.builder()
                .withFunctionCallbacks(List.of(
                        FunctionCallbackWrapper.builder(new MockWeatherService())
                                .withName("CurrentWeather")
                                .withDescription("Get the weather in location").build()))
                .build();

        return chatClient.call(new Prompt(List.of(userMessage), promptOptions));

    }

    @GetMapping("/ai/embedding")
    public List<Double> embed(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        EmbeddingResponse embeddingResponse = this.embeddingClient.embedForResponse(List.of(message));
        return embeddingResponse.getResult().getOutput();
    }

    @GetMapping("/ai/rag_add")
    public List<Document> rag_add() {
        List <Document> documents = List.of(
                new Document("Spring AI rocks!! Spring AI rocks!! Spring AI rocks!! Spring AI rocks!! Spring AI rocks!!", Map.of("country", "CHN")),
                new Document("The World is Big and Salvation Lurks Around the Corner",Map.of("country", "CHN")),
                new Document("You walk forward facing the past and you turn back toward the future.", Map.of("country", "USA")));

        // Add the documents to PGVector
        vectorStore.add(documents);

        return documents;
    }

    @GetMapping("/ai/rag_search")
    public List<Document> rag_search() {
        // Retrieve documents similar to a query
        List<Document> results = vectorStore.similaritySearch(
                SearchRequest.query("Spring")
//                        .withFilterExpression("metadata['country'] == 'CHN' && content != 'abc'") //当前spring ai 对json不支持
                        .withFilterExpression("content != 'abc'")
                        .withTopK(5)
        );



//        List<Document> results = vectorStore.similaritySearch("Spring");


        return results;
    }

}
