/*
 * ************************************************************
 * Copyright (c) 2020. Beijing CXZH-Tech Co.,Ltd.
 * ************************************************************
 * File:BaseBeanRunner.java
 * 修改历史：(主要历史变动原因及说明)
 *  YYYY-MM-DD      |     Author    |    Change Description
 *  2020-04-11            hanqf           Created
 * ************************************************************
 */

package org.piaoyi.common;
/**
 * Created by hanqf on 2020/4/5 16:04.
 */

import java.util.Map;

/**
 * @author hanqf
 * @date 2020/4/5 16:04
 */
public abstract class BaseBeanRunner extends BaseRunner {

    /**
     * 启动抓取线程
     */
    @Override
    public void start(Map<String, Object> map) {
        init(map);
        makeGeccoEngine(this.getClass().getPackage().getName(), makeHttpUrls(map));
    }

}
