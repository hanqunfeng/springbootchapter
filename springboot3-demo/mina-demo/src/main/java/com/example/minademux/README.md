假设一段数据发送过来之后，需要根据某种条件决定使用哪个解码器，而不是像上面的例子，固定使用一个解码器，那么该如何做呢？
幸好Mina 提供了org.apache.mina.filter.codec.demux 包来完成这种多路分离（Demultiplexes）的解码工作，也就是同时注册多个解码器，
然后运行时依据传入的数据决定到底使用哪个解码器来工作。所谓多路分离就是依据条件分发到指定的解码器，
譬如：上面的短信协议进行扩展，可以依据状态行来判断使用1.0 版本的短信协议解码器还是2.0版本的短信协议解码器。

下面我们使用一个简单的例子，说明这个多路分离的解码器是如何使用的，需求如下所示：
(1.) 客户端传入两个int 类型的数字，还有一个char 类型的符号。
(2.) 如果符号是+，服务端就是用1 号解码器，对两个数字相加，然后把结果返回给客户端。
(3.) 如果符号是-，服务端就使用2 号解码器，将两个数字变为相反数，然后相加，把结果返回给客户端。

Demux 开发编解码器主要有如下几个步骤：
A. 定义Client 端、Server 端发送、接收的数据对象。
B. 使用Demux 编写编码器是实现MessageEncoder接口，T 是你要编码的数据对象，这个MessageEncoder 会在DemuxingProtocolEncoder 中调用。
C. 使用Demux 编写编码器是实现MessageDecoder 接口，这个MessageDecoder 会在DemuxingProtocolDecoder 中调用。
D. 在 DemuxingProtocolCodecFactory 中调用addMessageEncoder()、addMessageDecoder()方法组装编解码器。
