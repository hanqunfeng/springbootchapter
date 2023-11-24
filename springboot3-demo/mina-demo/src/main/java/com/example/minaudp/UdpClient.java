package com.example.minaudp;

/**
 * <h1>UdpClient</h1>
 * Created by hanqf on 2023/11/24 10:50.
 */


import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.LineDelimiter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.DatagramSessionConfig;
import org.apache.mina.transport.socket.nio.NioDatagramConnector;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

public class UdpClient {

    public static void main(String[] args) throws Exception {
        // 创建 UDP 客户端
        IoConnector connector = new NioDatagramConnector();

        connector.getFilterChain().addLast("codec",
                new ProtocolCodecFilter(new TextLineCodecFactory(Charset.forName("UTF-8"),
                        LineDelimiter.WINDOWS.getValue(),
                        LineDelimiter.WINDOWS.getValue()))
        );

        // 设置客户端处理器
        connector.setHandler(new UdpClientHandler());

        // 配置 UDP 会话参数
        DatagramSessionConfig dcfg = ((NioDatagramConnector) connector).getSessionConfig();
        dcfg.setReuseAddress(true);

        // 连接到服务器
        ConnectFuture future = connector.connect(new InetSocketAddress("localhost", 9898));

        // 等待连接完成
        future.awaitUninterruptibly();

        // 发送消息到服务器
        IoSession session = future.getSession();
        session.write("Hello, Server!");

        // 等待一段时间，然后关闭客户端
        Thread.sleep(1000);
        connector.dispose();
    }

    static class UdpClientHandler extends IoHandlerAdapter {
        @Override
        public void messageReceived(IoSession session, Object message) throws Exception {
            // 处理接收到的服务器响应
            String response = message.toString();
            System.out.println("Received response from server: " + response);
        }
    }
}

