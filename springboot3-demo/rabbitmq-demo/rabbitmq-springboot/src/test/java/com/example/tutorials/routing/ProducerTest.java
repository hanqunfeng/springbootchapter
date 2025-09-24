package com.example.tutorials.routing;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * Created by hanqf on 2025/9/23 17:20.
 */

@SpringBootTest
public class ProducerTest {

    @Autowired
    RoutingProducer routingProducer;


    @Test
    public void sendMap() throws InterruptedException {
        Map<String, Object> message = new HashMap<>();
        for (int i = 0; i < 5; i++) {
            message.put("message", "hello world ** " + i);
            System.out.println("send map: " + i + " : " + routingProducer.send1(message));
        }

        for (int i = 0; i < 5; i++) {
            message.put("message", "hello world ## " + i);
            System.out.println("send map: " + i + " : " + routingProducer.send2(message));
        }

        Thread.sleep(10000);

    }


}
