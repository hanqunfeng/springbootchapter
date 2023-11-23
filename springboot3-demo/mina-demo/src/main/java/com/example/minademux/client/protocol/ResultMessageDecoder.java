package com.example.minademux.client.protocol;

/**
 * <h1>ResultMessageDecoder</h1>
 * Created by hanqf on 2023/11/23 15:00.
 */


import com.example.minademux.model.ResultMessage;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.demux.MessageDecoderAdapter;
import org.apache.mina.filter.codec.demux.MessageDecoderResult;

public class ResultMessageDecoder extends MessageDecoderAdapter {
    @Override
    public MessageDecoderResult decodable(IoSession session, IoBuffer in) {
        if (in.remaining() < 4)
            return MessageDecoderResult.NEED_DATA;
        else if (in.remaining() == 4)
            return MessageDecoderResult.OK;
        else
            return MessageDecoderResult.NOT_OK;
    }

    @Override
    public MessageDecoderResult decode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
        ResultMessage rm = new ResultMessage();
        rm.setResult(in.getInt());
        out.write(rm);
        return MessageDecoderResult.OK;
    }

}

