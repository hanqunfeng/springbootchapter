package com.example.tutorials.topics;

import com.example.tutorials.RabbitMQConnectionFactory;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

/**
 * 生产者
 * Created by hanqf on 2025/9/22 16:25.
 * <p>
 * Topics 基于话题的路由
 *
 * 把preducer与Consumer进⾏进⼀步的解耦。producer只负责发送消息，⾄于消息进⼊哪个queue，由exchange来分配。
 * 增加⼀个路由配置，指定exchange如何将不同类别的消息分发到不同的queue上
 * 这个模式也就在Routing模式的基础上，对routingKey进⾏了模糊匹配。单词之间⽤,隔开，* 代表⼀个具体的单词。# 代表0个或多个单词。
 */


public class Producer {
    private static final String EXCHANGE_NAME = "core-topic-exchange";
    private static final String ROUTING_KEY01 = "my.orange.test";
    private static final String ROUTING_KEY02 = "blue.my.test";

    // 启动生产者前要先启动消费者
    public static void main(String[] args) throws Exception {

        try (Connection connection = RabbitMQConnectionFactory.createConnection();
             Channel channel = connection.createChannel()) {

            // 声明交换机
            // 如果同名交换机已经存在，则此处声明的参数必须与服务端创建的队列的参数一致，否则会报错
            // TOPIC: 模糊匹配路由
            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);

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
                // 发送消息是指定 routing_key，消息将被发送到绑定了该routing_key的queue
                channel.basicPublish(EXCHANGE_NAME, ROUTING_KEY01, props, message.getBytes());
                channel.basicPublish(EXCHANGE_NAME, ROUTING_KEY02, props, message.getBytes());

                System.out.println(" [x] Sent '" + message + "'");
            }

        }
    }
}
