package com.example.tutorials.hello_world;

import com.example.tutorials.RabbitMQConnectionFactory;
import com.example.tutorials.utils.RabbitMQUtil;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;

/**
 * 消费者
 * Created by hanqf on 2025/9/22 16:25.
 *
 * hello world体验
 *
 * 最直接的⽅式，P端发送⼀个消息到⼀个指定的queue，中间不需要任何exchange规则。C端按queue⽅式进⾏消费。
 */


public class Consumer {
    private static final String QUEUE_NAME = "core-hello-world-queue";

    public static void main(String[] args) throws Exception {

        Connection connection = RabbitMQConnectionFactory.createConnection();
        Channel channel = connection.createChannel();
        // 声明队列
        // 如果队列已经存在，则此处声明的参数必须与服务端创建的队列的参数一致，否则会报错
        RabbitMQUtil.declareClassicQueue(channel, QUEUE_NAME);

        // 接收消息
        // 接收消息时触发
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [x] Received '" + message + "'");

            // 手动应答
            // deliveryTag: 消息的标识，通过它来确认消息被消费了
            final long deliveryTag = delivery.getEnvelope().getDeliveryTag();
            // false: 只拒绝当前 deliveryTag 对应的消息
            // true: 拒绝当前 deliveryTag 及之前所有未确认的消息
            boolean multiple = false;
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
