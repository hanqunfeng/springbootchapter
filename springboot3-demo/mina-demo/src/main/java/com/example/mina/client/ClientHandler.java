package com.example.mina.client;

import com.example.mina.protocol.MyPack;
import com.example.mina.session.MySession;
import lombok.extern.slf4j.Slf4j;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

import java.net.InetSocketAddress;

import static com.example.mina.Const.SESSION_KEY;

/**
 * <h1>ServerHandler</h1>
 * Created by hanqf on 2023/11/20 11:42.
 */

@Slf4j
public class ClientHandler extends IoHandlerAdapter {
    private final MyPack values;

    public ClientHandler(MyPack values) {
        this.values = values;
    }

    @Override
    public void sessionOpened(IoSession session) {
        session.write(values);
    }

    /**
     * NioSocketAcceptor设置handler后，会调用messageReceived方法处理业务逻辑。
     */
    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        MySession mySession = new MySession(session);
        MyPack myPack = (MyPack) message;
        log.info(mySession.toString() + ">>>>>> client received:" + message);

        if("bind success".equals(myPack.getBody())){
            session.closeOnFlush();
        }


    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        log.info(session.getAttribute(SESSION_KEY) + " nid: " + session.getId() + " >>>>>> sessionClosed ");
        session.closeNow();
    }

    @Override
    public void sessionCreated(IoSession session) throws Exception {
        InetSocketAddress isa = (InetSocketAddress) session.getRemoteAddress();
        // IP
        String address = isa.getAddress().getHostAddress();
        session.setAttribute("address", address);
        log.info(">>>>>> 来自" + address + " 的终端上线，sessionId：" + session.getId());
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        log.error(
                ">>>>>> 终端用户：" + session.getAttribute(SESSION_KEY) + "连接发生异常，即将关闭连接，原因：" + cause.getMessage());
    }

    @Override
    public void messageSent(IoSession session, Object message) throws Exception {
        log.info(">>>>>>>>>>>>>>>>>>>> 发送消息成功 >>>>>>>>>>>>>>>>>>>>");
    }

}

