package com.example.minanew.mina.server;


import com.example.minanew.mina.protocol.ByteArrayCodecFactory;
import com.example.minanew.mina.server.handler.MinaServerHandler;
import com.example.minanew.mina.server.keepalive.KeepAliveMessageFactoryImpl;
import com.example.minanew.mina.server.keepalive.KeepAliveRequestTimeoutHandlerImpl;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.keepalive.KeepAliveFilter;
import org.apache.mina.filter.keepalive.KeepAliveMessageFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

/**
 * <h1>server启动</h1>
 * Created by hanqf on 2023/11/21 14:39.
 */


public class MinaServerRun {
    private static final Logger logger = LoggerFactory.getLogger(MinaServerRun.class);

    /** 30秒后超时 */
    private static final int IDELTIMEOUT = 30;
    /** 25秒发送一次心跳包 */
    private static final int HEARTBEATRATE = 25;

    /**
     * 服务端口
    */
    private static final int prot = 12581;

    public static void main(String[] args) {
        logger.info("--->正在启动mina<---");
        IoAcceptor acceptor = new NioSocketAcceptor();
        acceptor.getSessionConfig().setReadBufferSize(2048);
        acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, IDELTIMEOUT);

        // filter过滤器链，按照声明的位置顺序执行，receive时正向，send时逆向
        //日志过滤器，receive时它在编解码器之前执行，send时在编解码器之后执行，所以都是IoBuffer
        acceptor.getFilterChain().addLast("logger", new LoggingFilter());
        // 自定义解编码器，这个是mina中最最重要的过滤器，用于对数据进行编解码。
        // receive时调用ProtocolDecoder对传递过滤的IoBuffer进行解码，send时调用ProtocolEncoder将数据转换为IoBuffer
        acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new ByteArrayCodecFactory(StandardCharsets.UTF_8)));

        // 心跳检测开始
        KeepAliveMessageFactory heartBeatFactory = new KeepAliveMessageFactoryImpl();
        KeepAliveFilter heartBeat = new KeepAliveFilter(heartBeatFactory, IdleStatus.BOTH_IDLE);

        // 设置是否forward到下一个filter
        heartBeat.setForwardEvent(true);
        // 设置心跳频率
        heartBeat.setRequestInterval(HEARTBEATRATE);
        // 设置失败处理handler
        heartBeat.setRequestTimeoutHandler(new KeepAliveRequestTimeoutHandlerImpl());
        acceptor.getFilterChain().addLast("heartbeat", heartBeat);
        // 心跳检测结束

        acceptor.getFilterChain().getAll().forEach(filter -> {
            logger.info("FilterName:{}",filter.getName());
        });

        //业务逻辑，经过过滤器链后到达handler，所以到达handler的数据是解码后的数据
        acceptor.setHandler(new MinaServerHandler());
        try {
            //绑定端口，此时就启动完成了
            acceptor.bind(new InetSocketAddress(prot));
        } catch (IOException e) {
            logger.error("mina启动失败", e);
        }

        logger.info("--->mina启动成功<---");

        //优雅关闭服务
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("---server acceptor unbind---");
            acceptor.unbind();
            logger.info("---server acceptor dispose---");
            acceptor.dispose();
        }));

    }
}
