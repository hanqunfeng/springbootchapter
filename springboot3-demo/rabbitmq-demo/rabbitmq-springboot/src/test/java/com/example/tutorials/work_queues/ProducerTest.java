package com.example.tutorials.work_queues;

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
    WorkQueuesProducer workQueuesProducer;


    @Test
    public void sendMap() throws InterruptedException {
        Map<String, Object> message = new HashMap<>();
        for (int i = 0; i < 20; i++) {
            message.put("message", "hello world " + i);
            System.out.println("send map: " + i + " : " + workQueuesProducer.send(message));
        }

        Thread.sleep(10000);

    }


}
