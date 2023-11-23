package com.example.minanew.mina.protocol;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

import java.nio.charset.Charset;


public class ByteArrayEncoder extends ProtocolEncoderAdapter {

    private final Charset charset;

    public ByteArrayEncoder(Charset charset) {
        this.charset = charset;

    }

    @Override
    public void encode(IoSession session, Object message,
                       ProtocolEncoderOutput out) throws Exception {
        // 仿项目，解决断包，粘包问题
        String value = (message == null ? "" : message.toString());// 消息值
        byte[] content = value.getBytes(charset);// 消息内容,字节数组
        IoBuffer buf = IoBuffer.allocate(38 + content.length).setAutoExpand(true);// 缓冲区容量大小38字节加上字符长度
        buf.put(new byte[]{0, -86, -69, -52});// 输入包开头固定值十六进制00 aa bb cc,转化成字节数组
        buf.putUnsignedInt(content.length);// int为4字节，一个字节等于2个16进制字符，所以有八位 00 00 00 0c，内容长度。
        buf.put(content);// 消息内容
        buf.put(new byte[]{0, -86, -69, -52});// 包尾
        // 当你组装数据完毕之后，调用flip()方法，为输出做好准备，
        // 切记在write()方法之前，要调用IoBuffer 的flip()方法，否则缓冲区的position 的后面是没有数据可以用来输出的，
        // 你必须调用flip()方法将position 移至0，limit 移至刚才的position。这个flip()方法的含义请参看java.nio.ByteBuffer
        buf.flip();
        out.write(buf);// 写入
    }


    public static void main(String[] args) {

        try {
            String value = "200";// 消息值
            byte[] content = value.getBytes("UTF-8");// 消息内容,字节数组
            System.out.println(content.toString());
            IoBuffer buf = IoBuffer.allocate(38 + content.length).setAutoExpand(true);// 缓冲区容量大小38字节加上字符长度
            buf.put(new byte[]{0, -86, -69, -52});// 输入包开头固定值十六进制00 aa bb cc,转化成字节数组
            buf.putUnsignedInt(content.length);// int为4字节，一个字节等于2个16进制字符，所以有八位 00 00 00 0c，内容长度。
            buf.put(content);// 消息内容
            buf.put(new byte[]{0, -86, -69, -52});// 包尾
            buf.flip();

            System.out.println(buf);

        } catch (Exception e) {
            // TODO: handle exception
        }

    }

}
