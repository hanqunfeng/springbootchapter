package com.example.producer;

import com.example.message.QueenMessage;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.jms.Topic;

/**
 * <h1>发布订阅消息生产者</h1>
 * Created by hanqf on 2021/5/14 15:29.
 */

@RestController
public class TopicProducerController {

    @Resource
    private JmsMessagingTemplate jmsMessagingTemplate;

    @Resource(name = "messageTopic")
    private Topic messageTopic;

    @RequestMapping("/sendtopic")
    public QueenMessage send(){
        QueenMessage queenMessage = new QueenMessage("测试","测试内容");
        jmsMessagingTemplate.convertAndSend(messageTopic,queenMessage);
        return queenMessage;
    }
}
