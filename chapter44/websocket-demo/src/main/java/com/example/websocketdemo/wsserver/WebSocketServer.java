package com.example.websocketdemo.wsserver;

import com.example.websocketdemo.controller.TokenController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
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
@ServerEndpoint(value = "/ws/{userId}")
public class WebSocketServer {

    //用来统计连接客户端的数量
    private static final AtomicInteger OnlineCount = new AtomicInteger(0);
    // concurrent包的线程安全Set，用来存放每个客户端对应的Session对象。
    //private static CopyOnWriteArraySet<Session> SessionSet = new CopyOnWriteArraySet<>();

    private static ConcurrentHashMap<String, Session> sessionMap = new ConcurrentHashMap<>();

    /**
     * 发送消息，实践表明，每次浏览器刷新，session会发生变化。
     *
     * @param session session
     * @param message 消息
     */
    private static void sendMessage(Session session, String message, String userId) throws IOException {

        session.getBasicRemote().sendText(String.format("%s (From Server，Session ID=%s，UserId=%s)", message, session.getId(), userId));

    }

    /**
     * 群发消息
     * 服务器向所有在线的javax.websocket.Session用户发送消息。
     *
     * @param message 消息
     */
    public static void broadCastInfo(String message) throws IOException {

        sessionMap.forEach(LambdaBiConsumer.wrapper((k, v) -> {
            Session session = sessionMap.get(k);
            if (session.isOpen()) {
                sendMessage(session, message, k);
            }
        }));

        //for (String userId : sessionMap.keySet()) {
        //    Session session = sessionMap.get(userId);
        //    if (session.isOpen()) {
        //        sendMessage(session, message, userId);
        //    }
        //}

    }

    /**
     * 连接建立成功调用的方法
     * <p>
     * javax.websocket.Session
     */
    @OnOpen
    public void onOpen(@PathParam(value = "userId") String userId, Session session) throws IOException {
        List<String> list = session.getRequestParameterMap().get("token");
        String token = null;
        if (list != null && list.size() > 0) {
            token = list.get(0);
        }
        if (TokenController.tokenMap.get(userId) != null && TokenController.tokenMap.get(userId).equals(token)) {
            sessionMap.put(userId, session);
            int cnt = OnlineCount.incrementAndGet(); // 在线数加1
            log.info("[" + userId + "]连接加入，当前连接数为：{}", cnt);
        } else {
            log.info("[" + userId + "]认证失败");
            session.close();
        }
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, @PathParam(value = "userId") String userId, Session session) throws IOException {
        log.info("[" + userId + "]来自客户端的消息：{}", message);
        //sendMessage(session, "Echo消息内容："+message);
        //使用一个或多个浏览器打开测试页面即可
        broadCastInfo(message); //群发消息
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(@PathParam(value = "userId") String userId, Session session) {
        sessionMap.remove(userId);
        int cnt = OnlineCount.decrementAndGet();
        if (cnt < 0) {
            OnlineCount.set(0);
        }
        log.info("[" + userId + "]连接关闭，当前连接数为：{}", cnt);
    }

    /**
     * 出现错误
     */
    @OnError
    public void onError(@PathParam(value = "userId") String userId, Session session, Throwable error) {
        log.error("[" + userId + "]发生错误：{}，Session ID： {}", error.getMessage(), session.getId());
    }

}
