package com.example.minanew.mina.server.handler;

import com.example.minanew.utils.ThreadPools;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Socket信息处理
 *
 * @author Administrator
 */
public class MinaServerHandler extends IoHandlerAdapter {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 异常捕捉
     *
     * @param session
     * @param cause
     */
    @Override
    public void exceptionCaught(IoSession session, Throwable cause) {
        logger.error("客户端异常掉线--->{}", session.getId(), cause);
    }

    /**
     * 服务器与客户端创建连接
     *
     * @param session
     * @throws Exception
     */
    @Override
    public void sessionCreated(IoSession session) throws Exception {

        InetSocketAddress isa = (InetSocketAddress) session.getRemoteAddress();
        // IP
        String address = isa.getAddress().getHostAddress();
        session.setAttribute("address", address);
        logger.info(">>>>>> 来自" + address + " 的终端上线，sessionId：" + session.getId());

//        SocketSessionConfig cfg = (SocketSessionConfig) session.getConfig();
//        cfg.setReceiveBufferSize(2 * 1024 * 1024);
//        cfg.setReadBufferSize(2 * 1024 * 1024);
//        cfg.setKeepAlive(true);
//        cfg.setSoLinger(0);
        logger.info("服务器与客户端创建连接--->{}", session.getId());
    }

    @Override
    public void sessionOpened(IoSession session) throws Exception {
        logger.info("服务器与客户端打开连接--->{}", session.getId());
    }

    /**
     * 消息的接收处理
     *
     * @param session
     * @param message
     * @throws Exception
     */
    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        // 消息的接受
        // 传递自定义解编码器传递数组和解析数组丢包断包的
        logger.info("MinaServerHanlder->接收到的数据：-->{}, sessionId-->{}", message, session.getId());
        if (message == null || StringUtils.isBlank(message.toString())) {
            logger.info("空的消息，直接丢弃");
            return;
        }
        if (message.toString().equals("01010")) {
            logger.info("接收的倒是心跳的信息，直接丢弃");
            return;
        }
        final JsonNode mes = objectMapper.readTree(message.toString());
        messageHandler(session, mes);
    }

    /**
     * 消息发送后调用
     *
     * @param session
     * @param message
     * @throws Exception
     */
    @Override
    public void messageSent(IoSession session, Object message) throws Exception {
        logger.info("发送数据：-->{},session--->{}", message, session.getId());
    }

    /**
     * session关闭
     *
     * @param session
     * @throws Exception
     */
    @Override
    public void sessionClosed(IoSession session) throws Exception {
        logger.info("session---->{},关闭了", session.getId());
        session.closeNow();
    }

    private void messageHandler(IoSession session, JsonNode message) {
        try {
            Integer threadCount = ((ThreadPoolExecutor) ThreadPools.exec).getActiveCount();
            logger.info("当前正在活动的线程数量---->{}", threadCount);
        } catch (Exception e) {
            logger.error("获取线程数量失败");
        }
        try {
            ThreadPools.exec.execute(() -> new ServiceMessage().messageHandler(session, message));
        } catch (Exception e) {
            logger.error("Socket消息：{}，处理失败--->{}", message, session.getId(), e);
        }
    }
}
