package com.example.minanew.mina.server.keepalive;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.keepalive.KeepAliveMessageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * mina 心跳包
 *
 * @author Administrator
 */
public class KeepAliveMessageFactoryImpl implements KeepAliveMessageFactory {

    Logger logger = LoggerFactory.getLogger(getClass());
    /**
     * 心跳包内容
     */
    private static final String HEARTBEATREQUEST = "0x11";
    /**
     * 心跳返回类容
     */
    private static final String HEARTBEATRESPONSE = "01010";

    @Override
    public boolean isRequest(IoSession ioSession, Object o) {
        if (o.toString().equals(HEARTBEATREQUEST)) {
            logger.info("请求心跳包信息:{}，sessionId：{}", ioSession.getId(), o);
            return true;
        }
        return false;
    }



    @Override
    public boolean isResponse(IoSession session, Object message) {
        if (message.toString().equals(HEARTBEATRESPONSE)) {
            logger.info("收到:{}, 响应心跳包信息: {}", session.getId(), message);
            return true;
        }
        return false;
    }

    @Override
    public Object getRequest(IoSession session) {
        logger.info("响应预设信息--->sessionId：{}，内容：{}", session.getId(), HEARTBEATREQUEST);
        return HEARTBEATREQUEST;
    }

    @Override
    public Object getResponse(IoSession session, Object request) {
        logger.info("收到：{}，心跳包，预设响应内容：{}", session.getId(), HEARTBEATREQUEST);
        return HEARTBEATRESPONSE;
    }

}
