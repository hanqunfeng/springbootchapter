package com.example.tutorials.dead_letter_exchange;

import com.example.tutorials.RabbitMQConnectionFactory;
import com.example.tutorials.utils.RabbitMQUtil;
import com.rabbitmq.client.*;

/**
 * 消费者
 * Created by hanqf on 2025/9/22 16:25.
 * <p>
 * 死信队列
 */


public class Consumer {
    // 死信队列名称
    private static final String QUEUE_NAME = "core-dead-letter-queue";
    // 死信交换机名称
    private static final String DEAD_LETTER_EXCHANGE_NAME = "core-dead-letter-exchange";
    private static final String DEAD_LETTER_EXCHANGE_ROUTINGKEY = "core-dead-letter-routing_key";

    public static void main(String[] args) throws Exception {

        Connection connection = RabbitMQConnectionFactory.createConnection();
        Channel channel = connection.createChannel();
        // 声明队列
        // 如果队列已经存在，则此处声明的参数必须与服务端创建的队列的参数一致，否则会报错
        RabbitMQUtil.declareClassicQueue(channel, QUEUE_NAME);
        // 声明交换机
        // 如果同名交换机已经存在，则此处声明的参数必须与服务端创建的队列的参数一致，否则会报错
        channel.exchangeDeclare(DEAD_LETTER_EXCHANGE_NAME, BuiltinExchangeType.DIRECT);

        // 绑定队列到交换机，注意这里要设置 路由键，否则接收不到消息
        channel.queueBind(QUEUE_NAME, DEAD_LETTER_EXCHANGE_NAME, DEAD_LETTER_EXCHANGE_ROUTINGKEY);


        // 接收消息
        // 接收消息时触发
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [x] Received '" + message + "'");
            // 获取消息头，打印消息转化为死信的原因
            delivery.getProperties().getHeaders().forEach((key, value) -> {
                System.out.println("key: " + key + "; value: " + value);
            });

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
