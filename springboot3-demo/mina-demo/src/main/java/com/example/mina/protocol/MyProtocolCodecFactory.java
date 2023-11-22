package com.example.mina.protocol;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * <h1>MyProtocolCodecFactory</h1>
 * Created by hanqf on 2023/11/20 11:26.
 */


public class MyProtocolCodecFactory implements ProtocolCodecFactory {

    private final ProtocolEncoder encoder;
    private final ProtocolDecoder decoder;

    public MyProtocolCodecFactory() {
        this(StandardCharsets.UTF_8);
    }

    public MyProtocolCodecFactory(Charset charset) {
        this.encoder = new MyProtocolEncoder(charset);
        this.decoder = new MyProtocolDecoder(charset);
    }

    @Override
    public ProtocolEncoder getEncoder(IoSession session) throws Exception {
        return encoder;
    }

    @Override
    public ProtocolDecoder getDecoder(IoSession session) throws Exception {
        return decoder;
    }

}

