package com.example.tutorials.stream;

import com.example.tutorials.RabbitMQConnectionFactory;
import com.example.tutorials.utils.RabbitMQUtil;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

/**
 * 生产者
 * Created by hanqf on 2025/9/22 16:25.
 * <p>
 * stream 队列
 *
 */


public class Producer {
    private static final String QUEUE_NAME = "core-stream-queue";

    public static void main(String[] args) throws Exception {

        try (Connection connection = RabbitMQConnectionFactory.createConnection();
             Channel channel = connection.createChannel()) {
            // 声明队列
            // 如果队列已经存在，则此处声明的参数必须与服务端创建的队列的参数一致，否则会报错
            RabbitMQUtil.declareStreamQueue(channel, QUEUE_NAME);

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
