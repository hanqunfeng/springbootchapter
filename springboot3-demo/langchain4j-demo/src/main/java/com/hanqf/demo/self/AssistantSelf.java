package com.hanqf.demo.self;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.service.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * <h1></h1>
 * Created by hanqf on 2024/6/13 14:43.
 *
 * examples: https://github.com/langchain4j/langchain4j-examples/blob/main/other-examples/src/main/java/OtherServiceExamples.java
 */


public interface AssistantSelf {

    @SystemMessage("You are a polite assistant")
    String chat(String userMessage);


    @SystemMessage("You are a polite assistant")
    TokenStream chatStream(String userMessage);


    //  @SystemMessage 有变量时，一定要有 @UserMessage
    @SystemMessage("You are a professional translator into {{language}}")
    @UserMessage("Translate the following text: {{text}}")
    String translate(@V("text") String text, @V("language") String language);

    @SystemMessage("Summarize every message from user in {{n}} bullet points. Provide only bullet points.")
    List<String> summarize(@UserMessage String text, @V("n") int n);


    String chatSys(String userMessage);

    @UserMessage("你是我的好朋友。使用俚语回答。 {{message}}")
    String chatUser(@V("message") String userMessage);

    @SystemMessage("你是一个有礼貌的助理")
    @UserMessage("你是我的好朋友。使用俚语回答。 {{message}}")
    String chatSysUser(@V("message") String userMessage);

    @UserMessage("Hello, my name is {{name}}. I am {{age}} years old.")
    String chatNameAge(@V("name") String name, @V("age") int age);

    // classpath路径下查找文件
    @SystemMessage(fromResource = "/ai_agent/system.txt")
    String chatSysFile(String userMessage);

    // 输出其它类型
    @SystemMessage("You are a polite assistant")
    AiMessage chatAiMessage(String userMessage);

    @SystemMessage("You are a polite assistant")
    Response<AiMessage> chatResponseAiMessage(String userMessage);

    @SystemMessage("You are a polite assistant")
    Result<String> chatResultString(String userMessage);

    @SystemMessage("You are a polite assistant")
    Result<AiMessage> chatResultAiMessage(String userMessage);

    public enum Sentiment {
        POSITIVE, NEUTRAL, NEGATIVE
    }

    @UserMessage("Analyze sentiment of {{it}}")
    Sentiment analyzeSentimentOf(String text);

    @UserMessage("Does {{it}} has a positive sentiment?")
    boolean isPositive(String text);

    @Data
    public static class Person {
        String firstName;
        String lastName;
        LocalDate birthDate;
        Address address;
    }

    @Data
    public static class Address {
        String street;
        Integer streetNumber;
        String city;
    }

    @UserMessage("Extract information about a person from {{it}}")
    Person extractPersonFrom(String text);

    public static String TEXT = """
            In 1968, amidst the fading echoes of Independence Day,
            a child named John arrived under the calm evening sky.
            This newborn, bearing the surname Doe, marked the start of a new journey.
            He was welcomed into the world at 345 Whispering Pines Avenue
            a quaint street nestled in the heart of Springfield
            an abode that echoed with the gentle hum of suburban dreams and aspirations.
            """;


    // 指定对话上下文存储id,可以根据id查询关联上下文
    String chat(@MemoryId int memoryId, @UserMessage String message);

    // 指定对话上下文存储id,可以根据id查询关联上下文
    String chatPersistent(@MemoryId String memoryId, @UserMessage String message);
}
