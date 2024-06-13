package com.hanqf.ai_agent.service;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.spring.AiService;

/**
 * <h1></h1>
 * Created by hanqf on 2024/6/13 14:43.
 */


@AiService
public interface Assistant {

    @SystemMessage("You are a polite assistant")
    String chat(String userMessage);
}
