package com.example.tutorials.hello_world;

import com.example.tutorials.RabbitMQConnectionFactory;
import com.example.tutorials.utils.RabbitMQUtil;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

/**
 * 生产者
 * Created by hanqf on 2025/9/22 16:25.
 * <p>
 * hello world体验
 *
 * 最直接的⽅式，P端发送⼀个消息到⼀个指定的queue，中间不需要任何exchange规则。C端按queue⽅式进⾏消费。
 */


public class Producer {
    private static final String QUEUE_NAME = "core-hello-world-queue";

    public static void main(String[] args) throws Exception {

        try (Connection connection = RabbitMQConnectionFactory.createConnection();
             Channel channel = connection.createChannel()) {
            // 声明队列
            // 如果队列已经存在，则此处声明的参数必须与服务端创建的队列的参数一致，否则会报错
            RabbitMQUtil.declareClassicQueue(channel, QUEUE_NAME);

            // 发送消息
            String message = "Hello World!"; // 消息内容
            AMQP.BasicProperties props = null; // 消息属性
            channel.basicPublish("", QUEUE_NAME, props, message.getBytes());

            System.out.println(" [x] Sent '" + message + "'");

        }
    }
}
