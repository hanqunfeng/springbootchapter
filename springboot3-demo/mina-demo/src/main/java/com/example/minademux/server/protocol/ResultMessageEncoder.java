package com.example.minademux.server.protocol;

/**
 * <h1>ResultMessageEncoder</h1>
 * Created by hanqf on 2023/11/23 15:00.
 */


import com.example.minademux.model.ResultMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.apache.mina.filter.codec.demux.MessageEncoder;

@Slf4j
public class ResultMessageEncoder implements MessageEncoder<ResultMessage> {
    @Override
    public void encode(IoSession session, ResultMessage message, ProtocolEncoderOutput out) throws Exception {
        log.info("ResultMessageEncoder");
        IoBuffer buffer = IoBuffer.allocate(4);
        buffer.putInt(message.getResult());
        buffer.flip();
        out.write(buffer);
    }
}

