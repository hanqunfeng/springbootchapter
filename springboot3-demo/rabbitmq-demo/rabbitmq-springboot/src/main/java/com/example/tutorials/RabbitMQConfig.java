package com.example.tutorials;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * Created by hanqf on 2025/9/23 17:59.
 */

@Configuration
public class RabbitMQConfig {
/*
    | 工厂类                                      | 作用                                                                       |
    | ---------------------------------------- | ------------------------------------------------------------------------ |
    | **SimpleRabbitListenerContainerFactory** | 创建 **SimpleMessageListenerContainer**，使用**线程池 + 多消费者**的模型处理消息，支持动态伸缩并发。  |
    | **DirectRabbitListenerContainerFactory** | 创建 **DirectMessageListenerContainer**，使用**单线程 NIO 事件循环**模型处理消息，更轻量，延迟更低。 |

    | 特性      | SimpleMessageListenerContainer          | DirectMessageListenerContainer  |
    | ------- | --------------------------------------- | ------------------------------- |
    | 并发模型    | 多个独立的消费者线程，每个线程有自己的 `Channel`           | 基于 NIO 事件循环，一个线程可管理多个 `Channel` |
    | 并发控制    | 支持 `concurrency`、`maxConcurrency`，可动态伸缩 | 不支持动态伸缩，只能通过固定消费者数配置            |
    | 性能      | 在高并发下可能因为线程数多导致上下文切换开销变大                | 更轻量，低延迟，适合长连接场景                 |
    | 自动恢复    | 消费者线程异常会自动重启                            | 消费者异常会自动重连                      |
    | 消费者数量配置 | `concurrency`、`maxConcurrency`          | `consumersPerQueue`（每个队列多少个消费者） |
    | 延迟      | 可能略高，线程调度带来的延迟                          | 更低延迟，消息可即时分发                    |
    | 适用场景    | 绝大多数通用场景，尤其是并发动态调整要求高的场景                | 低延迟、超高性能、对线程资源敏感的场景             |

    | 场景                   | 建议使用容器                                 |
    | -------------------- | -------------------------------------- |
    | **通用业务场景**（订单、支付等）   | `SimpleRabbitListenerContainerFactory` |
    | **高性能、低延迟场景**（实时推送等） | `DirectRabbitListenerContainerFactory` |
    | **需要动态伸缩消费者数量**      | `SimpleRabbitListenerContainerFactory` |
    | **长连接、大量队列、少量消息**    | `DirectRabbitListenerContainerFactory` |


*/

//    @Bean(name="qos_4")
//    public SimpleRabbitListenerContainerFactory getSimpleRabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
//        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
//        factory.setMaxConcurrentConsumers(4);
//        factory.setConnectionFactory(connectionFactory);
//        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);//手动确认
//        factory.setMessageConverter(messageConverter()); // 默认使用 SimpleMessageConverter，设置接收消息的转换器
//        return factory;
//    }

//    @Bean
//    public DirectRabbitListenerContainerFactory directFactory(ConnectionFactory connectionFactory) {
//        DirectRabbitListenerContainerFactory factory = new DirectRabbitListenerContainerFactory();
//        factory.setConnectionFactory(connectionFactory);
//        factory.setConsumersPerQueue(5);
//        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
//        factory.setMessageConverter(messageConverter());
//        return factory;
//    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        // 开启消息确认回调
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            // correlationData 需要在发送消息时自己配置
            if (correlationData != null) {
                System.out.println("消息ID: " + correlationData.getId());
            }
            if (ack) {
                System.out.println("!消息发送成功!");
            } else {
                System.err.println("消息发送失败" + "，原因：" + cause);
            }
        });

        // 开启 Returns 回调，确认消息是否从交换机路由到队列
        rabbitTemplate.setReturnsCallback(returned -> {
            System.err.println("消息无法路由到队列，返回信息：");
            System.err.println("交换机：" + returned.getExchange());
            System.err.println("路由键：" + returned.getRoutingKey());
            System.err.println("消息体：" + new String(returned.getMessage().getBody()));
        });

        // 开启消息转换器
        rabbitTemplate.setMessageConverter(messageConverter());

        return rabbitTemplate;
    }

    @Bean
    public JsonMapper jsonMapper() {
        return JsonMapper.builder()
                // 允许属性可见性
                .visibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY)
                // 允许序列化空的POJO类（否则会抛出异常）
                .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                // 在遇到未知属性的时候不抛出异常
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                // 禁用把java.util.Date, Calendar输出为数字(时间戳)
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .build();
    }

    // 消息转换器
    @Bean("jsonConverter")
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter(jsonMapper());
    }
}
