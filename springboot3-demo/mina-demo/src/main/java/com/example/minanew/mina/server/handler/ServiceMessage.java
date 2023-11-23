package com.example.minanew.mina.server.handler;

import com.example.minanew.utils.DateUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Socket信息处理
 *
 * @author Administrator
 */

public class ServiceMessage {

    Logger logger = LoggerFactory.getLogger(getClass());
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String KEY = "mid";

    public void messageHandler(IoSession session, JsonNode mes) {
        logger.info("--->开始处理Socket消息<---");
        logger.info("Session--->{}", session.getId());
        logger.info("接受数据--->{}", mes);
        logger.info("当前时间-->{}", DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss:SSS"));
        Integer mid = mes.get(KEY).asInt();
        logger.info("mid--->{}", mid);

        //子服务器登陆
        Map<String, Object> map = new HashMap<>();
        map.put("mid", mid);
        map.put("msg", "hello,欢迎你登陆【监控服务器】.");
        logger.info("---子服务器监控服务器已登陆---");
        try {
            session.write(objectMapper.writeValueAsString(map));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }


}
