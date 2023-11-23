package com.example.minademux;

/**
 * <h1>MathProtocolCodecFactory</h1>
 * Created by hanqf on 2023/11/23 15:02.
 */


import com.example.minademux.client.protocol.ResultMessageDecoder;
import com.example.minademux.client.protocol.SendMessageEncoder;
import com.example.minademux.model.ResultMessage;
import com.example.minademux.model.SendMessage;
import com.example.minademux.server.protocol.ResultMessageEncoder;
import com.example.minademux.server.protocol.SendMessageDecoderNegative;
import com.example.minademux.server.protocol.SendMessageDecoderPositive;
import org.apache.mina.filter.codec.demux.DemuxingProtocolCodecFactory;

/**
 * 这个工厂类我们使用了构造方法的一个布尔类型的参数，以便其可以在Server 端、Client端同时使用。
*/
public class MathProtocolCodecFactory extends DemuxingProtocolCodecFactory {

    /**
     * DemuxingProtocolDecoder 内部在维护了一个MessageDecoder数组，用于保存添加的所有的消息解码器，
     * 每次decode()的时候就调用每个MessageDecoder的decodable()方法逐个检查，
     * 只要发现一个MessageDecoder 不是对应的解码器，就从数组中移除，直到找到合适的MessageDecoder，
     * 如果最后发现数组为空，就表示没找到对应的MessageDecoder，最后抛出异常。
    */
    public MathProtocolCodecFactory(boolean server) {
        if (server) {
            //基于类型配置编码器，不同类型对应不同的编码器，这里可以添加多个编码器
            super.addMessageEncoder(ResultMessage.class, ResultMessageEncoder.class);
            //添加解码器，可以添加多个解码器
            super.addMessageDecoder(SendMessageDecoderPositive.class);
            super.addMessageDecoder(SendMessageDecoderNegative.class);
        } else {
            super.addMessageEncoder(SendMessage.class, SendMessageEncoder.class);
            super.addMessageDecoder(ResultMessageDecoder.class);
        }
    }
}

