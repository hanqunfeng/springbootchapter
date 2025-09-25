package com.example.tutorials.hello_world;

import com.example.tutorials.RabbitMQConstants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 *
 * Created by hanqf on 2025/9/23 17:28.
 */

@Component
public class HelloWorldProducer {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private JsonMapper jsonMapper;

//    @Autowired
//    @Qualifier("jsonConverter")
//    private MessageConverter messageConverter;

    public String send(String message) {
        //设置部分请求参数
        MessageProperties messageProperties = new MessageProperties();
        // 因为默认的消息转换器是 jsonConverter，所以发送的消息如果是字符串，则需要在发送时配置为 CONTENT_TYPE_TEXT_PLAIN
        // 这样消费者接收到消息时才会按照字符串处理
        messageProperties.setContentType(MessageProperties.CONTENT_TYPE_TEXT_PLAIN);
        messageProperties.setPriority(2); // 优先级, 默认为 0
        rabbitTemplate.send(RabbitMQConstants.QUEUE_NAME_HELLO_WORLD, new Message(message.getBytes(StandardCharsets.UTF_8), messageProperties));
        return message;
    }


    public Map<String, Object> send2(Map<String, Object> message) {
//        rabbitTemplate.setMessageConverter(messageConverter); //设置消息转换器，无需配置，已经自动注入到 RabbitTemplate 中
        rabbitTemplate.convertAndSend(RabbitMQConstants.QUEUE_NAME_HELLO_WORLD_MAP, message);
        return message;
    }

    public Map<String, Object> send(Map<String, Object> message) {
//        rabbitTemplate.setMessageConverter(messageConverter); //设置消息转换器，无需配置，已经自动注入到 RabbitTemplate 中

        // 添加 CorrelationData，携带数据，方便发送回调处理
        rabbitTemplate.convertAndSend(RabbitMQConstants.QUEUE_NAME_HELLO_WORLD_MAP, message, new CorrelationData("100"));
        return message;
    }

    public Map<String, Object> send3(Map<String, Object> message) {
//        rabbitTemplate.setMessageConverter(messageConverter); //设置消息转换器，无需配置，已经自动注入到 RabbitTemplate 中

        // 发送消息前为消息添加额外属性
        rabbitTemplate.convertAndSend(RabbitMQConstants.QUEUE_NAME_HELLO_WORLD_MAP, message, m -> {
            m.getMessageProperties().setPriority(1);
            return m;
        });
        return message;
    }

    public Map<String, Object> sendMessage(Map<String, Object> message) throws JsonProcessingException {
//        rabbitTemplate.setMessageConverter(messageConverter); //设置消息转换器，无需配置，已经自动注入到 RabbitTemplate 中

        Message jsonMsg = MessageBuilder.withBody(jsonMapper.writeValueAsBytes(message))
                .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                .setPriority(2)
                .build();

        rabbitTemplate.send(RabbitMQConstants.QUEUE_NAME_HELLO_WORLD_MAP, jsonMsg);
        return message;
    }

}
