package com.example.tutorials;

import java.util.Map;

/**
 * RabbitMQ常量
 * Created by hanqf on 2025/9/23 17:23.
 */


public class RabbitMQConstants {

    // 默认消息
    public static final String DEFAULT_MESSAGE = "Hello World!";
    public static final Map<String, Object> DEFAULT_MESSAGE_MAP = Map.of("message", DEFAULT_MESSAGE, "version", "1.0.0");

    // hello_world 队列名称
    public static final String QUEUE_NAME_HELLO_WORLD = "springboot-hello-world-queue";
    public static final String QUEUE_NAME_HELLO_WORLD_MAP = "springboot-hello-world-queue-map";
    // stream 队列名称
    public static final String QUEUE_NAME_STREAM = "springboot-stream-queue";

    // work_queues 队列名称
    public static final String QUEUE_NAME_WORK_QUEUES = "springboot-work-queues-queue";

    // pub_sub 队列名称
    public static final String QUEUE_NAME_PUBLISH_SUBSCRIBE_1 = "springboot-publish-subscribe-queue1";
    public static final String QUEUE_NAME_PUBLISH_SUBSCRIBE_2 = "springboot-publish-subscribe-queue2";
    // pub_sub 交换机名称
    public static final String EXCHANGE_NAME_PUBLISH_SUBSCRIBE = "springboot-publish-subscribe-exchange";

    // routing 队列名称
    public static final String QUEUE_NAME_ROUTING_1 = "springboot-routing-queue1";
    public static final String QUEUE_NAME_ROUTING_2 = "springboot-routing-queue2";
    // routing 交换机名称
    public static final String EXCHANGE_NAME_ROUTING = "springboot-routing-exchange";
    // routing 绑定键名称
    public static final String ROUTING_KEY_ROUTING_1 = "springboot.routing.routing1";
    public static final String ROUTING_KEY_ROUTING_2 = "springboot.routing.routing2";

    // topic 队列名称
    public static final String QUEUE_NAME_TOPIC_1 = "springboot-topic-queue1";
    public static final String QUEUE_NAME_TOPIC_2 = "springboot-topic-queue2";
    // topic 交换机名称
    public static final String EXCHANGE_NAME_TOPIC = "springboot-topic-exchange";
    // routing 绑定键名称
    public static final String ROUTING_KEY_TOPIC_1 = "my.topic.routing";
    public static final String ROUTING_KEY_TOPIC_2 = "springboot.topic.routing";
    // topic 模糊匹配路由
    public static final String ROUTING_KEY_TOPIC_P1 = "*.topic.routing"; // * 匹配一个单词
    public static final String ROUTING_KEY_TOPIC_P2 = "springboot.#"; // # 匹配多个单词

    // header 队列名称
    public static final String QUEUE_NAME_HEADER_1 = "springboot-header-queue1";
    public static final String QUEUE_NAME_HEADER_2 = "springboot-header-queue2";
    public static final String QUEUE_NAME_HEADER_3 = "springboot-header-queue3";
    public static final String QUEUE_NAME_HEADER_4 = "springboot-header-queue4";
    // header 交换机名称
    public static final String EXCHANGE_NAME_HEADER = "springboot-header-exchange";

    private RabbitMQConstants() {
    }
}
