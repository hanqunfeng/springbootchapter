package com.example.minademux.client.protocol;

/**
 * <h1>SendMessageEncoder</h1>
 * Created by hanqf on 2023/11/23 14:56.
 */


import com.example.minademux.model.SendMessage;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.apache.mina.filter.codec.demux.MessageEncoder;

public class SendMessageEncoder implements MessageEncoder<SendMessage> {
    @Override
    public void encode(IoSession session, SendMessage message, ProtocolEncoderOutput out) throws Exception {
        // 这里我们的SendMessage、ResultMessage 中的字段都是用长度固定的基本数据类型，这样IoBuffer 就不需要自动扩展了，提高性能。
        // 按照一个char、两个int 计算，这里的IoBuffer只需要10 个字节的长度就可以了。
        IoBuffer buffer = IoBuffer.allocate(10);
        buffer.putChar(message.getSymbol());
        buffer.putInt(message.getI());
        buffer.putInt(message.getJ());
        buffer.flip();
        out.write(buffer);
    }
}


