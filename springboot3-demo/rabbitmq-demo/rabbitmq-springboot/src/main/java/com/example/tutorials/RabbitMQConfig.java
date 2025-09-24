package com.example.tutorials;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * Created by hanqf on 2025/9/23 17:59.
 */

@Configuration
public class RabbitMQConfig {

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        // 开启消息确认回调
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            // correlationData 需要在发送消息时自己配置
            if (correlationData != null) {
                System.out.println("消息ID: " + correlationData.getId());
            }
            if (ack) {
                System.out.println("!消息发送成功!");
            } else {
                System.err.println("消息发送失败" + "，原因：" + cause);
            }
        });

        // 开启 Returns 回调，确认消息是否从交换机路由到队列
        rabbitTemplate.setReturnsCallback(returned -> {
            System.err.println("消息无法路由到队列，返回信息：");
            System.err.println("交换机：" + returned.getExchange());
            System.err.println("路由键：" + returned.getRoutingKey());
            System.err.println("消息体：" + new String(returned.getMessage().getBody()));
        });

        // 开启消息转换器
        rabbitTemplate.setMessageConverter(messageConverter());

        return rabbitTemplate;
    }


    // 消息转换器
    @Bean("jsonConverter")
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
