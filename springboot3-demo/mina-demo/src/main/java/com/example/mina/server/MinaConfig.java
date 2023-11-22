package com.example.mina.server;

import com.example.mina.Const;
import com.example.mina.handler.BaseHandler;
import com.example.mina.handler.BindHandler;
import com.example.mina.handler.ServerHandler;
import com.example.mina.handler.TimeCheckHandler;
import com.example.mina.keepAlive.KeepAliveFactoryImpl;
import com.example.mina.protocol.MyProtocolCodecFactory;
import com.example.mina.session.DefaultSessionManagerImpl;
import com.example.mina.session.SessionManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.filterchain.IoFilter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.filter.keepalive.KeepAliveFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.filter.logging.MdcInjectionFilter;
import org.apache.mina.integration.beans.InetSocketAddressEditor;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.springframework.beans.factory.config.CustomEditorConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.beans.PropertyEditor;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * <h1> MinaConfig</h1>
 * Created by hanqf on 2023/11/20 11:15.
 */
@Slf4j
@Configuration
public class MinaConfig {

    /**
     * 设置I/O接收器
     * @return
     */
    private static Map<Class<?>, Class<? extends PropertyEditor>> customEditors = new HashMap<>();

    /**
     * 设置I/O接收器，指定接收到请求后交给handler处理
     * 此部分被 NioSocketAcceptor 隐式使用，无此则会报字符串无法转换成 InetSocketAddress
     * 从字符串到 SocketAddress 的转换，会偿试使用该自定义属性编辑器
    */
    @Bean
    public static CustomEditorConfigurer customEditorConfigurer() {
        customEditors.put(SocketAddress.class, InetSocketAddressEditor.class);
        CustomEditorConfigurer configurer = new CustomEditorConfigurer();
        configurer.setCustomEditors(customEditors);
        return configurer;
    }

    /**
     * 线程池filter
     *
     * 多线程处理
     * 在处理流程中加入线程池，可以较好的提高服务器的吞吐量，但也带来了新的问题：请求的处理顺序问题。
     * 在单线程的模型下，可以保证IO请求是挨个顺序地处理的。
     * 加入线程池之后，同一个IoSession的多个IO请求可能被ExecutorFilter并行的处理，这对于一些对请求处理顺序有要求的程序来说是不希望看到的。
     * 比如：数据库服务器处理同一个会话里的prepare，execute，commit请求希望是能按顺序逐一执行的。
     *
     * Mina里默认的实现是有保证同一个IoSession中IO请求的顺序的。
     * 具体的实现是，ExecutorFilter默认采用了Mina提供的OrderedThreadPoolExecutor作为内置线程池。
     * 后者并不会立即执行加入进来的Runnable对象，而是会先从Runnable对象里获取关联的IoSession(这里有个down cast成IoEvent的操作)，
     * 并将Runnable对象加入到session的任务列表中。
     * OrderedThreadPoolExecutor会按session里任务列表的顺序来处理请求，从而保证了请求的执行顺序。
     *
     * 对于没有顺序要请求的情况，可以为ExecutorFilter指定一个Executor来替换掉默认的OrderedThreadPoolExecutor，
     * 让同一个session的多个请求能被并行地处理，来进一步提高吞吐量。
     */
    @Bean
    public ExecutorFilter executorFilter() {
        return new ExecutorFilter();
    }

    /**
     * 日志信息注入过滤器，MDC(Mapped Diagnostic Context有译作线程映射表)是日志框架维护的一组信息键值对，
     * 可向日志输出信息中插入一些想要显示的内容。
     *
     */
    @Bean
    public MdcInjectionFilter mdcInjectionFilter() {
        return new MdcInjectionFilter(MdcInjectionFilter.MdcKey.remoteAddress);
    }

    /**
     * 日志filter
     */
    @Bean
    public LoggingFilter loggingFilter() {
        return new LoggingFilter();
    }

    /**
     * 编解码器filter
     */
    @Bean
    public ProtocolCodecFilter protocolCodecFilter() {
        return new ProtocolCodecFilter(new MyProtocolCodecFactory());
    }

    /**
     * 心跳包
     */
    @Bean
    public KeepAliveFactoryImpl keepAliveFactoryImpl() {
        return new KeepAliveFactoryImpl();
    }

