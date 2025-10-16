package com.example.stream;

import com.example.basic.KafkaManager;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;

import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

/**
 * Author： roy
 * Description：将INPUT_TOPIC中每个单词出现的次数。
 **/
public class WordCountStream {

    private static final String INPUT_TOPIC = "inputTopic";
    private static final String OUTPUT_TOPIC = "outputTopic";

    public static void main(String[] args) {
        Properties props = KafkaManager.getBootstrapServersConfig();
        // 设置应用程序ID
        props.putIfAbsent(StreamsConfig.APPLICATION_ID_CONFIG, "streams-wordcount");
        // 设置缓存大小
        props.putIfAbsent(StreamsConfig.STATESTORE_CACHE_MAX_BYTES_CONFIG, 0);
        // 设置key和value的序列化类
        props.putIfAbsent(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.StringSerde.class);
        props.putIfAbsent(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.StringSerde.class);
        // 设置 Kafka 消费者在找不到已提交偏移量时的行为
        props.putIfAbsent(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
//        | 值            | 含义                                     | 适用场景              |
//        | ------------ | -------------------------------------- | ----------------- |
//        | `"latest"`   | 如果找不到已提交的 offset，就从**最新消息**开始消费（跳过旧消息） | 常用于生产环境，消费者只关心新数据 |
//        | `"earliest"` | 如果找不到已提交的 offset，就从**最早的消息**开始消费（从头读）  | 常用于调试、日志重放、数据回溯   |
//        | `"none"`     | 如果没有找到 offset，就直接抛出异常                  | 常用于要求严格一致性的业务     |


        KafkaStreams streams = new KafkaStreams(buildTopology(), props);
        final CountDownLatch latch = new CountDownLatch(1);

        // 优雅关闭。streams需要调用close才会清除本地缓存
        Runtime.getRuntime().addShutdownHook(new Thread("streams-wordcount-shutdown-hook") {
            @Override
            public void run() {
                streams.close();
                latch.countDown();
            }
        });

        try {
            streams.start();
            latch.await();
        } catch (final Throwable e) {
            System.exit(1);
        }
        System.exit(0);
    }

    private static Topology buildTopology() {
        StreamsBuilder streamsBuilder = new StreamsBuilder();
        KStream<Object, String> source = streamsBuilder.stream(WordCountStream.INPUT_TOPIC);
        //flatMapValues：对每个值(如果Value是Collection，也会解析出每个值)执行一个函数，返回一个或多个值
        // 将字符串转换为小写，并使用空格分隔符分割字符串
        source.flatMapValues(value -> Arrays.asList(value.toLowerCase().split("\\W+")))
                // 将每个单词作为key，进行分组
                .groupBy((key, value) -> value)
                // 对每个分组进行计数，结果为一个KTable，可以理解为一个中间结果集
                .count()
                // 转换成为KStream数据流
                .toStream()
                // 输出到指定Topic
                .to(OUTPUT_TOPIC, Produced.with(Serdes.String(), Serdes.Long()));
        return streamsBuilder.build();
    }
}
