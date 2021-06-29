package com.example.rocketmq.common;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * <h1>发布消息公共方法</h1>
 *
 * Tag：用于区分过滤同一主题下的不同业务类型的消息，非常实用。在项目里往mq写入消息时，最好每条消息都带上tag，用于消费时根据业务过滤。
 * rocketmq-spring-boot-starter 中，设置 tag 的方式：在 topic后面加上 “:tagName”，源码中是以 “:”进行分割的，前面的 topic，后面的就是 tag
 *
 * “key”的设置方式，发送消息时在header中设置
 *
 * @author hanqf
 * @date 2021/6/24 15:43
 */

@Component
@Slf4j
public class MqSendService {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    /**
     * 同步发送,没有返回结果
     * 普通发送（这里的参数对象msg可以随意定义，可以发送个对象，也可以是字符串等）
     */
    public <T> void send(T msg, String topic, String group, String tag) {
        if (StringUtils.isBlank(topic) || StringUtils.isBlank(group)) {
            new Throwable("发送方topic或者group不能为空");
        }
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        RocketMqMessage message = new RocketMqMessage();
        message.setProducerTopic(topic);
        message.setProducerGroup(group);
        message.setProducerTag(tag);
        message.setContent(msg);
        message.setMsgKey(uuid);

        String destination = topic;
        if (StringUtils.isNotBlank(tag)) {
            destination = topic + ":" + tag;
        }
        rocketMQTemplate.convertAndSend(destination, message);
        //rocketMQTemplate.send(destination, MessageBuilder.withPayload(message).build()); // 等价于上面一行
    }


    /**
     * 同步发送消息，有返回结果
     * 发送带tag的消息
     *
     * @param msg
     * @param topic
     * @param group
     * @param tag
     * @return: org.apache.rocketmq.client.producer.SendResult
     **/
    public  <T> SendResult syncSend(T msg, String topic, String group, String tag) {
        if (StringUtils.isBlank(topic) || StringUtils.isBlank(group)) {
            new Throwable("发送方topic或者group不能为空");
        }
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        RocketMqMessage message = new RocketMqMessage();
        message.setProducerTopic(topic);
        message.setProducerGroup(group);
        message.setProducerTag(tag);
        message.setContent(msg);
        message.setMsgKey(uuid);
        // 发送消息
        Message messageFinal = MessageBuilder.withPayload(message).setHeader(RocketMQHeaders.KEYS, uuid).build();
        String destination = topic;
        if (StringUtils.isNotBlank(tag)) {
            destination = topic + ":" + tag;
        }
        SendResult result = rocketMQTemplate.syncSend(destination, messageFinal);
        log.info("成功发送消息,消息内容为:{},返回值为:{}", message, result);
        return result;
    }

    /**
     * 发送不带tag的消息
     *
     * @param msg
     * @param topic
     * @param group
     * @return: org.apache.rocketmq.client.producer.SendResult
     **/
    public <T> SendResult syncSend(T msg, String topic, String group) {
        return this.syncSend(msg, topic, group, null);
    }


    /**
     * 异步发送，没有返回结果
     *
     * @param msg
     * @param topic
     * @param group
     * @param tag
     */
    public <T> void asyncSend(T msg, String topic, String group, String tag) {
        if (StringUtils.isBlank(topic) || StringUtils.isBlank(group)) {
            new Throwable("发送方topic或者group不能为空");
        }
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        RocketMqMessage message = new RocketMqMessage();
        message.setProducerTopic(topic);
        message.setProducerGroup(group);
        message.setProducerTag(tag);
        message.setContent(msg);
        message.setMsgKey(uuid);
        // 发送消息
        Message messageFinal = MessageBuilder.withPayload(message).setHeader(RocketMQHeaders.KEYS, uuid).build();
        String destination = topic;
        if (StringUtils.isNotBlank(tag)) {
            destination = topic + ":" + tag;
        }


        rocketMQTemplate.asyncSend(destination, messageFinal, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("发送成功");
            }

            @Override
            public void onException(Throwable throwable) {
                log.info("发送失败");
            }
        });

    }


    /**
     * 发送延时消息<br/>
     * 在start版本中 延时消息一共分为18个等级分别为：1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h
     */
    public void sendDelayMsg(String msgBody, String topic, int messageTimeOut, int delayLevel) {
        rocketMQTemplate.syncSend(topic, MessageBuilder.withPayload(msgBody).build(), messageTimeOut, delayLevel);
    }

    /**
     * 发送单向消息（不关心发送结果，如日志）
     */
    public void sendOneWayMsg(String msgBody, String topic) {
        rocketMQTemplate.sendOneWay(topic, MessageBuilder.withPayload(msgBody).build());
    }


}


