package com.example.rocketmq.producer;

import com.example.rocketmq.common.MqSendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * <h1>消费发送Controller</h1>
 *
 * @author hanqf
 * @date 2021/6/24 15:47
 */

@RestController
public class Producer01Controller {

    @Value(value = "${rocketmq.producer.topic}")
    private String topic;

    @Value(value = "${rocketmq.producer.group}")
    private String group;

    @Autowired
    private MqSendService mqSendService;

    @GetMapping("/rocketmq/sendMsg")
    public String sendMsg() {
        List<String> list=new ArrayList<>();
        list.add("1");
        list.add("2");
        list.add("3");
        mqSendService.syncSend(list,topic,group);
        return "syncSend message success";
    }

    @GetMapping("/rocketmq/sendMsg02")
    public String sendMsg02() {
        List<String> list=new ArrayList<>();
        list.add("1");
        list.add("2");
        list.add("3");
        mqSendService.syncSend(list,topic,group,"tag02");
        return "syncSend message success";
    }
}
