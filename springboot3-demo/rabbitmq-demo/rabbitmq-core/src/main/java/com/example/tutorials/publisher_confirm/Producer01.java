package com.example.tutorials.publisher_confirm;

import com.example.tutorials.RabbitMQConnectionFactory;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

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
 *
 * 单次/批量确认
 */


public class Producer01 {
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

            int batchSize = 100;
            int outstandingMessageCount = 0;

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
                // 发送消息是指定 routing_key，消息将被发送到绑定了该routing_key的queue
                channel.basicPublish(EXCHANGE_NAME, ROUTING_KEY, props, message.getBytes());

//                | 方法 | 返回值 | 超时设置 | 行为特点 | 异常情况 |
//                |------|--------|----------|----------|----------|
//                | `waitForConfirms()` | `boolean` | 无超时（无限等待） | 等待所有未确认的消息被确认 | 中断异常 |
//                | `waitForConfirms(timeout)` | `boolean` | 自定义超时时间 | 在指定时间内等待确认 | 超时或中断异常 |
//                | `waitForConfirmsOrDie()` | `void` | 无超时（无限等待） | 等待确认，失败则抛出异常 | 直接抛出异常 |
//                | `waitForConfirmsOrDie(timeout)` | `void` | 自定义超时时间 | 在指定时间内等待确认，失败则抛出异常 | 直接抛出异常 |

                // 这里是每次发布消息都进行确认，效率较低，可以使用批量确认
//                channel.waitForConfirmsOrDie(5000); // 阻塞5秒

                outstandingMessageCount++;

                // 批量确认，每100条消息进行确认一次
                if (outstandingMessageCount == batchSize) {
                    channel.waitForConfirmsOrDie(5_000);
                    outstandingMessageCount = 0;
                }

                System.out.println(" [x] Sent '" + message + "'");
            }

            // 最后再验证是否还有未确认的消息
            if (outstandingMessageCount > 0) {
                channel.waitForConfirmsOrDie(5_000);
            }

        }
    }
}
