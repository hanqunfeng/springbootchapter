package com.example.tutorials.publisher_confirm;

import com.example.tutorials.RabbitMQConnectionFactory;
import com.example.tutorials.utils.RabbitMQUtil;
import com.rabbitmq.client.*;

import java.nio.charset.StandardCharsets;

/**
 * 消费者
 * Created by hanqf on 2025/9/22 16:25.
 * <p>
 * Publisher Confirms 发送者消息确认
 * <p>
 * RabbitMQ的消息可靠性是⾮常⾼的，但是他以往的机制都是保证消息发送到了MQ之后，可以推送到消费者消费，不会丢失消息。
 * 但是发送者发送消息是否成功是没有保证的。
 * 我们可以回顾下，发送者发送消息的基础API：Producer.basicPublish⽅法是没有返回值的，
 * 也就是说，⼀次发送消息是否成功，应⽤是不知道的，这在业务上就容易造成消息丢失。
 * ⽽这个模块就是通过给发送者提供⼀些确认机制，来保证这个消息发送的过程是成功的。
 * 发送者确认模式默认是不开启的，所以如果需要开启发送者确认模式，需要⼿动在channel中进⾏声明。
 */


public class Consumer {
    private static final String QUEUE_NAME = "core-publisher-confirm-queue";
    private static final String EXCHANGE_NAME = "core-publisher-confirm-exchange";
    private static final String ROUTING_KEY = "core-publisher-confirm-routing_key";

    public static void main(String[] args) throws Exception {

        Connection connection = RabbitMQConnectionFactory.createConnection();
        Channel channel = connection.createChannel();
        // 声明队列
        // 如果队列已经存在，则此处声明的参数必须与服务端创建的队列的参数一致，否则会报错
        RabbitMQUtil.declareClassicQueue(channel, QUEUE_NAME);
        // 声明交换机
        // 如果同名交换机已经存在，则此处声明的参数必须与服务端创建的队列的参数一致，否则会报错
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);

        // 绑定队列到交换机，与其它类型相比，这里需要多传递一个 headers 参数
        channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, ROUTING_KEY);

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
