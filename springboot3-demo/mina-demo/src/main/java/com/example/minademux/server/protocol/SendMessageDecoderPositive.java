package com.example.minademux.server.protocol;

/**
 * <h1>SendMessageDecoderPositive</h1>
 * Created by hanqf on 2023/11/23 14:57.
 */


import com.example.minademux.model.SendMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.demux.MessageDecoderAdapter;
import org.apache.mina.filter.codec.demux.MessageDecoderResult;

@Slf4j
public class SendMessageDecoderPositive extends MessageDecoderAdapter {

    /**
     * 因为客户端发送的SendMessage 的前两个字节（char）就是符号位，所以我们在decodable()方法中对此条件进行了判断，
     * 之后读到两个字节，并且这两个字节表示的字符是+时，才认为这个解码器可用。
    */
    @Override
    public MessageDecoderResult decodable(IoSession session, IoBuffer in) {
        if (in.remaining() < 2)
            return MessageDecoderResult.NEED_DATA;
        else {
            char symbol = in.getChar();
            if (symbol == '+') {
                return MessageDecoderResult.OK;
            } else {
                return MessageDecoderResult.NOT_OK;
            }
        }
    }

    @Override
    public MessageDecoderResult decode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
        log.info("SendMessageDecoderPositive");
        SendMessage sm = new SendMessage();
        sm.setSymbol(in.getChar());
        sm.setI(in.getInt());
        sm.setJ(in.getInt());
        out.write(sm);
        return MessageDecoderResult.OK;
    }

}


