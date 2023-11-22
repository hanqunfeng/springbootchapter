package com.example.minanew.mina.server.keepalive;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.keepalive.KeepAliveFilter;
import org.apache.mina.filter.keepalive.KeepAliveRequestTimeoutHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 处理心跳异常的程序
 *
 * @author Administrator
 */
public class KeepAliveRequestTimeoutHandlerImpl implements KeepAliveRequestTimeoutHandler {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void keepAliveRequestTimedOut(KeepAliveFilter filter, IoSession session) {
        session.closeNow();
        logger.info("心跳检测异常!干掉用户session--->{}", session);
    }

}
