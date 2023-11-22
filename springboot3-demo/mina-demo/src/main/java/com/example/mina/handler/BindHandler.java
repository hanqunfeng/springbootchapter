package com.example.mina.handler;

import com.example.mina.Const;
import com.example.mina.session.MySession;
import com.example.mina.session.SessionManager;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <h1>BindHandler</h1>
 * Created by hanqf on 2023/11/20 11:43.
 */


public class BindHandler implements BaseHandler {

    static final Logger logger = LoggerFactory.getLogger(BindHandler.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    // 获取会话管理类
    @Autowired
    private SessionManager sessionManager;

    @Override
    public String process(MySession mySession, String content) {

        if (StringUtils.isBlank(content)) {
            return null;
        }

        try {
            JsonNode jsonNode = objectMapper.readTree(content);

            // 检查账号是否存在
            String account = jsonNode.get("account").asText();

            // 可增加数据库、redis之类的认证
            if (StringUtils.isBlank(account)) {
                return null;
            }

            // 检查软件版本号
            String version = jsonNode.get("version").asText();
            // 可增加数据库、redis之类的认证
            if (!Const.VERSION.equals(version)) {
                return null;
            }

            mySession.setAttribute(Const.SESSION_KEY, account);
            mySession.setAttribute(Const.TIME_OUT_KEY, 0); // 超时次数设为0

            // 由于客户端断线服务端可能会无法获知的情况，客户端重连时，需要关闭旧的连接
            MySession oldSession = sessionManager.getSession(account);
            if (oldSession != null && !oldSession.equals(mySession)) {
                // 移除account属性
                oldSession.removeAttribute(Const.SESSION_KEY);
                // 移除超时时间
                oldSession.removeAttribute(Const.TIME_OUT_KEY);
                // 替换oldSession
                sessionManager.replaceSession(account, mySession);
                oldSession.close(false);
                logger.info(">>>>>> oldsession close!");
            }
            if (oldSession == null) {
                sessionManager.addSession(account, mySession);
            }
            logger.info(">>>>>> bind success: " + mySession.getNid());
        } catch (Exception e) {
            logger.error(">>>>>> bind error: " + e.getMessage());
            return null;
        }

        return "bind success";
    }

}

