package com.example.websocketdemo.wsserver;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <h1>WebSocketServer</h1>
 * Created by hanqf on 2020/10/29 11:27.
 *
 * @ServerEndpoint(value = "/ws/asset")表示websocket的接口服务地址
 * @OnOpen注解的方法，为连接建立成功时调用的方法
 * @OnClose注解的方法，为连接关闭调用的方法
 * @OnMessage注解的方法，为收到客户端消息后调用的方法
 * @OnError注解的方法，为出现异常时调用的方法
 */


@Component
@Slf4j
@ServerEndpoint(value = "/ws/asset")
public class WebSocketServer {

    //用来统计连接客户端的数量
    private static final AtomicInteger OnlineCount = new AtomicInteger(0);
    // concurrent包的线程安全Set，用来存放每个客户端对应的Session对象。
    private static CopyOnWriteArraySet<Session> SessionSet = new CopyOnWriteArraySet<>();

    /**
     * 发送消息，实践表明，每次浏览器刷新，session会发生变化。
     *
     * @param session session
     * @param message 消息
     */
    private static void sendMessage(Session session, String message) throws IOException {

        session.getBasicRemote().sendText(String.format("%s (From Server，Session ID=%s)", message, session.getId()));

    }

    /**
     * 群发消息
     * 服务器向所有在线的javax.websocket.Session用户发送消息。
     *
     * @param message 消息
     */
    public static void broadCastInfo(String message) throws IOException {
        for (Session session : SessionSet) {
            if (session.isOpen()) {
                sendMessage(session, message);
            }
        }
    }

    /**
     * 连接建立成功调用的方法
     * <p>
     * javax.websocket.Session
     */
    @OnOpen
    public void onOpen(Session session) {
        SessionSet.add(session);
        int cnt = OnlineCount.incrementAndGet(); // 在线数加1
        log.info("有连接加入，当前连接数为：{}", cnt);
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        log.info("来自客户端的消息：{}", message);
        //sendMessage(session, "Echo消息内容："+message);
        //使用一个或多个浏览器打开测试页面即可
        broadCastInfo(message); //群发消息
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(Session session) {
        SessionSet.remove(session);
        int cnt = OnlineCount.decrementAndGet();
        log.info("有连接关闭，当前连接数为：{}", cnt);
    }

    /**
     * 出现错误
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("发生错误：{}，Session ID： {}", error.getMessage(), session.getId());
    }

}
