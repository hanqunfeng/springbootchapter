package com.hanqf.demo;

/**
 * <h1></h1>
 * Created by hanqf on 2024/6/13 15:03.
 */


import com.fasterxml.jackson.core.JsonProcessingException;
import com.hanqf.demo.MemoryStore.PersistentChatMemoryStore;
import com.hanqf.demo.self.AssistantSelf;
import com.hanqf.demo.self.AssistantToolsSelf;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.Result;
import dev.langchain4j.service.TokenStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.List;

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

    @GetMapping("/modelSysFile")
    public String modelSysFile(@RequestParam(value = "message", defaultValue = "你是谁？") String message) {
        AssistantSelf assistantSelf = AiServices.builder(AssistantSelf.class)
                .chatLanguageModel(chatLanguageModel)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .build();
        return assistantSelf.chatSysFile(message);
    }

    @GetMapping("/modelSysUser")
    public String modelSysUser(@RequestParam(value = "message", defaultValue = "你是谁？") String message) {
        AssistantSelf assistantSelf = AiServices.builder(AssistantSelf.class)
                .chatLanguageModel(chatLanguageModel)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .build();
        return assistantSelf.chatSysUser(message);
    }

    @GetMapping("/modelChatSys")
    public String modelChatSys(@RequestParam(value = "message", defaultValue = "为什么token可以转换为向量") String message) {
        AssistantSelf assistantSelf = AiServices.builder(AssistantSelf.class)
                .chatLanguageModel(chatLanguageModel)
                .systemMessageProvider(o -> "你是一个高级AI专家，请回答有关LLM相关的问题")
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .build();
        return assistantSelf.chatSys(message);
    }

    @GetMapping("/modelChatUser")
    public String modelChatUser(@RequestParam(value = "message", defaultValue = "你是谁？") String message) {
        AssistantSelf assistantSelf = AiServices.builder(AssistantSelf.class)
                .chatLanguageModel(chatLanguageModel)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .build();
        return assistantSelf.chatUser(message);
    }

    @GetMapping("/modelNameAge")
    public String modelNameAge(@RequestParam(value = "name", defaultValue = "张三") String name, @RequestParam(value = "age", defaultValue = "20") Integer age) {
        AssistantSelf assistantSelf = AiServices.builder(AssistantSelf.class)
                .chatLanguageModel(chatLanguageModel)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .build();
        return assistantSelf.chatNameAge(name, age);
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

//        final Sinks.One<String> one = Sinks.one();
//        one.tryEmitValue("hello");
//        final Mono<String> mono = one.asMono();

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


    // 返回以下类型时，如AiMessage， Response<AiMessage>等等，要配置 HttpMessageConverter 并指定 ObjectMapper，
    // 主要是 ObjectMapper中要 .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)，否则序列化时会抛出异常
    @GetMapping(value = "/chatAiMessage", produces = MediaType.APPLICATION_JSON_VALUE)
    public AiMessage chatAiMessage(@RequestParam(value = "message", defaultValue = "Hello") String message) throws JsonProcessingException {
        AssistantSelf assistantSelf = AiServices.builder(AssistantSelf.class)
                .chatLanguageModel(chatLanguageModel)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .build();

        return assistantSelf.chatAiMessage(message);
    }

    @GetMapping(value = "/chatResponseAiMessage", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<AiMessage> chatResponseAiMessage(@RequestParam(value = "message", defaultValue = "Hello") String message) {
        AssistantSelf assistantSelf = AiServices.builder(AssistantSelf.class)
                .chatLanguageModel(chatLanguageModel)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .build();
        return assistantSelf.chatResponseAiMessage(message);
    }

    @GetMapping(value = "/chatResultString")
    public Result<String> chatResultString(@RequestParam(value = "message", defaultValue = "Hello") String message) {
        AssistantSelf assistantSelf = AiServices.builder(AssistantSelf.class)
                .chatLanguageModel(chatLanguageModel)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .build();
        return assistantSelf.chatResultString(message);
    }

    @GetMapping(value = "/chatResultAiMessage", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<AiMessage> chatResultAiMessage(@RequestParam(value = "message", defaultValue = "Hello") String message) {
        AssistantSelf assistantSelf = AiServices.builder(AssistantSelf.class)
                .chatLanguageModel(chatLanguageModel)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .build();
        return assistantSelf.chatResultAiMessage(message);
    }

    @GetMapping("/analyzeSentimentOf")
    public AssistantSelf.Sentiment analyzeSentimentOf(@RequestParam(value = "message", defaultValue = "This is great!") String message) {
        AssistantSelf assistantSelf = AiServices.builder(AssistantSelf.class)
                .chatLanguageModel(chatLanguageModel)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .build();
        return assistantSelf.analyzeSentimentOf(message);
    }


    @GetMapping("/isPositive")
    public boolean isPositive(@RequestParam(value = "message", defaultValue = "It's awful!") String message) {
        AssistantSelf assistantSelf = AiServices.builder(AssistantSelf.class)
                .chatLanguageModel(chatLanguageModel)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .build();
        return assistantSelf.isPositive(message);
    }

    @GetMapping("/extractPersonFrom")
    public AssistantSelf.Person extractPersonFrom(@RequestParam(value = "message", defaultValue = AssistantSelf.TEXT) String message) {
        AssistantSelf assistantSelf = AiServices.builder(AssistantSelf.class)
                .chatLanguageModel(chatLanguageModel)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .build();
        return assistantSelf.extractPersonFrom(message);
    }


    @GetMapping("/modelSys")
    public List<String> modelSys() {
        AssistantSelf assistantSelf = AiServices.builder(AssistantSelf.class)
                .chatLanguageModel(chatLanguageModel)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .build();

        String translation = assistantSelf.translate("Hello, how are you?", "chinese");
        System.out.println(translation); // Ciao, come stai?


        String text = "AI, or artificial intelligence, is a branch of computer science that aims to create " +
                "machines that mimic human intelligence. This can range from simple tasks such as recognizing " +
                "patterns or speech to more complex tasks like making decisions or predictions.";

        List<String> bulletPoints = assistantSelf.summarize(text, 3);
        System.out.println(bulletPoints);

        return bulletPoints;
    }


    @GetMapping("/modelMemoryId")
    public List<List<String>> modelMemoryId() {
        AssistantSelf assistantSelf = AiServices.builder(AssistantSelf.class)
                .chatLanguageModel(chatLanguageModel)
                .chatMemoryProvider(memoryId -> MessageWindowChatMemory.withMaxMessages(10))
                .build();

        String answerToKlaus1 = assistantSelf.chat(1, "Hello, my name is Klaus");
        String answerToKlaus2 = assistantSelf.chat(1, "给我讲个笑话");
        List<String> answerToKlaus = List.of(answerToKlaus1, answerToKlaus2);

        String answerToFrancine1 = assistantSelf.chat(2, "Hello, my name is Francine");
        String answerToFrancine2 = assistantSelf.chat(2, "美国为什么总是打仗？");
        List<String> answerToFrancine = List.of(answerToFrancine1, answerToFrancine2);

        return List.of(answerToKlaus, answerToFrancine);

    }


    @Autowired
    private PersistentChatMemoryStore persistentChatMemoryStore;
    @GetMapping("/modelPersistentMemoryId")
    public List<List<String>> modelPersistentMemoryId() {
        ChatMemoryProvider chatMemoryProvider = memoryId -> MessageWindowChatMemory.builder()
                .id(memoryId)
                .maxMessages(10)
                .chatMemoryStore(persistentChatMemoryStore)
                .build();

        AssistantSelf assistantSelf = AiServices.builder(AssistantSelf.class)
                .chatLanguageModel(chatLanguageModel)
                .chatMemoryProvider(chatMemoryProvider)
                .build();

        String answerToKlaus1 = assistantSelf.chatPersistent("1", "Hello, my name is Klaus");
        String answerToKlaus2 = assistantSelf.chatPersistent("1", "给我讲个笑话");
        List<String> answerToKlaus = List.of(answerToKlaus1, answerToKlaus2);

        String answerToFrancine1 = assistantSelf.chatPersistent("2", "Hello, my name is Francine");
        String answerToFrancine2 = assistantSelf.chatPersistent("2", "美国为什么总是打仗？");
        List<String> answerToFrancine = List.of(answerToFrancine1, answerToFrancine2);

        return List.of(answerToKlaus, answerToFrancine);

    }



}
