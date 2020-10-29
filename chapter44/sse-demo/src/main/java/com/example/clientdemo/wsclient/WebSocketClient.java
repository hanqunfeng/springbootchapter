package com.example.clientdemo.wsclient;

import com.example.clientdemo.utils.OkHttpUtil;
import lombok.extern.slf4j.Slf4j;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;

/**
 * <h1>websocketClient</h1>
 * Created by hanqf on 2020/10/29 15:32.
 */

@ClientEndpoint
@Slf4j
public class WebSocketClient {

    private String userId;
    private Session session;

    private WebSocketClient() {
    }

    public WebSocketClient(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    /**
     * 连接建立成功调用的方法
     * <p>
     * javax.websocket.Session
     */
    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        log.info("[" + userId + "]连接加入");
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message) {
        log.info("[" + userId + "]来自服务端的消息：{}", message);
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        log.info("[" + userId + "]连接关闭");
    }

    /**
     * 出现错误
     */
    @OnError
    public void onError(Throwable error) {
        log.error("[" + userId + "]发生错误：{}，Session ID： {}", error.getMessage(), session.getId());
    }

    /**
     * 建立连接
     *
     * @param url websocket地址
    */
    public void createConnect(String url) throws URISyntaxException, IOException, DeploymentException {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        container.connectToServer(this,new URI(url));
    }

    /**
     * 发送消息
     *
     * @param message 消息
     */
    public void sendMessage(String message) throws IOException {
        session.getBasicRemote().sendText(String.format("%s (From Client，Session ID=%s，UserId=%s)", message, session.getId(), userId));
    }

    /**
     * 关闭连接
     */
    public void close() throws IOException {
        try {
            Session session = this.session;
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        finally {
            this.session = null;
        }
    }

    public static void main(String[] args) throws URISyntaxException, IOException, DeploymentException, InterruptedException {

        String userId = "zhangsan";
        String token = OkHttpUtil.get("http://localhost:8081/token/" + userId);

        WebSocketClient webSocketClient = new WebSocketClient(userId);
        webSocketClient.createConnect("ws://localhost:8081/ws/"+webSocketClient.getUserId()+"?token="+token);

        for(int i = 0;i<10;i++){
            webSocketClient.sendMessage("你好:"+i);
            TimeUnit.SECONDS.sleep(1L);
        }

        webSocketClient.close();

    }
}
