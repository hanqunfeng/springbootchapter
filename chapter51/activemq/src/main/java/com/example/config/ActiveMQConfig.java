package com.example.config;


import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;

import javax.jms.ConnectionFactory;
import javax.jms.Queue;
import javax.jms.Topic;

/**
 * <h1></h1>
 * Created by hanqf on 2021/5/14 14:52.
 */

@EnableJms //开启消息中间件的服务能力
@Configuration
public class ActiveMQConfig {


    /*
    以下为点对点消息模型和发布订阅模型分别创建消息监听工厂类JmsListenerContainerFactory，
    在消费者订阅消息时，需要指定containerFactory，这样就可以同时支持两种消息模型了
     */
    @Bean(name = "queueListenerFactory")
    public JmsListenerContainerFactory<?> queueListenerFactory(ConnectionFactory connectionFactory) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        //点对点模式
        factory.setPubSubDomain(false);
        //设置处理消息的最大并发线程数，默认是1，如果要严格控制消息的执行顺序就不要设置它
        factory.setConcurrency("10");
        factory.setConnectionFactory(connectionFactory);
        return factory;
    }

    @Bean(name = "topicListenerFactory")
    public JmsListenerContainerFactory<?> topicListenerFactory(ConnectionFactory connectionFactory) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        //发布订阅模式
        factory.setPubSubDomain(true);
        //设置处理消息的最大并发线程数，默认是1，如果要严格控制消息的执行顺序就不要设置它
        factory.setConcurrency("10");
        factory.setConnectionFactory(connectionFactory);
        return factory;
    }


    //配置一个消息队列（P2P模式）
    @Bean(name = "messageQueue")
    public Queue messageQueue() {
        //这里相当于为消息队列起一个名字用于生产消费用户访问
        return new ActiveMQQueue("message.queue");
    }

    //配置一个消息队列（P2P模式）,用于回调
    @Bean(name = "messageQueue2")
    public Queue messageQueue2() {
        //这里相当于为消息队列起一个名字用于生产消费用户访问
        return new ActiveMQQueue("message.queue2");
    }

    //配置一个消息队列（发布订阅模式）
    @Bean(name = "messageTopic")
    public Topic messageTopic() {
        return new ActiveMQTopic("message.topic");
    }
}
