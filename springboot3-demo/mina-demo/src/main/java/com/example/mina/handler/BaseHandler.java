package com.example.mina.handler;

/**
 * <h1>BaseHandler</h1>
 * Created by hanqf on 2023/11/20 11:42.
 */


import com.example.mina.session.MySession;

/**
 * Mina的请求处理接口，必须实现此接口
 *
 */
public interface BaseHandler {
    String process(MySession mySession, String content);
}

