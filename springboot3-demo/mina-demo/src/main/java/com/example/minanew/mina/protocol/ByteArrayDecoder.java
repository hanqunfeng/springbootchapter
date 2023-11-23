package com.example.minanew.mina.protocol;

import com.example.minanew.utils.MinaUtil;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import java.nio.charset.Charset;

public class ByteArrayDecoder extends CumulativeProtocolDecoder {

    private final Charset charset;

    public ByteArrayDecoder(Charset charset) {
        this.charset = charset;

    }

    @Override
    /**
     * 解码方法，从IoBuffer中提取完整消息并输出到ProtocolDecoderOutput。
     * @param session IoSession对象
     * @param in 输入的IoBuffer
     * @param out 输出的ProtocolDecoderOutput
     * @return 如果解码成功，返回true；否则返回false。
     * @throws Exception 可能抛出的异常
     */
    protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
        // 检查是否有足够字节用于处理包头
        if (hasEnoughBytesForHeader(in)) {
            // 标记当前位置
            in.mark();
            // 读取包头信息
            byte[] headerBytes = readBytesFromBuffer(in, 4);

            // 检查是否有足够字节用于处理内容长度信息
            if (hasEnoughBytesForContentLength(in)) {
                // 内容长度信息不足，重置位置并返回false表示断包
                in.reset();
                return false;
            }

            // 读取内容长度信息
            int contentLength = readContentLength(in);

            // 检查是否有足够字节用于处理消息内容
            if (hasEnoughBytesForMessageContent(in, contentLength)) {
                // 消息内容不足，重置位置并返回false表示断包
                in.reset();
                return false;
            }

            // 读取消息内容并输出到ProtocolDecoderOutput
            byte[] messageBytes = readBytesFromBuffer(in, contentLength);
            out.write(new String(messageBytes, charset));

            // 检查是否有足够字节用于处理包尾
            if (hasEnoughBytesForFooter(in)) {
                // 包尾不足，重置位置并返回false表示断包
                in.reset();
                return false;
            }

            // 读取包尾信息
            readBytesFromBuffer(in, 4);

            // 如果IoBuffer中还有剩余字节，表示可能发生了粘包，返回true等待下一次调用doDecode()处理剩余数据
            return in.remaining() > 0;
        }

        // 包头不足，返回false表示断包
        return false;
    }

    /**
     * 检查是否有足够字节用于处理包头。
     * @param in 输入的IoBuffer
     * @return 如果有足够字节，返回true；否则返回false。
     */
    private boolean hasEnoughBytesForHeader(IoBuffer in) {
        return in.remaining() > 4;
    }

    /**
     * 检查是否有足够字节用于处理内容长度信息。
     * @param in 输入的IoBuffer
     * @return 如果内容长度信息不足，返回true；否则返回false。
     */
    private boolean hasEnoughBytesForContentLength(IoBuffer in) {
        return in.remaining() < 4;
    }

    /**
     * 读取内容长度信息。
     * @param in 输入的IoBuffer
     * @return 内容长度
     */
    private int readContentLength(IoBuffer in) {
        byte[] lengthBytes = readBytesFromBuffer(in, 4);
        return MinaUtil.byteArrayToInt(lengthBytes);
    }

    /**
     * 检查是否有足够字节用于处理消息内容。
     * @param in 输入的IoBuffer
     * @param contentLength 消息内容的长度
     * @return 如果消息内容不足，返回true；否则返回false。
     */
    private boolean hasEnoughBytesForMessageContent(IoBuffer in, int contentLength) {
        return in.remaining() < contentLength;
    }

    /**
     * 检查是否有足够字节用于处理包尾。
     * @param in 输入的IoBuffer
     * @return 如果包尾不足，返回true；否则返回false。
     */
    private boolean hasEnoughBytesForFooter(IoBuffer in) {
        return in.remaining() < 4;
    }

    /**
     * 从IoBuffer中读取指定长度的字节。
     * @param in 输入的IoBuffer
     * @param length 要读取的字节长度
     * @return 读取到的字节数组
     */
    private byte[] readBytesFromBuffer(IoBuffer in, int length) {
        byte[] bytes = new byte[length];
        in.get(bytes);
        return bytes;
    }

    //为了降低代码复杂度，对该方法进行了重构，将一些逻辑拆分为独立的方法
//    protected boolean doDecode(IoSession session, IoBuffer in,
//                               ProtocolDecoderOutput out) throws Exception {
//        // 丢包，断包处理
//        if (in.remaining() > 4)// 有包头，包头足够
//        {
//            in.mark();// 标记当前position的快照标记mark，以便后继的reset操作能恢复position位置，开始是0
//            byte[] l = new byte[4];
//            in.get(l);// 读取包头，占4个字节
//            if (in.remaining() < 4)// 内容长度的4个字节不够，断包
//            {
//                in.reset();
//                return false;//
//            } else {// 内容长度的4个字节数组足够
//                byte[] bytesLegth = new byte[4];// 内容长度
//                in.get(bytesLegth);// 读取内容长度,int类型，占四个字节
//                int len = MinaUtil.byteArrayToInt(bytesLegth);// 内容长度有多少
//                if (in.remaining() < len)// 内容不够，断包
//                {
//                    in.reset();
//                    return false;//
//                } else { // 消息内容足够
//                    byte[] bytes = new byte[len];
//                    in.get(bytes, 0, len);
//                    out.write(new String(bytes, charset));// 读取内容，并且发送
//                    if (in.remaining() < 4) {// 包尾不够
//                        in.reset();
//                        return false;//
//                    } else {// 包尾足够
//                        byte[] tails = new byte[4];
//                        in.get(tails);// 读取包尾
//                        if (in.remaining() > 0)// 最后如果粘了包，会再次调用doDeocde()方法，把剩余数据给doDeocde()方法处理
//                        {
//                            return true;
//                        }
//
//                    }
//                }
//
//            }
//
//        }
//        return false;// 断包，或者执行完，
//    }

}
