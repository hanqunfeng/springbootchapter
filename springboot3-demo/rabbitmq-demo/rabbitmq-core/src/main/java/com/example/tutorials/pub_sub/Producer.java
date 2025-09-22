package com.example.tutorials.pub_sub;

import com.example.tutorials.RabbitMQConnectionFactory;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

/**
 * 生产者
 * Created by hanqf on 2025/9/22 16:25.
 * <p>
 * Publish/Subscribe 订阅 发布 机制
 *
 * 把preducer与Consumer进⾏进⼀步的解耦。producer只负责发送消息，⾄于消息进⼊哪个queue，由exchange来分配。
 */


public class Producer {
    private static final String EXCHANGE_NAME = "core-pub-sub-exchange";

    // 启动生产者前要先启动消费者
    public static void main(String[] args) throws Exception {

        try (Connection connection = RabbitMQConnectionFactory.createConnection();
             Channel channel = connection.createChannel()) {

            // 声明交换机
            // 如果同名交换机已经存在，则此处声明的参数必须与服务端创建的队列的参数一致，否则会报错
            // FANOUT: 广播，将消息发送给所有绑定的队列
            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT);

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
                // 广播消息，此时不需要 routing_key，所有被绑定的队列都可以接收到
                channel.basicPublish(EXCHANGE_NAME, "", props, message.getBytes());

                System.out.println(" [x] Sent '" + message + "'");
            }

        }
    }
}
