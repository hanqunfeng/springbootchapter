package com.example.tutorials.stream;

import com.example.tutorials.RabbitMQConstants;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * Created by hanqf on 2025/9/23 17:22.
 */

@Component
public class StreamConsumer {
    // 这种方式会自动记录偏移量，且自动确认，concurrency = "1" 表示每次只处理一条消息
    @RabbitListener(queues = {RabbitMQConstants.QUEUE_NAME_STREAM}, messageConverter = "jsonConverter", concurrency = "1")
    public void receiveStream(Map<String, Object> message) {
        System.out.println("consumer received stream message map: " + message);
    }


    // 手工确认消息，手工记录偏移量
    @RabbitListener(queues = {RabbitMQConstants.QUEUE_NAME_STREAM}, ackMode = "MANUAL", concurrency = "1")
    public void receiveStream2(Map<String, Object> message, Channel channel) throws IOException {
        // stream 队列 QoS 必须设置
//        channel.basicQos(5);

        // 接收消息
        // 接收消息时触发
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String mess = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [x] Received '" + mess + "'");

            // 手动应答
            // deliveryTag: 消息的标识，通过它来确认消息被消费了，不是偏移量，其每次都是从 1 开始
            long deliveryTag = delivery.getEnvelope().getDeliveryTag();
            System.out.println("deliveryTag: " + deliveryTag);

            // 获取消息的属性，其中 x-stream-offset 表示当前消费到的消息的偏移量
            // 可以从回调中取出最后的偏移量并保存，下次启动时从该偏移量开始消费
            delivery.getProperties().getHeaders().forEach((key, value) -> {
                System.out.println("key: " + key + "; value: " + value);
            });

            // false: 只拒绝当前 deliveryTag 对应的消息
            // true: 拒绝当前 deliveryTag 及之前所有未确认的消息
            boolean multiple = false;
            channel.basicAck(deliveryTag, multiple); // 确认消息，这里注意 stream 队列中的消息即便做了应对，也不会删除

            boolean requeue = false; // true: 重新入队
//            channel.basicNack(deliveryTag, multiple, false); // 拒绝消息
        };

        // 队列被删除时触发
        CancelCallback cancelCallback = consumerTag -> System.out.println("canceled message consumerTag: " + consumerTag + "; ");


        // 消费时，必须指定offset。
        // 可选的值：
        // first: 从日志队列中第一个可消费的消息开始消费
        // last: 消费消息日志中最后一个消息
        // next: 相当于不指定offset，以前的消息是消费不到的，只会获取此刻之后的最新的消息
        // Offset: 一个数字型的偏移量
        // Timestamp:一个代表时间的 Data类型 变量，表示从这个时间点开始消费。例如 一个小时前 Date timestamp = new Date(System.currentTimeMillis() - 60 * 60 * 1_000)
        Map<String, Object> consumeParam = new HashMap<>();
//        consumeParam.put("x-stream-offset", "first"); // 每次从第一条 开始消费
        consumeParam.put("x-stream-offset", "last"); // 每次从第一条 开始消费
//        consumeParam.put("x-stream-offset", 5); // 指定偏移量，从第 6 条开始消费，可以从回调中取出最后的偏移量并保存，下次启动时从该偏移量开始消费
        // 建议手动应答
        channel.basicConsume(RabbitMQConstants.QUEUE_NAME_STREAM, false, consumeParam, deliverCallback, cancelCallback);

        System.out.println("consumer received stream message map: " + message);
    }
}
