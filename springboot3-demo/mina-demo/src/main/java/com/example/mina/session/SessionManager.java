package com.example.mina.session;

/**
 * <h1>SessionManager</h1>
 * Created by hanqf on 2023/11/20 11:41.
 *
 * 方便对session会话进行管理，方便对session会话集合获取和删除
 *
 * 服务端接收到新的Session后，构造一个封装类，实现session 的部分方法，并额外实现方法
 */


public interface SessionManager {

    /**
     * 添加session
     */
    void addSession(String device, MySession session);

    /**
     * 获取session
     */
    MySession getSession(String device);

    /**
     * 替换Session
     */
    void replaceSession(String device, MySession session);

    /**
     * 删除session
     */
    void removeSession(String device);

    /**
     * 删除session
     */
    void removeSession(MySession session);

}

