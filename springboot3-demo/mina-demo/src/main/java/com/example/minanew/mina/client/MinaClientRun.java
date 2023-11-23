package com.example.minanew.mina.client;

import com.example.minanew.mina.protocol.ByteArrayCodecFactory;
import com.example.minanew.mina.client.handler.MinaClientHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * <h1>client 启动</h1>
 * Created by hanqf on 2023/11/20 15:09.
 */


public class MinaClientRun {

    private static final Logger logger = LoggerFactory.getLogger(MinaClientRun.class);

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 服务端口
     */
    private static final int prot = 12581;

    public static void main(String[] args) throws JsonProcessingException {

        IoConnector connector = new NioSocketConnector();
        connector.setConnectTimeoutMillis(30000);

        connector.getFilterChain().addLast("logger", new LoggingFilter());
        // 自定义解编码器
        connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new ByteArrayCodecFactory(StandardCharsets.UTF_8)));

        //发送的数据
        Map<String,Object> map = new HashMap<>();
        map.put("mid", 100);
        map.put("msg", "hello server.");

        //业务逻辑
        connector.setHandler(new MinaClientHandler(objectMapper.writeValueAsString(map)));

        //连接server
        // 执行connector.connect后会调用handler的sessionCreated方法，但是此时连接尚未连接成功
//        connector.connect(new InetSocketAddress("localhost", prot));
        final ConnectFuture connectFuture = connector.connect(new InetSocketAddress("localhost", prot));

        logger.info("mina client 已经启动");


        // 在连接成功之后执行一些事情,有如下两种办法:

        // 方法一：
        // 产生阻塞，中断等待连接成功，相当于是转异步执行为同步执行。推荐使用Listener
        // 连接成功时会调用handler的sessionOpened方法
        connectFuture.awaitUninterruptibly();
        // 连接成功后获取会话对象。如果没有上面的等待，由于connect()方法是异步的，session可能会无法获取。
        IoSession ioSession = connectFuture.getSession();
        // 每执行一次write方法，就产生一次连接，调用一次handler的messageSent方法
        ioSession.write(objectMapper.writeValueAsString(map));
        ioSession.write(objectMapper.writeValueAsString(map));
        ioSession.write(objectMapper.writeValueAsString(map));

        // 方法二：
        // 添加一个监听器， 在异步执行的结果返回时会执行监听器中的回调方法operationComplete(IoFuture future)，
        // 也就是说，这是替代awaitUninterruptibly()方法另一种等待异步执行结果的方法，它的好处是不会产生阻塞。
        connectFuture.addListener(ioFuture -> {
            // 连接成功后获取会话对象。如果没有上面的等待，由于connect()方法是异步的，session可能会无法获取。
            IoSession ioSession2 = ioFuture.getSession();
            try {
                ioSession2.write(objectMapper.writeValueAsString(map));
                ioSession2.write(objectMapper.writeValueAsString(map));
                ioSession2.write(objectMapper.writeValueAsString(map));
            }catch (JsonProcessingException e){
                e.printStackTrace();
            }
        });




        //优雅关闭服务
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("---client connector dispose---");
            connector.dispose();
        }));


    }


}
