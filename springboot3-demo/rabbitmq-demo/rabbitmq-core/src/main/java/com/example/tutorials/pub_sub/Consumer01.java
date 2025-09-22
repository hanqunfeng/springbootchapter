package com.example.tutorials.pub_sub;

import com.example.tutorials.RabbitMQConnectionFactory;
import com.example.tutorials.utils.RabbitMQUtil;
import com.rabbitmq.client.*;
import java.nio.charset.StandardCharsets;

/**
 * 消费者
 * Created by hanqf on 2025/9/22 16:25.
 *
 * Publish/Subscribe 订阅 发布 机制
 *
 * 把preducer与Consumer进⾏进⼀步的解耦。producer只负责发送消息，⾄于消息进⼊哪个queue，由exchange来分配。
 */


public class Consumer01 {
    private static final String QUEUE_NAME = "core-pub-sub-queue01";
    private static final String EXCHANGE_NAME = "core-pub-sub-exchange";

    public static void main(String[] args) throws Exception {

        Connection connection = RabbitMQConnectionFactory.createConnection();
        Channel channel = connection.createChannel();
        // 声明队列
        // 如果队列已经存在，则此处声明的参数必须与服务端创建的队列的参数一致，否则会报错
        RabbitMQUtil.declareClassicQueue(channel, QUEUE_NAME);
        // 声明交换机
        // 如果同名交换机已经存在，则此处声明的参数必须与服务端创建的队列的参数一致，否则会报错
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT);

        // 绑定队列到交换机
        channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "");

        // 接收消息
        // 接收消息时触发
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println(" [x] Received '" + message + "'");

            // 手动应答
            // deliveryTag: 消息的标识，通过它来确认消息被消费了
            final long deliveryTag = delivery.getEnvelope().getDeliveryTag();
            // false: 只拒绝当前 deliveryTag 对应的消息
            // true: 拒绝当前 deliveryTag 及之前所有未确认的消息
            boolean multiple = true;
            channel.basicAck(deliveryTag, multiple);
        };

        // 队列内删除时触发
        CancelCallback cancelCallback = consumerTag -> System.out.println("canceled message consumerTag: " + consumerTag + "; ");

        // 建议手动应答
        channel.basicConsume(QUEUE_NAME, false, deliverCallback, cancelCallback);
    }
}
