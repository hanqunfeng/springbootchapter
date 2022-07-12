/*
 * ************************************************************
 * Copyright (c) 2020. Beijing CXZH-Tech Co.,Ltd.
 * ************************************************************
 * File:BaseRunner.java
 * 修改历史：(主要历史变动原因及说明)
 *  YYYY-MM-DD      |     Author    |    Change Description
 *  2020-04-11            hanqf           Created
 * ************************************************************
 */

package org.piaoyi.common;/**
 * Created by hanqf on 2020/4/5 16:04.
 */


import org.piaoyi.config.SpringPipelineFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author hanqf
 * @date 2020/4/5 16:04
 */
public abstract class BaseRunner {
    @Autowired
    public SpringPipelineFactory springPipelineFactory;
    //启动线程数
    protected int thread = 1;
    //间隔时间，毫秒
    protected int interval = 2000;


    public void setThread(int thread) {
        this.thread = thread;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    /**
     * 启动方法
     *
     * @author hanqf
     * @date2020/4/10 11:20
     * @since
     */
    public abstract void start();
}
