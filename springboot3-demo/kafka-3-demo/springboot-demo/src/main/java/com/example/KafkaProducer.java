package com.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * Created by hanqf on 2025/10/15 17:56.
 */


@RestController
public class KafkaProducer {
    public static final String TOPIC_NAME = "disTopic";


    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;
    // 发送消息
    @GetMapping("/kafka/send/{message}")
    public String sendMessage(@PathVariable("message") String message) {
        kafkaTemplate.send(TOPIC_NAME, message);
        return "success:" + message;
    }
}
