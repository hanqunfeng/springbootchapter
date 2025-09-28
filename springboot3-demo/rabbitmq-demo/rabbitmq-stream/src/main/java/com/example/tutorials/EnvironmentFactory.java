package com.example.tutorials;

import com.rabbitmq.stream.Address;
import com.rabbitmq.stream.AddressResolver;
import com.rabbitmq.stream.Environment;
import com.rabbitmq.stream.EnvironmentBuilder;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class EnvironmentFactory {

    // 默认配置
    private static final String DEFAULT_HOST = "52.82.28.255";
    private static final int DEFAULT_PORT = 5552; // Stream 端口，3.9+ 支持
    private static final String DEFAULT_CLUSTER_ADDRESS = "43.192.82.161:5552,52.82.20.28:5552,68.79.57.194:5552";
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

    public static Environment createDefaultEnvironmentCluster() {
        String vhostEncoded = "%2fvtest"; // "/" 的 URL 编码
        // 1. 生成 URI 列表，自动替换 AMQP 默认端口 5672 为 Stream 默认端口 5552
        List<String> uris = Arrays.stream(DEFAULT_CLUSTER_ADDRESS.split(","))
                .map(String::trim)
                .map(hp -> {
                    String[] parts = hp.split(":");
                    String host = parts[0];
                    String port = parts.length > 1 ? parts[1] : "5552";
                    if ("5672".equals(port)) port = "5552";
                    return "rabbitmq-stream://" + DEFAULT_USERNAME + ":" + DEFAULT_PASSWORD + "@" + host + ":" + port + "/" + vhostEncoded;
                })
                .collect(Collectors.toList());

        // 2. 提取所有 host，用于 AddressResolver 映射
        Set<String> validHosts = new HashSet<>();
        Arrays.stream(DEFAULT_CLUSTER_ADDRESS.split(","))
                .map(addr -> addr.split(":")[0].trim())
                .forEach(validHosts::add);

        // 3. AddressResolver 动态匹配 broker 返回的内部 host
        AddressResolver resolver = node -> {
            String host = node.host();
            if (validHosts.contains(host)) {
                return new Address(host, 5552); // 返回 stream 默认端口
            }
            // 找不到匹配时返回第一个节点
            return new Address(validHosts.iterator().next(), 5552);
        };

        // 4. Environment.builder() 可以使用 uris + resolver
        return Environment.builder()
                .uris(uris)
                .addressResolver(resolver)
                .build();
    }
}
