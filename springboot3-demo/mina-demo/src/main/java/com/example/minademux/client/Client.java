package com.example.minademux.client;

/**
 * <h1>Client</h1>
 * Created by hanqf on 2023/11/23 15:10.
 */


import com.example.minademux.MathProtocolCodecFactory;
import com.example.minademux.client.handler.ClientHandler;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import java.net.InetSocketAddress;

public class Client {
    public static void main(String[] args) throws Throwable {
        IoConnector connector = new NioSocketConnector();
        connector.setConnectTimeoutMillis(30000);
        connector.getFilterChain().addLast("logger", new LoggingFilter());
        connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new MathProtocolCodecFactory(false)));
        connector.setHandler(new ClientHandler());
        connector.connect(new InetSocketAddress("localhost", 9123));
    }
}


