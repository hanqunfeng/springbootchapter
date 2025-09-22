package com.example.tutorials.headers;

import com.example.tutorials.RabbitMQConnectionFactory;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.util.HashMap;
import java.util.Map;

/**
 * 生产者
 * Created by hanqf on 2025/9/22 16:25.
 * <p>
 * Headers 头部路由机制
 *
 * direct,fanout,topic等这些Exchange，都是以 routing-key 为关键字来进⾏消息路由的，
 * 但是这些Exchange有⼀个普遍的局限就是都是只⽀持⼀个字符串的形式，⽽不⽀持其他形式。
 * Headers类型的Exchange就是⼀种忽略routingKey的路由⽅式。
 * 他通过Headers来进⾏消息路由。这个headers是⼀个键值对，发送者可以在发送的时候定义⼀些键值对，接受者也可以在绑定时定义⾃⼰的键值对。
 * 当键值对匹配时，对应的消费者就能接收到消息。
 * 匹配的⽅式有两种，⼀种是all，表示需要所有的键值对都满⾜才⾏。另⼀种是any，表示只要满⾜其中⼀个键值就可以了。
 *
 * Headers交换机的性能相对⽐较低，因此官⽅并不建议⼤规模使⽤这种交换机，也没有把他列⼊基础的示例当中。
 */


public class Producer {
    private static final String EXCHANGE_NAME = "core-headers-exchange";

    // 启动生产者前要先启动消费者
    public static void main(String[] args) throws Exception {

        try (Connection connection = RabbitMQConnectionFactory.createConnection();
             Channel channel = connection.createChannel()) {

            // 声明交换机
            // 如果同名交换机已经存在，则此处声明的参数必须与服务端创建的队列的参数一致，否则会报错
            // HEADERS: 键值对匹配
            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.HEADERS);

            for (int i = 0; i < 10; i++) {
                // 发送消息
                String message = "Hello World!:" + i; // 消息内容
                // The map for the headers.
                Map<String, Object> headers = new HashMap<>();
                headers.put("loglevel", "error");
                headers.put("buslevel", "product");
                headers.put("syslevel", "admin");
                // 持久化消息
//                AMQP.BasicProperties props = MessageProperties.PERSISTENT_TEXT_PLAIN;
                AMQP.BasicProperties props = new AMQP.BasicProperties.Builder()
                        .contentType("text/plain")
                        .deliveryMode(2)
                        .priority(0)
                        .headers(headers)
                        .build();
                // 发送消息是指定 routing_key，消息将被发送到绑定了该routing_key的queue
                channel.basicPublish(EXCHANGE_NAME, "", props, message.getBytes());

                System.out.println(" [x] Sent '" + message + "'");
            }

        }
    }
}
