package com.example.tutorials.routing;

import com.example.tutorials.RabbitMQConstants;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

/**
 *
 * Created by hanqf on 2025/9/23 17:22.
 */

@Component
public class RoutingConsumer {
    // 这里不声明 messageConverter 也可以，会自动使用 RabbitMQConfig 中声明的 messageConverter
    @RabbitListener(queues = {RabbitMQConstants.QUEUE_NAME_ROUTING_1}, messageConverter = "jsonConverter")
    public void receive01(Map<String, Object> message) {
        System.out.println("consumer received01 message map: " + message);
    }

    @RabbitListener(queues = {RabbitMQConstants.QUEUE_NAME_ROUTING_2}, ackMode = "MANUAL")
    public void receive02(Map<String, Object> message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {
        // 因为 消费线程 配置为 5，所以这里 deliveryTag 会有 5 个相同值，因为一个线程代表一个 channel，deliveryTag 是在每个 channel 中自增的
        System.out.println("consumer received02 message map: " + message + " || deliveryTag: " + deliveryTag);
        // 处理消息
        channel.basicAck(deliveryTag, false);
    }

}
