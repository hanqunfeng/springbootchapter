package com.example.tutorials.utils;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * rabbitmq 工具类
 * Created by hanqf on 2025/9/22 17:38.
 */


public class RabbitMQUtil {

    // 声明一个 Classic 队列
    public static AMQP.Queue.DeclareOk declareClassicQueue(Channel channel, String queueName) throws IOException {
        return declareClassicQueue(channel, queueName, null);
    }

    public static AMQP.Queue.DeclareOk declareClassicQueue(Channel channel, String queueName, Map<String, Object> arguments) throws IOException {
        return declareClassicQueue(channel, queueName, true, false, false, arguments);
    }

    public static AMQP.Queue.DeclareOk declareClassicQueue(Channel channel, String queueName, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments) throws IOException {
        return channel.queueDeclare(queueName, durable, exclusive, autoDelete, arguments);
    }

    // 声明一个 Quorum 队列
    // 对于Quorum类型，durable参数就必须是true了，设置成false的话，会报错。同样，exclusive参数必须设置为false
    public static AMQP.Queue.DeclareOk declareQuorumQueue(Channel channel, String queueName) throws IOException {
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-queue-type", "quorum");
        return declareQuorumQueue(channel, queueName, arguments);
    }

    public static AMQP.Queue.DeclareOk declareQuorumQueue(Channel channel, String queueName, Map<String, Object> arguments) throws IOException {
        if (arguments != null) {
            if (!arguments.containsKey("x-queue-type") || !arguments.get("x-queue-type").equals("quorum")) {
                arguments.put("x-queue-type", "quorum");
            }
        } else {
            arguments = new HashMap<>();
            arguments.put("x-queue-type", "quorum");
        }
        // 创建队列
        return channel.queueDeclare(queueName, true, false, false, arguments);
    }


    // 声明一个 Stream 队列
    // durable参数必须是true，exclusive必须是false。
    // x-max-length-bytes 表示⽇志⽂件的最⼤字节数。x-stream-max-segment-size-bytes 每⼀个⽇志⽂件的最⼤⼤⼩。这两个是可选参数，通常为了防⽌stream⽇志⽆限制累计，都会配合stream队列⼀起声明。
    public static AMQP.Queue.DeclareOk declareStreamQueue(Channel channel, String queueName) throws IOException {
        return declareStreamQueue(channel, queueName, null);
    }

    public static AMQP.Queue.DeclareOk declareStreamQueue(Channel channel, String queueName, Map<String, Object> arguments) throws IOException {
        if (arguments != null) {
            if (!arguments.containsKey("x-queue-type") || !arguments.get("x-queue-type").equals("stream")) {
                arguments.put("x-queue-type", "stream");
                arguments.put("x-max-length-bytes", 20_000_000_000L); // maximum stream size: 20 GB
                arguments.put("x-stream-max-segment-size-bytes", 100_000_000); // size of segment files: 100
            }
        } else {
            arguments = new HashMap<>();
            arguments.put("x-queue-type", "stream");
            arguments.put("x-max-length-bytes", 20_000_000_000L); // maximum stream size: 20 GB
            arguments.put("x-stream-max-segment-size-bytes", 100_000_000); // size of segment files: 100
        }
        // 创建队列
        // 创建队列
        return channel.queueDeclare(queueName, true, false, false, arguments);
    }

    // 声明一个 交换机
    // durable 默认为 false，这里改为 true
    public static AMQP.Exchange.DeclareOk declareExchange(Channel channel, String exchangeName, BuiltinExchangeType exchangeType) throws IOException {
        return declareExchange(channel, exchangeName, exchangeType, true);
    }


    public static AMQP.Exchange.DeclareOk declareExchange(Channel channel, String exchangeName, BuiltinExchangeType exchangeType, boolean durable) throws IOException {
        return channel.exchangeDeclare(exchangeName, exchangeType.getType(), durable);
    }
}

