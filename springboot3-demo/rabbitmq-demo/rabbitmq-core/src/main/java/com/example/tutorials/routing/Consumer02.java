package com.example.tutorials.routing;

import com.example.tutorials.RabbitMQConnectionFactory;
import com.example.tutorials.utils.RabbitMQUtil;
import com.rabbitmq.client.*;

import java.nio.charset.StandardCharsets;

/**
 * 消费者
 * Created by hanqf on 2025/9/22 16:25.
 * <p>
 * Routing 基于内容的路由
 * <p>
 * 把preducer与Consumer进⾏进⼀步的解耦。producer只负责发送消息，⾄于消息进⼊哪个queue，由exchange来分配。
 * 增加⼀个路由配置，指定exchange如何将不同类别的消息分发到不同的queue上
 */


public class Consumer02 {
    private static final String QUEUE_NAME = "core-routing-queue02";
    private static final String EXCHANGE_NAME = "core-routing-exchange";
    private static final String ROUTING_KEY = "core-routing-routing_key02";

    public static void main(String[] args) throws Exception {

        Connection connection = RabbitMQConnectionFactory.createConnection();
        Channel channel = connection.createChannel();
        // 声明队列
        // 如果队列已经存在，则此处声明的参数必须与服务端创建的队列的参数一致，否则会报错
        RabbitMQUtil.declareClassicQueue(channel, QUEUE_NAME);
        // 声明交换机
        // 如果同名交换机已经存在，则此处声明的参数必须与服务端创建的队列的参数一致，否则会报错
        RabbitMQUtil.declareExchange(channel, EXCHANGE_NAME, BuiltinExchangeType.DIRECT);

        // 绑定队列到交换机
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
            channel.basicAck(deliveryTag, multiple); // 确认消息

            boolean requeue = false; // true: 重新入队
//            channel.basicNack(deliveryTag, multiple, false); // 拒绝消息
        };

        // 队列被删除时触发
        CancelCallback cancelCallback = consumerTag -> System.out.println("canceled message consumerTag: " + consumerTag + "; ");

        // 建议手动应答
        channel.basicConsume(QUEUE_NAME, false, deliverCallback, cancelCallback);
    }
}
