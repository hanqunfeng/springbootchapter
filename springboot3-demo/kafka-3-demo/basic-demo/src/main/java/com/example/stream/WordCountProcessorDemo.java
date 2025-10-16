package com.example.stream;

import com.example.basic.KafkaManager;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.processor.PunctuationType;
import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.ProcessorContext;
import org.apache.kafka.streams.processor.api.ProcessorSupplier;
import org.apache.kafka.streams.processor.api.Record;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.StoreBuilder;
import org.apache.kafka.streams.state.Stores;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

/**
 * Author： roy
 * Description：将INPUT_TOPIC中每个单词出现的次数。 使用LowLevel API构建Topology。自由度更高。
 **/
public class WordCountProcessorDemo {
    private static final String INPUT_TOPIC = "inputTopic";
    private static final String OUTPUT_TOPIC = "outputTopic";

    public static void main(String[] args) {
        Properties props = KafkaManager.getBootstrapServersConfig();
        // 设置应用程序ID
        props.putIfAbsent(StreamsConfig.APPLICATION_ID_CONFIG, "word");
        // 缓存大小
        props.putIfAbsent(StreamsConfig.STATESTORE_CACHE_MAX_BYTES_CONFIG, 0);
        // key和value的序列化类
        props.putIfAbsent(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.StringSerde.class);
        props.putIfAbsent(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.StringSerde.class);
        // 设置 Kafka 消费者在找不到已提交偏移量时的行为，这里是 latest，表示从最新消息开始消费（跳过旧消息）
        props.putIfAbsent(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");

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

    private static Topology buildTopology(){
        Topology topology = new Topology();

        topology.addSource("source",WordCountProcessorDemo.INPUT_TOPIC);

        topology.addProcessor("process", new MyWCProcessor(),"source");

        topology.addSink("sink",OUTPUT_TOPIC, new StringSerializer(),new LongSerializer(),"process");
        return topology;
    }
    //输入和输出的key与value都必须是相同类型，否则无法序列化。--只能设置一个KEY_SERDE_CLASS 和一个 VALUE_SERDE_CLASS
    // 用highlevel API就可以转。但是用lowlevel API的话，暂不知道怎么处理。
    static class MyWCProcessor implements ProcessorSupplier<String,String,String,Long> {
        @Override
        public Processor<String, String, String, Long> get() {
            return new Processor<String, String, String, Long>() {
                private KeyValueStore<String,Long> kvstore;
                @Override
                public void init(ProcessorContext<String, Long> context) {
                    context.schedule(Duration.ofSeconds(1),PunctuationType.STREAM_TIME,timestamp -> {
                        try(KeyValueIterator<String, Long> iter = kvstore.all()){
                            System.out.println("======="+timestamp+"======");
                            while (iter.hasNext()){
                                KeyValue<String, Long> entry = iter.next();
                                System.out.println("["+entry.key+","+entry.value+"]");
                                context.forward(new Record<>(entry.key, entry.value, timestamp));
                            }
                        }
                    });
                    this.kvstore = context.getStateStore("counts");
                }

                @Override
                public void process(Record<String, String> record) {
                    System.out.println(">>>>>"+record.value());
                    String[] words = record.value().toLowerCase().split("\\W+");
                    for (String word : words) {
                        Long count = this.kvstore.get(word);
                        if(null == count){
                            this.kvstore.put(word,1L);
                        }else{
                            this.kvstore.put(word,count+1);
                        }
                    }
                }
            };
        }

        @Override
        public Set<StoreBuilder<?>> stores() {
            return Collections.singleton(Stores.keyValueStoreBuilder(
                    Stores.inMemoryKeyValueStore("counts"),
                    Serdes.String(),
                    Serdes.Long()));
        }
    }
}
