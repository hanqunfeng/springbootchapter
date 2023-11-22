package com.example.mina.protocol;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * <h1>MyPack</h1>
 * Created by hanqf on 2023/11/20 11:23.
 *
 * 自定义协议以及编解码器
 * 底层的传输与交互都是采用二进制的方式。
 * 如何判断发送的消息已经结束，就需要通过协议来规定，比如收到换行符等标识时，判断为结束等。
 *
 * 根据协议，把二进制数据转换成Java对象称为解码（也叫做拆包），把Java对象转换为二进制数据称为编码（也叫做打包）。
 *
 * 常用的协议制定方法：
 *
 * 定长消息法：这种方式是使用长度固定的数据发送，一般适用于指令发送。譬如：数据发送端规定发送的数据都是双字节，AA 表示启动、BB 表示关闭等等
 * 字符定界法：这种方式是使用特殊字符作为数据的结束符，一般适用于简单数据的发送。譬如：在消息的结尾自动加上文本换行符（Windows使用\r\n，Linux使用\n）,接收方见到文本换行符就认为是一个完整的消息，结束接收数据开始解析。注意：这个标识结束的特殊字符一定要简单，常常使用ASCII码中的特殊字符来标识（会出现粘包、半包情况）。
 * 定长报文头法：使用定长报文头，在报文头的某个域指明报文长度。该方法最灵活，使用最广。譬如：协议为-- 协议编号（1字节）+数据长度（4个字节）+真实数据。请求到达后，解析协议编号和数据长度，根据数据长度来判断后面的真实数据是否接收完整。HTTP 协议的消息报头中的Content-Length 也是表示消息正文的长度，这样数据的接收端就知道到底读到多长的字节数就不用再读取数据了。
 * 实际应用中，采用最多的还是定长报文头法。
 *
 *
 *
 * 自定义协议
 * 首先来实现一个自定义协议：
 * 协议组成： 数据长度（4个字节） + 协议编号（1字节）+ 模块代码（4字节）+ 序列号（4字节）+ 真实数据。
 */

@Data
public class MyPack {

    // 数据总长度
    private int len;

    // 数据传输类型：0x00-设备到服务端 0x01-服务端到设备
    private byte type = 0x01;

    // 模块代码
    private int module;

    /**
     *  此域表示一个序列号，使用在异步通信模式下，由消息发起者设定，应答者对应给回此序列号。
     *  序列号范围：0000－9999，循环使用。
     *  同步方式下该域保留。
     **/
    private String seq;

    // 包体
    private String body;

    /**
     * 0x00表示客户端到服务端
     */
    public static final byte REQUEST = 0x00;
    /**
     * 0x01表示服务端到客户端
     */
    public static final byte RESPONSE = 0x01;

    // 包头长度
    public static final int PACK_HEAD_LEN = 13;

    // 最大长度
    public static final int MAX_LEN = 9999;

    public MyPack(int module, String seq, String body) {
        this.module = module;
        this.seq = seq;
        this.body = body;
        // 总长度
        this.len = PACK_HEAD_LEN + (StringUtils.isBlank(body) ? 0 : body.getBytes().length);
    }

    public MyPack(int module, String seq, String body,byte type) {
        this.module = module;
        this.seq = seq;
        this.body = body;
        // 总长度
        this.len = PACK_HEAD_LEN + (StringUtils.isBlank(body) ? 0 : body.getBytes().length);
        this.type = type;
    }
    // getter/setter...
}

