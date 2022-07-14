/*
 * ************************************************************
 * Copyright (c) 2020. Beijing CXZH-Tech Co.,Ltd.
 * ************************************************************
 * File:BaseDynamicRunner.java
 * 修改历史：(主要历史变动原因及说明)
 *  YYYY-MM-DD      |     Author    |    Change Description
 *  2020-04-11            hanqf           Created
 * ************************************************************
 */

package org.piaoyi.common;

import java.util.Map;

/**
 * ${DESCRIPTION}
 * Created by hanqf on 2018/11/28 16:41.
 */


public abstract class BaseDynamicRunner extends BaseRunner {

    /**
     * 构建DynamicGecco
     */
    public abstract void makeDynamicGecco();

    /**
     * 启动抓取线程
     */
    @Override
    public void start(Map<String, Object> map) {
        init(map);
        makeDynamicGecco();
        makeGeccoEngine(this.getClass().getName(), makeHttpUrls(map));
    }

}
