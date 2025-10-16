package com.example.basic;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 基于Redis管理消费者Offset,防止消息重复处理。
 * 伪代码，就不去具体实现了。
 *
 * @auth roykingw
 */
public class RedisConsumer {

    Logger logger = LoggerFactory.getLogger(RedisConsumer.class);
    //计算密集型任务
    private final static int CORES = 2 * Runtime.getRuntime().availableProcessors();
    public static final String REDIS_PREFEX = "myoffset_";

    private volatile boolean IF_SLEEP = false;
    private volatile boolean RUNNING = true;

    private final ThreadPoolExecutor executorService;

    private String group;
    private String topic;
    private final KafkaConsumer<String, String> consumer;

    private static JedisPool pool;


    static {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(8);        // 最大连接数
        config.setMaxIdle(4);         // 最大空闲连接
        config.setMinIdle(1);         // 最小空闲连接
        config.setTestOnBorrow(true); // 取出连接前测试可用性

        pool = new JedisPool(config, "localhost", 6379, 2000, "password");
    }

    public RedisConsumer(String topic, String group) {
        this.topic = topic;
        this.group = group;
        executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(CORES,
                new ThreadFactory() {
                    private final AtomicInteger threadNumber = new AtomicInteger();

                    @Override
                    public Thread newThread(Runnable r) {
                        return new Thread(null, r, "RedisConsumer_" + threadNumber.getAndIncrement());
                    }
                });
        Properties props = KafkaManager.getBootstrapServersConfig();

        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, 5000);
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 45000);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, 500);//
        props.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, 1);
        props.put(ConsumerConfig.RECEIVE_BUFFER_CONFIG, 64 * 1024);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, group);
        consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Arrays.asList(topic.split(",")));
    }

    public void doTask() {
        try (Jedis redisClient = pool.getResource()) {
            try {
                while (RUNNING) {
                    try {
                        if (!IF_SLEEP) {
                            ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));
                            records.partitions().forEach(partition -> {
                                //从redis获取偏移量
                                String redisKafkaOffset = redisClient.hget(REDIS_PREFEX + partition.topic(),
                                        "" + partition.partition());
                                long redisOffset = (null == redisKafkaOffset || "".equals(redisKafkaOffset)) ? -1 : Long.valueOf(redisKafkaOffset);
                                List<ConsumerRecord<String, String>> partitionRecords = records.records(partition);
                                logger.info("[pull partition] topic:{}, partition:{}, records size:{}", partition.topic(),
                                        partition.partition(), partitionRecords.size());
                                partitionRecords.forEach(record -> {
                                    //redis记录的偏移量>=kafka实际的偏移量，表示已经消费过了，则丢弃。
                                    if (redisOffset >= record.offset()) {
                                        logger.error("[pool discard] group id:{}, offset:{}, redisOffset:{} ,value:{}", group, record.offset(), redisOffset, record.value());
                                        return;
                                    }
                                    executorService.execute(() -> {
                                        doMessage(record.topic(), record.value());
                                    });
                                });
                                //保存Redis偏移量
                                long saveRedisOffset = partitionRecords.get(partitionRecords.size() - 1).offset();
                                redisClient.hset(REDIS_PREFEX + partition.topic(), "" + partition.partition(), "" + saveRedisOffset);
                            });
                            //异步提交。消费业务多时，异步提交有可能造成消息重复消费，通过Redis中的Offset，就可以过滤掉这一部分重复的消息。
                            consumer.commitAsync();
                        }
                    } catch (Throwable e) {
                        logger.warn("[consumer exception] {}", e);
                    }
                }
            } catch (Throwable e) {
                logger.warn("[huge exception] to finish. {}", e);
            } finally {
                executorService.shutdown();
                try {
                    executorService.awaitTermination(5, TimeUnit.SECONDS);
                    logger.warn("[wait finish] RedisConsumer time beyond {}.", 5);
                } catch (InterruptedException e) {
                    logger.warn("[wait finish exception] RedisConsumer e:{}.", e);
                }
                executorService.shutdownNow();
                consumer.close();
                logger.warn("[finish consumer] topic:{}, groupId:{}.", topic, group);
            }
        }
    }

    //实际处理请求。通常可以交给子实现类去做。
    private void doMessage(String topic, String value) {
        System.out.println("[deal message] topic : " + topic + "; value = " + value);
    }

    public static void main(String[] args) {
        RedisConsumer consumer = new RedisConsumer(KafkaManager.topicName, "test-group");
        consumer.doTask();
    }
}
