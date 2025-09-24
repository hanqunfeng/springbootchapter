package com.example.tutorials.stream;

import com.example.tutorials.RabbitMQConstants;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 *
 * Created by hanqf on 2025/9/23 17:28.
 */

@Component
public class StreamProducer {
    @Autowired
    private RabbitTemplate rabbitTemplate;

//    @Autowired
//    @Qualifier("jsonConverter")
//    private MessageConverter messageConverter;


    // 发送流消息
    public Map<String, Object> sendStream(Map<String, Object> message) {
//        rabbitTemplate.setMessageConverter(messageConverter); //设置消息转换器，无需配置，已经自动注入到 RabbitTemplate 中
        rabbitTemplate.convertAndSend(RabbitMQConstants.QUEUE_NAME_STREAM, message);
        return message;
    }
}
