package com.example.tutorials.pub_sub;

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
    PubSubProducer pubSubProducer;


    @Test
    public void sendMap() throws InterruptedException {
        Map<String, Object> message = new HashMap<>();
        for (int i = 0; i < 10; i++) {
            message.put("message", "hello world " + i);
            System.out.println("send map: " + i + " : " + pubSubProducer.send(message));
        }

        Thread.sleep(10000);

    }


}
