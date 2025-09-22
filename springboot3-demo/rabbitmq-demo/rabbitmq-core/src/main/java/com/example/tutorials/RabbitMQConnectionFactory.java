package com.example.tutorials;

/**
 *
 * Created by hanqf on 2025/9/22 16:18.
 */


import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * RabbitMQ 连接工厂
 * 提供多种方式创建RabbitMQ连接
 */
public class RabbitMQConnectionFactory {

    // 默认配置
    private static final String DEFAULT_HOST = "52.82.28.255";
    private static final int DEFAULT_PORT = 5672;
    private static final String DEFAULT_USERNAME = "vtest";
    private static final String DEFAULT_PASSWORD = "123456";
    private static final String DEFAULT_VIRTUAL_HOST = "/vtest";


    private RabbitMQConnectionFactory() {
        // 私有构造函数，防止实例化
    }

    /**
     * 创建默认配置的连接
     */
    public static Connection createConnection() throws Exception {
        return createConnection(DEFAULT_HOST, DEFAULT_PORT, DEFAULT_USERNAME,
                DEFAULT_PASSWORD, DEFAULT_VIRTUAL_HOST);
    }

    /**
     * 创建指定主机和端口连接（使用默认认证信息）
     */
    public static Connection createConnection(String host, int port) throws Exception {
        return createConnection(host, port, DEFAULT_USERNAME, DEFAULT_PASSWORD, DEFAULT_VIRTUAL_HOST);
    }

    /**
     * 创建完整自定义配置的连接
     */
    public static Connection createConnection(String host, int port, String username,
                                              String password, String virtualHost) throws Exception {
        ConnectionFactory factory = createConnectionFactory(host, port, username, password, virtualHost);

        return factory.newConnection();
    }

    /**
     * 通过ConnectionFactory配置对象创建连接
     */
    public static Connection createConnection(ConnectionFactory factory) throws Exception {
        return factory.newConnection();
    }

    /**
     * 创建带自定义配置的ConnectionFactory
     */
    public static ConnectionFactory createConnectionFactory(String host, int port, String username,
                                                            String password, String virtualHost) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);
        factory.setPort(port);
        factory.setUsername(username);
        factory.setPassword(password);
        factory.setVirtualHost(virtualHost);
        return factory;
    }

    /**
     * 创建默认配置的ConnectionFactory
     */
    public static ConnectionFactory createDefaultConnectionFactory() {
        return createConnectionFactory(DEFAULT_HOST, DEFAULT_PORT, DEFAULT_USERNAME,
                DEFAULT_PASSWORD, DEFAULT_VIRTUAL_HOST);
    }
}
