package com.example.minanew.mina.client;

import com.example.minanew.mina.ByteArrayCodecFactory;
import com.example.minanew.mina.client.handler.MinaClientHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.mina.core.service.IoConnector;
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
        connector.connect(new InetSocketAddress("localhost", prot));

        logger.info("mina client 已经启动");

        //优雅关闭服务
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("---client connector dispose---");
            connector.dispose();
        }));


    }


}
