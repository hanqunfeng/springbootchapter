package com.example.producer;

import com.example.message.QueenMessage;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.jms.Queue;

/**
 * <h1>生产者实例</h1>
 * Created by hanqf on 2021/5/14 14:57.
 */


@RestController
public class P2pProducerController {
    @Resource
    private JmsMessagingTemplate jmsMessagingTemplate;

    @Resource(name = "messageQueue")
    private Queue messageQueue;

    @RequestMapping("/send")
    public QueenMessage send(){
        QueenMessage queenMessage = new QueenMessage("测试","测试内容");

        jmsMessagingTemplate.convertAndSend(messageQueue,queenMessage);
        return queenMessage;
    }
}
