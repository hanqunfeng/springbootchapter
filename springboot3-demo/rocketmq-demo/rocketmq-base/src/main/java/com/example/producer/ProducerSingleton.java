package com.example.producer;

import org.apache.rocketmq.client.apis.ClientConfiguration;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.ClientServiceProvider;
import org.apache.rocketmq.client.apis.producer.Producer;
import org.apache.rocketmq.client.apis.producer.ProducerBuilder;
import org.apache.rocketmq.client.apis.producer.TransactionChecker;

/**
 * https://github.com/apache/rocketmq-clients/blob/master/java/client/src/main/java/org/apache/rocketmq/client/java/example/ProducerSingleton.java
 * 生产者工厂类
 * 每个客户端将与进程内的服务器节点建立独立的连接。
 *
 * <p>大多数情况下，单例模式可以满足更高并发的要求。
 * 如果需要多个连接，请考虑适当增加客户端数量。
 */
public class ProducerSingleton {
    private static volatile Producer PRODUCER;
    private static volatile Producer TRANSACTIONAL_PRODUCER;
    // 账号和密码
    private static final String ACCESS_KEY = "yourAccessKey";
    private static final String SECRET_KEY = "yourSecretKey";
    // 接入点地址，需要设置成Proxy的地址和端口列表，一般是xxx:8080;xxx:8081。
    private static final String ENDPOINTS = "68.79.47.19:8081";

    private ProducerSingleton() {
    }

    private static Producer buildProducer(TransactionChecker checker, String... topics) throws ClientException {
        final ClientServiceProvider provider = ClientServiceProvider.loadService();
        // Credential provider is optional for client configuration.
        // This parameter is necessary only when the server ACL is enabled. Otherwise,
        // it does not need to be set by default.
//        SessionCredentialsProvider sessionCredentialsProvider =
//                new StaticSessionCredentialsProvider(ACCESS_KEY, SECRET_KEY);
        ClientConfiguration clientConfiguration = ClientConfiguration.newBuilder()
                .setEndpoints(ENDPOINTS)
                // On some Windows platforms, you may encounter SSL compatibility issues. Try turning off the SSL option in
                // client configuration to solve the problem please if SSL is not essential.
                // .enableSsl(false)
//                .setCredentialProvider(sessionCredentialsProvider)
                .build();
        final ProducerBuilder builder = provider.newProducerBuilder()
                .setClientConfiguration(clientConfiguration)
                // Set the topic name(s), which is optional but recommended. It makes producer could prefetch
                // the topic route before message publishing.
                .setTopics(topics);
        if (checker != null) {
            // Set the transaction checker.
            builder.setTransactionChecker(checker);
        }
        return builder.build();
    }

    public static Producer getInstance(String... topics) throws ClientException {
        if (null == PRODUCER) {
            synchronized (ProducerSingleton.class) {
                if (null == PRODUCER) {
                    PRODUCER = buildProducer(null, topics);
                }
            }
        }
        return PRODUCER;
    }

    public static Producer getTransactionalInstance(TransactionChecker checker,
                                                    String... topics) throws ClientException {
        if (null == TRANSACTIONAL_PRODUCER) {
            synchronized (ProducerSingleton.class) {
                if (null == TRANSACTIONAL_PRODUCER) {
                    TRANSACTIONAL_PRODUCER = buildProducer(checker, topics);
                }
            }
        }
        return TRANSACTIONAL_PRODUCER;
    }
}
