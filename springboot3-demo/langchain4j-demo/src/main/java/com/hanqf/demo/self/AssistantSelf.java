package com.hanqf.demo.self;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.TokenStream;

/**
 * <h1></h1>
 * Created by hanqf on 2024/6/13 14:43.
 */


public interface AssistantSelf {

    @SystemMessage("You are a polite assistant")
    String chat(String userMessage);


    @SystemMessage("You are a polite assistant")
    TokenStream chatStream(String userMessage);
}
