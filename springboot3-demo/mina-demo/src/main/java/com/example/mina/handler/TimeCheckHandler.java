package com.example.mina.handler;

import com.example.mina.session.MySession;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * <h1>TimeCheckHandler</h1>
 * Created by hanqf on 2023/11/20 11:43.
 */


public class TimeCheckHandler implements BaseHandler {

    static final Logger logger = LoggerFactory.getLogger(TimeCheckHandler.class);


    @Override
    public String process(MySession mySession, String content) {

        if (StringUtils.isBlank(content)) {
            return null;
        }

        try {

            // 平台系统时间
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            String time = sdf.format(new Date());
            return time;

        } catch (Exception e) {
            logger.error(">>>>>> time check error: " + e.getMessage());
            return null;
        }
    }

}

