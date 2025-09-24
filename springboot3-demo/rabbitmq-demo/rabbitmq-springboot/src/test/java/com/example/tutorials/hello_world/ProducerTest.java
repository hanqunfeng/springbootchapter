package com.example.tutorials.hello_world;

import com.example.tutorials.RabbitMQConstants;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

/**
 *
 * Created by hanqf on 2025/9/23 17:20.
 */

@SpringBootTest
public class ProducerTest {

    @Autowired
    HelloWorldProducer helloWorldProducer;

    @Test
    public void sendString() {
        String message = RabbitMQConstants.DEFAULT_MESSAGE;
        System.out.println("send string: " + helloWorldProducer.send(message));
    }

    @Test
    public void sendMap() throws InterruptedException {
        Map<String,Object> message = RabbitMQConstants.DEFAULT_MESSAGE_MAP;
        System.out.println("send map: " + helloWorldProducer.send(message));
//        Thread.sleep(10000L);
    }


}
