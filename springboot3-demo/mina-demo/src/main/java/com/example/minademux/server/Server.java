package com.example.minademux.server;

/**
 * <h1>Server</h1>
 * Created by hanqf on 2023/11/23 15:07.
 */


import com.example.minademux.MathProtocolCodecFactory;
import com.example.minademux.server.handler.ServerHandler;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import java.net.InetSocketAddress;

public class Server {
    public static void main(String[] args) throws Exception {
        IoAcceptor acceptor = new NioSocketAcceptor();
        acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 5);
        acceptor.getFilterChain().addLast("logger", new LoggingFilter());
        acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new MathProtocolCodecFactory(true)));
        acceptor.setHandler(new ServerHandler());
        acceptor.bind(new InetSocketAddress(9123));
    }
}


