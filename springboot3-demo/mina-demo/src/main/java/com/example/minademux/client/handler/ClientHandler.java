package com.example.minademux.client.handler;

/**
 * <h1>ClientHandler</h1>
 * Created by hanqf on 2023/11/23 15:10.
 */


import com.example.minademux.model.ResultMessage;
import com.example.minademux.model.SendMessage;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientHandler extends IoHandlerAdapter {
    private final static Logger LOGGER = LoggerFactory.getLogger(ClientHandler.class);

    @Override
    public void sessionOpened(IoSession session) throws Exception {
        SendMessage sm = new SendMessage();
        sm.setI(100);
        sm.setJ(99);
        sm.setSymbol('+');
        session.write(sm);
    }

    @Override
    public void messageReceived(IoSession session, Object message) {
        ResultMessage rs = (ResultMessage) message;
        LOGGER.info(String.valueOf(rs.getResult()));
    }
}


