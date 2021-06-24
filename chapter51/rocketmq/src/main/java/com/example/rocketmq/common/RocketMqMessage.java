package com.example.rocketmq.common;

import lombok.Data;

import java.io.Serializable;

/**
 * <h1>消息封装类</h1>
 *
 * @author hanqf
 * @date 2021/6/24 15:40
 */

@Data
public class RocketMqMessage<T> implements Serializable {

    private static final long serialVersionUID = -2925685420164374547L;

    /**
     * 消息内容
     */
    private T content;

    /**
     * 消息的key
     */
    private String msgKey;

    /**
     * topic
     */
    private String producerTopic;

    /**
     * group
     */
    private String producerGroup;

    /**
     * tag
     */
    private String producerTag;
}

