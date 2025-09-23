package com.example.tutorials.hello_world;

import com.example.tutorials.EnvironmentFactory;
import com.rabbitmq.stream.ByteCapacity;
import com.rabbitmq.stream.Consumer;
import com.rabbitmq.stream.Environment;
import com.rabbitmq.stream.OffsetSpecification;

import java.io.IOException;

/**
 * Stream 接收端
 * Created by hanqf on 2025/9/23 15:02.
 *
 * rabbitmq-plugins enable rabbitmq_stream rabbitmq_stream_management
 */


public class Receive {
    private static final String STREAM_NAME = "hello-java-stream";
    public static void main(String[] args) throws IOException {
        // 创建Environment，建立连接
        Environment environment = EnvironmentFactory.createDefaultEnvironment();

        // 创建stream队列，如果不存在则创建，存在则必须与创建的参数一致
        environment.streamCreator().stream(STREAM_NAME).maxLengthBytes(ByteCapacity.GB(5)).create();

        // 创建consumer
        Consumer consumer = environment.consumerBuilder()
                .stream(STREAM_NAME)
//                .name("my-consumer") // 设置consumer名称
                .offset(OffsetSpecification.first()) // 从第一个offset开始消费
                .messageHandler((unused, message) -> {
                    System.out.println("Received message: " + new String(message.getBodyAsBinary()));
                    System.out.println("Received offset: " + unused.offset());
                })
                .build();

        System.out.println(" [x]  Press Enter to close the consumer...");
        System.in.read(); // 阻塞等待用户输入，此处就是为了持续运行，否则consumer会被关闭
        consumer.close();
        environment.close();
    }
}
