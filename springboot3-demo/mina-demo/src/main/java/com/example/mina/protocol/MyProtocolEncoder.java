package com.example.mina.protocol;

import org.apache.commons.lang3.StringUtils;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

import java.nio.charset.Charset;

/**
 * <h1>MyProtocolEncoder</h1>
 * Created by hanqf on 2023/11/20 11:26.
 */


public class MyProtocolEncoder implements ProtocolEncoder {

    private final Charset charset;

    public MyProtocolEncoder() {
        this.charset = Charset.defaultCharset();
    }

    public MyProtocolEncoder(Charset charset) {
        this.charset = charset;
    }

    @Override
    public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception {
        MyPack myPack = (MyPack) message;
        IoBuffer ioBuffer = IoBuffer.allocate(myPack.getLen()).setAutoExpand(true);
        System.out.println("encode length: " + myPack.getLen());
        ioBuffer.putInt(myPack.getLen());
        ioBuffer.put(myPack.getType());
        ioBuffer.putInt(myPack.getModule());
        ioBuffer.putString(myPack.getSeq(), charset.newEncoder());
        if (StringUtils.isNotBlank(myPack.getBody())) {
            System.out.println("encoder body length:" + myPack.getBody().getBytes().length);
            ioBuffer.putString(myPack.getBody(), charset.newEncoder());
        }
        // 当你组装数据完毕之后，调用flip()方法，为输出做好准备，
        // 切记在write()方法之前，要调用IoBuffer 的flip()方法，否则缓冲区的position 的后面是没有数据可以用来输出的，
        // 你必须调用flip()方法将position 移至0，limit 移至刚才的position。这个flip()方法的含义请参看java.nio.ByteBuffer
        ioBuffer.flip();
        out.write(ioBuffer);
    }

    @Override
    public void dispose(IoSession session) throws Exception {
        // TODO Auto-generated method stub

    }

}

