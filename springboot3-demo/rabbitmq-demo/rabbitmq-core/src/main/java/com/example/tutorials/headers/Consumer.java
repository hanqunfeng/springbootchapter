package com.example.tutorials.headers;

import com.example.tutorials.RabbitMQConnectionFactory;
import com.example.tutorials.utils.RabbitMQUtil;
import com.rabbitmq.client.*;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 消费者
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


public class Consumer {
    private static final String QUEUE_NAME = "core-headers-queue";
    private static final String EXCHANGE_NAME = "core-headers-exchange";

    public static void main(String[] args) throws Exception {

        Connection connection = RabbitMQConnectionFactory.createConnection();
        Channel channel = connection.createChannel();
        // 声明队列
        // 如果队列已经存在，则此处声明的参数必须与服务端创建的队列的参数一致，否则会报错
        RabbitMQUtil.declareClassicQueue(channel, QUEUE_NAME);
        // 声明交换机
        // 如果同名交换机已经存在，则此处声明的参数必须与服务端创建的队列的参数一致，否则会报错
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.HEADERS);

        Map<String, Object> headers = new HashMap<String, Object>();
        headers.put("x-match", "any"); //x-match:特定的参数。all表示必须全部匹配才算成功。any表示只要匹配⼀个就算成功。
        headers.put("loglevel", "info");
        headers.put("buslevel", "product");
        headers.put("syslevel", "admin");
        // 绑定队列到交换机，与其它类型相比，这里需要多传递一个 headers 参数
        channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "", headers);

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
