package com.example.basic;

import org.apache.kafka.clients.admin.*;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.errors.UnknownTopicOrPartitionException;

import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutionException;

/**
 * 创建topic等
 * Created by hanqf on 2025/10/15 16:56.
 */


public class KafkaManager {
    public static final String topicName = "disTopic";
    public static final int numPartitions = 3;
    public static final short replicationFactor = 2;


    /**
     * 创建认证连接的 config
     * Created by hanqf on 2025/10/15 17:32.
     * @author hanqf
     */
    public static Properties getBootstrapServersConfig() {
        Properties config = new Properties();
        // PLAINTEXT
//        config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "161.189.227.200:9093,43.192.84.195:9093,68.79.13.235:9093");

        // ===============================================================================================================
        // SASL_PLAINTEXT
//        config.put(AdminClientConfig.SECURITY_PROTOCOL_CONFIG, "SASL_PLAINTEXT");
//        config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "161.189.227.200:9094,43.192.84.195:9094,68.79.13.235:9094");
//        config.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
//        config.put(SaslConfigs.SASL_JAAS_CONFIG, "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"admin\" password=\"admin-secret\";");

        // ===============================================================================================================
        // SASL_SSL
        config.put(AdminClientConfig.SECURITY_PROTOCOL_CONFIG, "SASL_SSL");
        config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "161.189.227.200:9095,43.192.84.195:9095,68.79.13.235:9095");
        config.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
        config.put(SaslConfigs.SASL_JAAS_CONFIG, "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"admin\" password=\"admin-secret\";");
        // pem 证书
        config.put(SslConfigs.SSL_TRUSTSTORE_TYPE_CONFIG, "PEM");
        config.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, "/Users/hanqf/develop_soft/kafka/kafka3/config/ssl/server.crt");
        // jks 证书
//        config.put(SslConfigs.SSL_TRUSTSTORE_TYPE_CONFIG, "JKS");
//        config.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, "/Users/hanqf/develop_soft/kafka/kafka3/config/ssl/kafka.truststore.jks");
//        config.put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, "123456");

        // 禁用SSL主机验证
        config.put(SslConfigs.SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG, "");
        // ===============================================================================================================

        return config;
    }
    public static void main(String[] args) {

        Properties  config = KafkaManager.getBootstrapServersConfig();
        try (AdminClient admin = AdminClient.create(config)) {

            final DescribeTopicsResult describeTopicsResult = admin.describeTopics(Collections.singleton(topicName));

            try {
                describeTopicsResult.values().get(topicName).get();  // 阻塞等待结果
                System.out.println("✅ Topic 存在: " + topicName);
            } catch (ExecutionException e) {
                if (e.getCause() instanceof UnknownTopicOrPartitionException) {
                    System.out.println("❌ Topic 不存在: " + topicName);
                    // 创建Topic
                    NewTopic topic = new NewTopic(topicName, numPartitions, replicationFactor);
                    admin.createTopics(Collections.singleton(topic)).all().get();
                    System.out.println("Topic created successfully");
                } else {
                    throw e;
                }
            }

            // 列出所有Topic
            KafkaFuture<Set<String>> names = admin.listTopics().names();
            names.get().forEach(System.out::println);

            //列出Topic下的所有Partition
            final Map<String, TopicDescription> partitionsMap = admin.describeTopics(Collections.singleton(topicName)).allTopicNames().get();
            partitionsMap.forEach((k, v) -> {
                System.out.println(k + ":" + v);
                v.partitions().forEach(p -> {
                    System.out.println(p.partition());
                    System.out.println(p.leader());
                    System.out.println(p.replicas());
                    System.out.println(p.isr());
                });
            });
        } catch (InterruptedException | ExecutionException e) {
            System.out.println("Error occurred while creating the topic: " + e.getMessage());
        }

    }
}

