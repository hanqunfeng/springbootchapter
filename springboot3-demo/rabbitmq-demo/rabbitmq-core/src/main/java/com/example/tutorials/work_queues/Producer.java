package com.example.tutorials.work_queues;

import com.example.tutorials.RabbitMQConnectionFactory;
import com.example.tutorials.utils.RabbitMQUtil;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

/**
 * 生产者
 * Created by hanqf on 2025/9/22 16:25.
 * <p>
 * Work queues ⼯作序列
 *
 * 这是RabbitMQ最基础也是最常⽤的⼀种⼯作机制。
 * ⼯作任务模式，领导部署⼀个任务，由下⾯的⼀个员⼯来处理。只关⼼任务被正确处理，不关⼼给谁处理。
 */


public class Producer {
    private static final String QUEUE_NAME = "core-work-queues-queue";

    public static void main(String[] args) throws Exception {

        try (Connection connection = RabbitMQConnectionFactory.createConnection();
             Channel channel = connection.createChannel()) {
            // 声明队列
            // 如果队列已经存在，则此处声明的参数必须与服务端创建的队列的参数一致，否则会报错
            RabbitMQUtil.declareClassicQueue(channel, QUEUE_NAME);

            for (int i = 0; i < 10; i++) {
                // 发送消息
                String message = "Hello World!:" + i; // 消息内容
                // 持久化消息
//                AMQP.BasicProperties props = MessageProperties.PERSISTENT_TEXT_PLAIN;
                AMQP.BasicProperties props = new AMQP.BasicProperties.Builder()
                        .contentType("text/plain")
                        .deliveryMode(2)
                        .priority(0)
                        .build();
                // 指定队列名称
                channel.basicPublish("", QUEUE_NAME, props, message.getBytes());

                System.out.println(" [x] Sent '" + message + "'");
            }

        }
    }
}
