package com.example.tutorials.hello_world;

import com.example.tutorials.EnvironmentFactory;
import com.rabbitmq.stream.*;

import java.io.IOException;

/**
 * Stream 发送端
 * Created by hanqf on 2025/9/23 14:24.
 *
 * rabbitmq-plugins enable rabbitmq_stream rabbitmq_stream_management
 */


public class Send {
    private static final String STREAM_NAME = "hello-java-stream";

    public static void main(String[] args) throws IOException {
        // 创建Environment，建立连接
//        Environment environment = EnvironmentFactory.createDefaultEnvironment();
        Environment environment = EnvironmentFactory.createDefaultEnvironmentCluster();

        // 创建stream
        environment.streamCreator()
                .stream(STREAM_NAME)
                .maxLengthBytes(ByteCapacity.GB(5)) // 最大存储空间 5G
                .create();

        // 创建producer
        Producer producer = environment.producerBuilder()
                .stream(STREAM_NAME)
                .build();

        String messageString = "Hello, World!";
        // 创建消息
        Message message = producer.messageBuilder()
                .addData(messageString.getBytes())
                .build();

        ConfirmationHandler confirmationHandler = confirmationStatus -> {
            System.out.println(" [x] '" + messageString + "' message confirmed");
            System.out.println(confirmationStatus.isConfirmed());
            System.out.println(confirmationStatus.getCode());
            System.out.println(new String(confirmationStatus.getMessage().getBodyAsBinary()));

        };

        // 发送消息
        producer.send(message, confirmationHandler);

        System.out.println(" [x] 'Hello, World!' message sent");

        System.out.println(" [x] Press Enter to close the producer...");
        System.in.read();
        producer.close();
        environment.close();
    }
}
