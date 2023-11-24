package com.example.minaudp;

/**
 * <h1>UdpServer</h1>
 * Created by hanqf on 2023/11/24 10:49.
 */


import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.LineDelimiter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.DatagramSessionConfig;
import org.apache.mina.transport.socket.nio.NioDatagramAcceptor;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

public class UdpServer {

    public static void main(String[] args) throws Exception {
        // 创建 UDP 服务器
        IoAcceptor acceptor = new NioDatagramAcceptor();

        acceptor.getFilterChain().addLast("codec",
                new ProtocolCodecFilter(new TextLineCodecFactory(Charset.forName("UTF-8"),
                        LineDelimiter.WINDOWS.getValue(),
                        LineDelimiter.WINDOWS.getValue()))
        );

        // 设置服务器处理器
        acceptor.setHandler(new UdpServerHandler());

        // 配置 UDP 会话参数
        DatagramSessionConfig dcfg = ((NioDatagramAcceptor) acceptor).getSessionConfig();
        dcfg.setReuseAddress(true);

        // 绑定服务器端口
        acceptor.bind(new InetSocketAddress(9898));

        System.out.println("UDP Server is listening on port 9898");
    }

    static class UdpServerHandler extends IoHandlerAdapter {
        @Override
        public void messageReceived(IoSession session, Object message) throws Exception {
            // 处理接收到的消息
            String str = message.toString();
            System.out.println("Received message from client: " + str);

            // 向客户端发送响应
            session.write("Hello, Client!");
        }
    }
}

