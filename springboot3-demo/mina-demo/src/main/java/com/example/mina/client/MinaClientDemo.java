package com.example.mina.client;

import com.example.mina.Const;
import com.example.mina.protocol.MyPack;
import com.example.mina.protocol.MyProtocolCodecFactory;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import java.net.InetSocketAddress;

/**
 * <h1></h1>
 * Created by hanqf on 2023/11/20 15:09.
 */


public class MinaClientDemo {
    public static void main(String[] args) {

        IoConnector connector = new NioSocketConnector();
        connector.setConnectTimeoutMillis(30000);

//        connector.setFilterChainBuilder(defaultIoFilterChainBuilder());

        connector.getFilterChain().addLast("codec", protocolCodecFilter());

//        connector.setHandler(new ClientHandler(new MyPack(Const.AUTHEN, "1111", "{\"account\":\"hanqf\",\"version\":\"V1.0\"}", MyPack.REQUEST)));
       //模拟心跳包
        connector.setHandler(new ClientHandler(new MyPack(Const.HEART_BEAT, "1111", "{\"account\":\"hanqf\",\"version\":\"V1.0\"}", MyPack.REQUEST)));

        connector.connect(new InetSocketAddress("localhost", Const.PORT));
    }


    /**
     * 编解码器filter
     */
    public static ProtocolCodecFilter protocolCodecFilter() {
        return new ProtocolCodecFilter(new MyProtocolCodecFactory());
    }

}
