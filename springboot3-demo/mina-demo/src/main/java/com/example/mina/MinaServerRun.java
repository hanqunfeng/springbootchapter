package com.example.mina;

import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;

/**
 * <h1>MinaServerRun</h1>
 * Created by hanqf on 2023/11/20 11:45.
 */


@Component
public class MinaServerRun implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(MinaServerRun.class);

    @Autowired
    private NioSocketAcceptor acceptor;

    public MinaServerRun(NioSocketAcceptor acceptor) {
        this.acceptor = acceptor;
    }

    @Override
    public void run(String... args) throws Exception {
        acceptor.bind(new InetSocketAddress(Const.PORT));
        logger.info("---springboot mina server start---");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("---server acceptor unbind---");
            acceptor.unbind();
            logger.info("---server acceptor dispose---");
            acceptor.dispose();
        }));
    }

}

