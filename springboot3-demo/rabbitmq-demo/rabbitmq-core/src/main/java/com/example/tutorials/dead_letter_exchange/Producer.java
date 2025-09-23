package com.example.tutorials.dead_letter_exchange;

import com.example.tutorials.RabbitMQConnectionFactory;
import com.example.tutorials.utils.RabbitMQUtil;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.util.HashMap;
import java.util.Map;

/**
 * 生产者
 * Created by hanqf on 2025/9/22 16:25.
 * <p>
 * 死信队列
 *
 * 这里基于消息过期时间，模拟了一个 延时队列，即5秒后消息被转发到 死信队列中，交由 死信队列 的 消费者 处理
 */


public class Producer {
    // 原队列名称
    private static final String QUEUE_NAME = "core-dead-letter-source-queue";
    // 死信交换机名称
    private static final String DEAD_LETTER_EXCHANGE_NAME = "core-dead-letter-exchange";
    private static final String DEAD_LETTER_EXCHANGE_ROUTINGKEY = "core-dead-letter-routing_key";

    public static void main(String[] args) throws Exception {

        try (Connection connection = RabbitMQConnectionFactory.createConnection();
             Channel channel = connection.createChannel()) {
            // 声明队列
            // 如果队列已经存在，则此处声明的参数必须与服务端创建的队列的参数一致，否则会报错
            Map<String, Object> params = new HashMap<>();
            params.put("x-dead-letter-exchange", DEAD_LETTER_EXCHANGE_NAME); // 死信交换机名称
            params.put("x-dead-letter-routing-key", DEAD_LETTER_EXCHANGE_ROUTINGKEY); // 死信路由键，设置了这个键，会替换原队列中的路由键
            params.put("x-message-ttl", 5000); // 消息过期 时间 单位毫秒，这里设置5秒
            RabbitMQUtil.declareClassicQueue(channel, QUEUE_NAME, params);

            // 发送消息
            for(int i = 0; i < 10; i++) {
                String message = "Hello World!" + i; // 消息内容
                AMQP.BasicProperties props = null; // 消息属性
                channel.basicPublish("", QUEUE_NAME, props, message.getBytes());

                System.out.println(" [x] Sent '" + message + "'");
            }

        }
    }
}
