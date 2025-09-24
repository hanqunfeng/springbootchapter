package com.example.tutorials.pub_sub;

import com.example.tutorials.RabbitMQConstants;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * 发布-订阅模式，广播 fanout
 *
 * 所有订阅者都能收到消息
 * Created by hanqf on 2025/9/23 17:16.
 */

@Configuration
public class PubSubConfig {

    @Bean
    public Queue pubSubQueue1() {
        // 创建 quorum queue
        Map<String,Object> params = new HashMap<>();
        params.put("x-queue-type","quorum");
        return new Queue(RabbitMQConstants.QUEUE_NAME_PUBLISH_SUBSCRIBE_1, true, false, false, params);
    }

    @Bean
    public Queue pubSubQueue2() {
        // 创建 classic queue
        return new Queue(RabbitMQConstants.QUEUE_NAME_PUBLISH_SUBSCRIBE_2);
    }


    @Bean
    public FanoutExchange pubSubFanout() {
        return new FanoutExchange(RabbitMQConstants.EXCHANGE_NAME_PUBLISH_SUBSCRIBE);
    }

    @Bean
    public Binding pubSubBinding1() {
        // fanout模式，不需要绑定 routing key
        return BindingBuilder.bind(pubSubQueue1()).to(pubSubFanout());
    }

    @Bean
    public Binding pubSubBinding2() {
        return BindingBuilder.bind(pubSubQueue2()).to(pubSubFanout());
    }

}
