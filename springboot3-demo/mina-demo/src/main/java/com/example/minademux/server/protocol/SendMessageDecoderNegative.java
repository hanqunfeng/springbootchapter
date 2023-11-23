package com.example.minademux.server.protocol;

/**
 * <h1>SendMessageDecoderNegative</h1>
 * Created by hanqf on 2023/11/23 14:59.
 */


import com.example.minademux.model.SendMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.demux.MessageDecoderAdapter;
import org.apache.mina.filter.codec.demux.MessageDecoderResult;

@Slf4j
public class SendMessageDecoderNegative extends MessageDecoderAdapter {
    /**
     * decodable()方法有三个返回值，分别表示如下的含义：
     * A. MessageDecoderResult.NOT_OK：表示这个解码器不适合解码数据，然后检查其它解码器，如果都不满足会抛异常；
     * B. MessageDecoderResult.NEED_DATA：表示当前的读入的数据不够判断是否能够使用这个解码器解码，然后再次调用decodable()方法检查其它解码器，如果都是NEED_DATA,则等待下次输入；
     * C. MessageDecoderResult.OK： 表示这个解码器可以解码读入的数据， 然后则调用MessageDecoder 的decode()方法。这里注意decodable()方法对参数IoBuffer in 的任何操作在方法结束之后，都会复原，也就是你不必担心在调用decode()方法时，position 已经不在缓冲区的起始位置。这个方法相当于是预读取，用于判断是否是可用的解码器。
    */
    @Override
    public MessageDecoderResult decodable(IoSession session, IoBuffer in) {
        if (in.remaining() < 2)
            return MessageDecoderResult.NEED_DATA;
        else {
            char symbol = in.getChar();
            if (symbol == '-') {
                return MessageDecoderResult.OK;
            } else {
                return MessageDecoderResult.NOT_OK;
            }
        }
    }

    /**
     * decode()方法有三个返回值，分别表示如下的含义：
     * A. MessageDecoderResult.NOT_OK：表示解码失败，会抛异常；
     * B. MessageDecoderResult.NEED_DATA：表示数据不够，需要读到新的数据后，再次调用decode()方法。
     * C. MessageDecoderResult.OK：表示解码成功。
    */
    @Override
    public MessageDecoderResult decode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
        log.info("SendMessageDecoderNegative");
        SendMessage sm = new SendMessage();
        sm.setSymbol(in.getChar());
        sm.setI(-in.getInt());
        sm.setJ(-in.getInt());
        out.write(sm);
        return MessageDecoderResult.OK;
    }

}


