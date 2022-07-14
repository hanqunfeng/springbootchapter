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

import com.geccocrawler.gecco.GeccoEngine;

import java.util.Map;

/**
 * ${DESCRIPTION}
 * Created by hanqf on 2018/11/28 16:41.
 */


public abstract class BaseDynamicRunner extends BaseRunner {


    /**
     * 根据栏目关键字替换启动URL，并封装
     *
     * @return a
     * @throws
     * @author hanqf
     * @date2020/4/10 11:20
     * @since
     */
    public abstract String[] makeHttpUrls();


    /**
     * 构建DynamicGecco
     */
    public abstract void makeDynamicGecco();


    /**
     * 构建
     */
    private void makeGeccoEngine(String[] urls) {
        GeccoEngine.create()
                .pipelineFactory(springPipelineFactory)
                //Gecco搜索的包路径，因为时使用的是DynamicGecco，所以不需要搜索，这里是要全局唯一的值就可以了
                //推荐使用当前的类名称
                .classpath(this.getClass().getName())
                //开始抓取的页面地址
                .start(urls)
                //开启几个爬虫线程，这个数量要与上面的start中的请求数量对应，要小于或者等于start请求的数量
                .thread(thread)
                //单个爬虫每次抓取完一个请求后的间隔时间
                .interval(interval)
                //配置全局cookie
                .cookies(cookies)
                .start();
    }


    /**
     * 启动抓取线程
     */
    @Override
    public void start(Map<String,Object> map) {
        init(map);
        makeDynamicGecco();
        makeGeccoEngine(makeHttpUrls());
    }

    /**
     * 初始化
    */
    @Override
    public void init(Map<String, Object> map) {
        //subClass toDo
    }
}
