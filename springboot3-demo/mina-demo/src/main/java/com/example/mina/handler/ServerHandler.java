package com.example.mina.handler;

import com.example.mina.protocol.MyPack;
import com.example.mina.session.MySession;
import com.example.mina.session.SessionManager;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.InetSocketAddress;
import java.util.HashMap;

import static com.example.mina.Const.*;

/**
 * <h1>ServerHandler</h1>
 * Created by hanqf on 2023/11/20 11:42.
 */


public class ServerHandler extends IoHandlerAdapter {
    static final Logger logger = LoggerFactory.getLogger(ServerHandler.class);

    private HashMap<Integer, BaseHandler> handlers = new HashMap<>();

    @Autowired
    SessionManager sessionManager;

    /**
     * NioSocketAcceptor设置handler后，会调用messageReceived方法处理业务逻辑。
    */
    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        MySession mySession = new MySession(session);
        MyPack MyPack = (MyPack) message;
        logger.info(mySession.toString() + ">>>>>> server received:" + message);

        MyPack response;

        // 如果是心跳包接口，则说明处理失败
        if (HEART_BEAT == MyPack.getModule()) {
            logger.info(mySession.toString() + ">>>>>> server handler heartbeat error!");
            response = new MyPack(AUTHEN, MyPack.getSeq(), "authen fail", MyPack.REQUEST);
            mySession.write(response);
            mySession.close(false);
            return;
        }

        // 终端在未认证时连接进来，SERVER端要发送认证失败的包给终端，然后再断开连接，防止未知设备连到服务器
        if (null == mySession.getAttribute(SESSION_KEY) && AUTHEN != MyPack.getModule()) {
            logger.info(mySession.toString() + ">>>>>> need device authen!");
            response = new MyPack(AUTHEN, MyPack.getSeq(), "authen fail", MyPack.REQUEST);
            mySession.write(response);
            mySession.close(false);
            return;
        }

        BaseHandler handler = handlers.get(MyPack.getModule());
        String result = handler.process(mySession, MyPack.getBody());
        if (result == null) {
            logger.info(mySession.toString() + ">>>>>> need authen!");
            response = new MyPack(AUTHEN, MyPack.getSeq(), "deal error", MyPack.REQUEST);
            mySession.write(response);
            mySession.close(false);
        } else {
            logger.info(mySession.toString() + ">>>>>> succeed!");
            response = new MyPack(MyPack.getModule(), MyPack.getSeq(), result, MyPack.REQUEST);
            mySession.write(response);
        }
    }

    /**
     * 心跳包超时处理
     *
     * 当读写空闲时间超过定义的时间后，会调用该handler的sessionIdle方法。
     */
    @Override
    public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
        if (session.getAttribute(TIME_OUT_KEY) == null) {
            session.closeNow();
            logger.error(
                    session.getAttribute(SESSION_KEY) + " nid: " + session.getId() + " >>>>>> time_out_key null");
            return;
        }
        try {
            int isTimeoutNum = (int) session.getAttribute(TIME_OUT_KEY);
            isTimeoutNum++;
            // 没有超过最大次数，超时次数加1
            if (isTimeoutNum < TIME_OUT_NUM) {
                session.setAttribute(TIME_OUT_KEY, isTimeoutNum);
            } else {
                // 超过最大次数，关闭会话连接
                String account = (String) session.getAttribute(SESSION_KEY);
                // 移除device属性
                session.removeAttribute(SESSION_KEY);
                // 移除超时属性
                session.removeAttribute(TIME_OUT_KEY);
                sessionManager.removeSession(account);
                session.closeOnFlush();
                logger.info(">>>>>> client user: " + account + " more than " + TIME_OUT_NUM
                        + " times have no response, connection closed! >>>>>>");
            }
        } catch (Exception e) {
            logger.error(
                    session.getAttribute(SESSION_KEY) + " nid: " + session.getId() + " >>>>>> " + e.getMessage());
            session.closeNow();
        }
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        logger.info(session.getAttribute(SESSION_KEY) + " nid: " + session.getId() + " >>>>>> sessionClosed ");
        // 移除account属性
        session.removeAttribute(SESSION_KEY);
        // 移除超时属性
        session.removeAttribute(TIME_OUT_KEY);
        String account = (String) session.getAttribute(SESSION_KEY);
        sessionManager.removeSession(account);
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
    public void sessionOpened(IoSession session) throws Exception {
        logger.info("Open a session ...");
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        logger.error(
                ">>>>>> 终端用户：" + session.getAttribute(SESSION_KEY) + "连接发生异常，即将关闭连接，原因：" + cause.getMessage());
    }

    @Override
    public void messageSent(IoSession session, Object message) throws Exception {
        logger.info(">>>>>>>>>>>>>>>>>>>> 发送消息成功 >>>>>>>>>>>>>>>>>>>>");
    }

    public HashMap<Integer, BaseHandler> getHandlers() {
        return handlers;
    }

    public void setHandlers(HashMap<Integer, BaseHandler> handlers) {
        this.handlers = handlers;
        logger.info(">>>>>> server handlers set success!");
    }

}

