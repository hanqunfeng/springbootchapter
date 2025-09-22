package com.example.tutorials.publisher_confirm;

import com.example.tutorials.RabbitMQConnectionFactory;
import com.rabbitmq.client.*;

/**
 * 生产者
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
 * <p>
 * 异步确认
 */


public class Producer02 {
    private static final String EXCHANGE_NAME = "core-publisher-confirm-exchange";
    private static final String ROUTING_KEY = "core-publisher-confirm-routing_key";

    // 启动生产者前要先启动消费者
    public static void main(String[] args) throws Exception {

        try (Connection connection = RabbitMQConnectionFactory.createConnection();
             Channel channel = connection.createChannel()) {

            // 声明交换机
            // 如果同名交换机已经存在，则此处声明的参数必须与服务端创建的队列的参数一致，否则会报错
            // DIRECT，这里可以是任意类型的交换机
            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);

            // 开启发送者确认模式，默认是关闭的
            channel.confirmSelect();


            // 添加异步确认监听器
            ConfirmCallback ackCallback = (deliveryTag, multiple) -> {
                System.out.println("消息发送成功，deliveryTag：" + deliveryTag + "，multiple：" + multiple);
            };
            ConfirmCallback nackCallback = (deliveryTag, multiple) -> {
                System.out.println("消息发送失败，deliveryTag：" + deliveryTag + "，multiple：" + multiple);
            };
            channel.addConfirmListener(ackCallback, nackCallback);

            for (int i = 0; i < 1000; i++) {
                // 发送消息
                String message = "Hello World!:" + i; // 消息内容
                // 持久化消息
//                AMQP.BasicProperties props = MessageProperties.PERSISTENT_TEXT_PLAIN;
                AMQP.BasicProperties props = new AMQP.BasicProperties.Builder()
                        .contentType("text/plain")
                        .deliveryMode(2)
                        .priority(0)
                        .build();

                // 异步确认每次发布消息前要执行如下代码
                long sequenceNumber = channel.getNextPublishSeqNo();
                // 发送消息是指定 routing_key，消息将被发送到绑定了该routing_key的queue
                channel.basicPublish(EXCHANGE_NAME, ROUTING_KEY, props, message.getBytes());

                System.out.println(" [x] Sent '" + message + "' sequenceNumber:" + sequenceNumber );
            }

            // 因为发送者确认模式是异步的，所以这里需要等待所有消息发送完成，等待所有消息发送完成，这里需要等待10秒
            System.out.println("所有消息发送完成，等待确认...");
            Thread.sleep(10000); // 等待10秒
            System.out.println("程序结束");
        }
    }
}
