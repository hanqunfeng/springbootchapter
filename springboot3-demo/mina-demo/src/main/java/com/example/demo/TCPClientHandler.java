package com.example.demo;

import lombok.extern.slf4j.Slf4j;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

/**
 * <h1>TCPClientHandler</h1>
 * Created by hanqf on 2023/11/17 17:21.
 */

@Slf4j
public class TCPClientHandler extends IoHandlerAdapter {
    private final String values;

    public TCPClientHandler(String values) {
        this.values = values;
    }

    @Override
    public void sessionOpened(IoSession session) {
        session.write(values);
    }
}
