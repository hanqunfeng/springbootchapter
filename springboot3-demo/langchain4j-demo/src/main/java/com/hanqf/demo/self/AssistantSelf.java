package com.hanqf.demo.self;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

/**
 * <h1></h1>
 * Created by hanqf on 2024/6/13 14:43.
 */


public interface AssistantSelf {

    @SystemMessage("You are a polite assistant")
    String chat(String userMessage);


    @SystemMessage("You are a polite assistant")
    TokenStream chatStream(String userMessage);

// 不可以为@SystemMessage指定变量
//    @SystemMessage("你是一个 {{message}}，请回答我的问题。")
//    String chatSys(@V("message") String userMessage);

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

}
