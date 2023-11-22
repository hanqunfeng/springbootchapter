package com.example.minanew.mina.client.handler;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;


/**
 * <h1>ServerHandler</h1>
 * Created by hanqf on 2023/11/20 11:42.
 */

public class MinaClientHandler extends IoHandlerAdapter {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 心跳包内容
     */
    private static final String HEARTBEATREQUEST = "0x11";
    /**
     * 心跳返回类容
     */
    private static final String HEARTBEATRESPONSE = "01010";

    private final ObjectMapper objectMapper = new ObjectMapper();


    /**
     * 业务数据
    */
    private String content;

    public MinaClientHandler(String content) {
        this.content = content;
    }

    @Override
    public void sessionOpened(IoSession session) {
        session.write(content);
    }

    /**
     * NioSocketAcceptor设置handler后，会调用messageReceived方法处理业务逻辑。
     */
    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        //心跳包
        if (HEARTBEATREQUEST.equals(message.toString())) {
            logger.info("心跳监测:" + message);
            session.write(HEARTBEATRESPONSE);
        } else {
            JsonNode jsonNode = objectMapper.readTree(message.toString());
            logger.info("receive message:" + objectMapper.writeValueAsString(jsonNode));
            //do something
        }
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        logger.info(session.getId() + " >>>>>> sessionClosed ");
        session.closeNow();
    }

    @Override
    public void sessionCreated(IoSession session) throws Exception {
        InetSocketAddress isa = (InetSocketAddress) session.getRemoteAddress();
        // IP
        String address = isa.getAddress().getHostAddress();
        session.setAttribute("address", address);
        logger.info(">>>>>> 来自" + address + " 的终端上线，sessionId：" + session.getId());
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        logger.error(
                ">>>>>> 终端用户：" + session.getId() + "连接发生异常，即将关闭连接，原因：" + cause.getMessage());
    }

    @Override
    public void messageSent(IoSession session, Object message) throws Exception {
        logger.info(">>>>>>>>>>>>>>>>>>>> 发送消息成功 >>>>>>>>>>>>>>>>>>>>");
    }

}

