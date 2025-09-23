package com.example.tutorials;

import com.rabbitmq.stream.Address;
import com.rabbitmq.stream.Environment;
import com.rabbitmq.stream.EnvironmentBuilder;

public class EnvironmentFactory {

    // 默认配置
    private static final String DEFAULT_HOST = "52.82.28.255";
    private static final int DEFAULT_PORT = 5552; // Stream 端口，3.9+ 支持
    private static final String DEFAULT_USERNAME = "vtest";
    private static final String DEFAULT_PASSWORD = "123456";
    private static final String DEFAULT_VIRTUAL_HOST = "/vtest";

    /**
     * 创建一个基于默认配置的 RabbitMQ Stream 环境
     * <p>
     * rabbitmq-plugins enable rabbitmq_stream rabbitmq_stream_management
     *
     * @return Environment 实例
     */
    public static Environment createDefaultEnvironment() {
        return Environment.builder()
                .host(DEFAULT_HOST)
                .port(DEFAULT_PORT)
                .username(DEFAULT_USERNAME)
                .password(DEFAULT_PASSWORD)
                .virtualHost(DEFAULT_VIRTUAL_HOST)
                .addressResolver(address -> new Address(DEFAULT_HOST, DEFAULT_PORT)) // 不加这个地址解析器会报错
                .build();
    }

    public static EnvironmentBuilder createDefaultEnvironmentBuilder() {
        return Environment.builder()
                .host(DEFAULT_HOST)
                .port(DEFAULT_PORT)
                .username(DEFAULT_USERNAME)
                .password(DEFAULT_PASSWORD)
                .virtualHost(DEFAULT_VIRTUAL_HOST)
                .addressResolver(address -> new Address(DEFAULT_HOST, DEFAULT_PORT)); // 不加这个地址解析器会报错

    }

    /**
     * 使用自定义参数创建 RabbitMQ Stream 环境
     *
     * @param host        RabbitMQ 主机地址
     * @param port        RabbitMQ 端口
     * @param username    RabbitMQ 用户名
     * @param password    RabbitMQ 密码
     * @param virtualHost RabbitMQ 虚拟主机
     * @return Environment 实例
     */
    public static Environment createCustomEnvironment(
            String host, int port, String username, String password, String virtualHost) {
        return Environment.builder()
                .host(host)
                .port(port)
                .username(username)
                .password(password)
                .virtualHost(virtualHost)
                .addressResolver(address -> new Address(host, port)) // 不加这个地址解析器会报错
                .build();
    }
}
