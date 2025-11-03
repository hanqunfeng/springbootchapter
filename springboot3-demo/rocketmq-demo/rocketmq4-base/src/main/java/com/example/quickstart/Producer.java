package com.example.quickstart;

import com.example.AclUtil;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;

import java.nio.charset.StandardCharsets;


/**
 * https://github.com/apache/rocketmq/blob/develop/example/src/main/java/org/apache/rocketmq/example/quickstart/Producer.java
 * This class demonstrates how to send messages to brokers using provided {@link DefaultMQProducer}.
 */
public class Producer {



    /**
     * The number of produced messages.
     */
    public static final int MESSAGE_COUNT = 10;
    public static final String PRODUCER_GROUP = "please_rename_unique_group_name";
    // name server address，在 rocketmq4.x 中， 由于client通过nameserver 获取broker地址，然后再直接连接broker进行通信， 但是注册broker时默认都是内网ip（除非在broker.conf中明确指定 brokerIP1=68.79.47.19  # Broker 公网 IP），所以这里返回的也都是内网地址，所以外网是无法访问的
    // 这样体现出，rocketmq5.x 增加 proxy 来统一处理请求的转发是多么的方便
    public static final String DEFAULT_NAMESRVADDR = "127.0.0.1:9876";
//    public static final String DEFAULT_NAMESRVADDR = "68.79.47.19:9876";
//    public static final String DEFAULT_NAMESRVADDR = "68.79.47.19:8080";
//    public static final String DEFAULT_NAMESRVADDR = "127.0.0.1:8080";
//    public static final String TOPIC = "TopicTest";
    public static final String TOPIC = "TestTopic";
    public static final String TAG = "TagA";



    public static void main(String[] args) throws MQClientException, InterruptedException {

        /*
         * Instantiate with a producer group name.
         */
        DefaultMQProducer producer = new DefaultMQProducer(PRODUCER_GROUP, AclUtil.getAclRPCHook());
//        DefaultMQProducer producer = new DefaultMQProducer(PRODUCER_GROUP);

        /*
         * Specify name server addresses.
         *
         * Alternatively, you may specify name server addresses via exporting environmental variable: NAMESRV_ADDR
         * <pre>
         * {@code
         *  producer.setNamesrvAddr("name-server1-ip:9876;name-server2-ip:9876");
         * }
         * </pre>
         */
        // Uncomment the following line while debugging, namesrvAddr should be set to your local address
         producer.setNamesrvAddr(DEFAULT_NAMESRVADDR);

        /*
         * Launch the instance.
         */
        producer.start();

        for (int i = 0; i < MESSAGE_COUNT; i++) {
            try {

                /*
                 * Create a message instance, specifying topic, tag and message body.
                 */
                Message msg = new Message(TOPIC /* Topic */,
                        TAG /* Tag */,
                        ("Hello RocketMQ " + i).getBytes(StandardCharsets.UTF_8) /* Message body */
                );

                /*
                 * Call send message to deliver message to one of brokers.
                 */
                SendResult sendResult = producer.send(msg, 20 * 1000);
                /*
                 * There are different ways to send message, if you don't care about the send result,you can use this way
                 * {@code
                 * producer.sendOneway(msg);
                 * }
                 */

                /*
                 * if you want to get the send result in a synchronize way, you can use this send method
                 * {@code
                 * SendResult sendResult = producer.send(msg);
                 * System.out.printf("%s%n", sendResult);
                 * }
                 */

                /*
                 * if you want to get the send result in a asynchronize way, you can use this send method
                 * {@code
                 *
                 *  producer.send(msg, new SendCallback() {
                 *  @Override
                 *  public void onSuccess(SendResult sendResult) {
                 *      // do something
                 *  }
                 *
                 *  @Override
                 *  public void onException(Throwable e) {
                 *      // do something
                 *  }
                 *});
                 *
                 *}
                 */

                System.out.printf("%s%n", sendResult);
            } catch (Exception e) {
                e.printStackTrace();
                Thread.sleep(1000);
            }
        }

        /*
         * Shut down once the producer instance is no longer in use.
         */
        producer.shutdown();
    }
}