    /**
     * 心跳filter
     */
    @Bean
    public KeepAliveFilter keepAliveFilter(KeepAliveFactoryImpl keepAliveFactory) {
        // 注入心跳工厂，读写空闲
        KeepAliveFilter filter = new KeepAliveFilter(keepAliveFactory, IdleStatus.BOTH_IDLE);
        // 设置是否forward到下一个filter
        filter.setForwardEvent(true);
        // 设置心跳频率，单位是秒，我这里设置的180，也就是3分钟
        filter.setRequestInterval(Const.IDELTIMEOUT);
        return filter;
    }

    @Bean
    public SessionManager sessionManager(){
        return new DefaultSessionManagerImpl();
    }

    private HashMap<Integer, BaseHandler> handlers = new HashMap<>();

    @Bean
    public BindHandler bindHandler(){
        return new BindHandler();
    }
    @Bean
    public TimeCheckHandler timeCheckHandler(){
        return new TimeCheckHandler();
    }

    /**
     * 业务处理类
     * 本例列举两个业务处理类：BindHandler、TimeCheckHandler
     * 心跳包业务是单独实现的
     *
     * BindHandler负责处理登录业务
     * TimeCheckHandler负责获取服务器的系统时间
     *
     * handlers集合是为了方便处理业务时，根据不同模块获取相应的处理类
    */
    @Bean
    public ServerHandler serverHandler() {
        handlers.put(Const.AUTHEN, bindHandler());
        handlers.put(Const.TIME_CHECK, timeCheckHandler());
        ServerHandler serverHandler = new ServerHandler();
        serverHandler.setHandlers(handlers);
        return serverHandler;
    }


    /**
     * 过滤器链
     * 主要用于拦截和过滤网络传输中I/O操作的各种消息，是在应用层和我们业务员层之间的过滤层
     *
     * 主要作用：
     *
     * 记录事件的日志（Mina默认提供了LoggingFilter）
     * 测量系统性能
     * 信息验证
     * 过载控制
     * 信息的转换(主要就是编码和解码)
     * 和其他更多的信息
     *
     *
     * 最后将心跳包检测加入到过滤器链中,
     * 注意：
     * 当你使用自定的ProtocolCodecFactory时候一定要将线程池配置在该过滤器之后，
     * 因为你自己实现的ProtocolCodecFactory直接读取和转换的是二进制数据，这些数据都是由和CPU绑定的I/O Processor来读取和发送的，
     * 因此为了不影响系统的性能，也应该将数据的编解码操作绑定到I/O Processor线程中，因为在Java中创建和线程切换都是比较耗资源的，
     * 因此建议将ProtocolCodecFactory配置在ExecutorFilter的前面
    */
    @Bean
    public DefaultIoFilterChainBuilder defaultIoFilterChainBuilder(ExecutorFilter executorFilter,
                                                                   MdcInjectionFilter mdcInjectionFilter, ProtocolCodecFilter protocolCodecFilter, LoggingFilter loggingFilter,
                                                                   KeepAliveFilter keepAliveFilter) {
        DefaultIoFilterChainBuilder filterChainBuilder = new DefaultIoFilterChainBuilder();
        Map<String, IoFilter> filters = new LinkedHashMap<>();
        filters.put("mdcInjectionFilter", mdcInjectionFilter);
        filters.put("protocolCodecFilter", protocolCodecFilter);
        filters.put("executor", executorFilter);
        filters.put("keepAliveFilter", keepAliveFilter);
        filterChainBuilder.setFilters(filters);
        return filterChainBuilder;
    }

    /**
     * 创建连接
     * @return
     */
    @Bean(initMethod = "init", destroyMethod = "dispose")
    public NioSocketAcceptor nioSocketAcceptor(ServerHandler serverHandler,
                                               DefaultIoFilterChainBuilder defaultIoFilterChainBuilder) {
        NioSocketAcceptor acceptor = new NioSocketAcceptor();
        acceptor.getSessionConfig().setReadBufferSize(2048);
        acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, Const.IDELTIMEOUT);
        // 绑定过滤器链
        acceptor.setFilterChainBuilder(defaultIoFilterChainBuilder);
        acceptor.setHandler(serverHandler);
        return acceptor;
    }






}
