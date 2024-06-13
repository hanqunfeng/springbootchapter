package com.hanqf.demo;

/**
 * <h1></h1>
 * Created by hanqf on 2024/6/13 15:03.
 */


import com.hanqf.demo.self.AssistantSelf;
import com.hanqf.demo.self.AssistantToolsSelf;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.TokenStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

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
    AssistantToolsSelf assistantToolsSelf;


    @GetMapping("/model")
    public String model(@RequestParam(value = "message", defaultValue = "Hello") String message) {
        return chatLanguageModel.generate(message);
    }

    @GetMapping("/modelToolStream")
    public void modelToolStream(@RequestParam(value = "message", defaultValue = "我想去北京雍和宫附近喝咖啡，请帮我推荐几个") String message) {
        AssistantSelf assistantSelf = AiServices.builder(AssistantSelf.class)
                .streamingChatLanguageModel(streamingChatLanguageModel)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .tools(assistantToolsSelf)
                .build();
        assistantSelf.chatStream(message).onNext(System.out::println)
                .onComplete(System.out::println)
                .onError(Throwable::printStackTrace)
                .start();
    }

    @GetMapping(value = "/modelToolStream2", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> modelToolStream2(@RequestParam(value = "message", defaultValue = "我想去北京雍和宫附近喝咖啡，请帮我推荐几个") String message) {
        Sinks.Many<String> sink = Sinks.many().unicast().onBackpressureBuffer();
        AssistantSelf assistantSelf = AiServices.builder(AssistantSelf.class)
                .streamingChatLanguageModel(streamingChatLanguageModel)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .tools(assistantToolsSelf)
                .build();

        TokenStream ts = assistantSelf.chatStream(message);
        ts.onNext(sink::tryEmitNext)
                .onError(sink::tryEmitError)
                .start();
        return sink.asFlux();
    }


    @GetMapping("/modelTool")
    public String modelTool(@RequestParam(value = "message", defaultValue = "我想去北京雍和宫附近喝咖啡，请帮我推荐几个") String message) {
        AssistantSelf assistantSelf = AiServices.builder(AssistantSelf.class)
                .chatLanguageModel(chatLanguageModel)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .tools(assistantToolsSelf)
                .build();
        return assistantSelf.chat(message);
    }


}
