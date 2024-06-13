package com.hanqf.ai_agent.controller;

/**
 * <h1></h1>
 * Created by hanqf on 2024/6/13 14:46.
 */


import com.hanqf.ai_agent.service.Assistant;
import com.hanqf.ai_agent.tools.AssistantTools;
import dev.langchain4j.service.spring.AiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * This is an example of using an {@link AiService}, a high-level LangChain4j API.
 */
@RestController
class AssistantController {

    Assistant assistant;

    AssistantController(Assistant assistant) {
        this.assistant = assistant;
    }

    @Autowired
    private AssistantTools assistantTools;

    @GetMapping("/assistant")
    public String assistant(@RequestParam(value = "message", defaultValue = "北京、石家庄的天气怎么样，适合穿什么衣服？温度保留1位小数。") String message) {
        return assistant.chat(message);
    }

    @GetMapping("/assistant2")
    public String assistant2(@RequestParam(value = "message", defaultValue = "我想在北京五道口附近喝咖啡，给我推荐几个") String message) {
        return assistant.chat(message);
    }

    @GetMapping("/tool")
    public String tool(@RequestParam(value = "city", defaultValue = "北京") String city, @RequestParam(value = "location", defaultValue = "雍和宫") String location) {
        Map<String, Object> result = assistantTools.getLocationCoordinate2(location, city);

        return assistantTools.searchNearbyPois(result.get("location").toString(), "咖啡馆");
    }
}
