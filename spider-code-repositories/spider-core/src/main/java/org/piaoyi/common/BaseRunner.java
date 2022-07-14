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


import com.geccocrawler.gecco.GeccoEngine;
import com.geccocrawler.gecco.request.HttpRequest;
import org.piaoyi.config.SpringPipelineFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

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

    //重试次数
    protected int retry = 5;

    /**
     * 全局cookies
     */
    protected String[] cookies;

    public void setRetry(int retry) {
        this.retry = retry;
    }


    protected void setCookies(String[] cookies) {
        this.cookies = cookies;
    }


    public void setThread(int thread) {
        this.thread = thread;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    /**
     * 启动方法
     * map : 初始化参数，为了扩展用，比如初始化cookie
     *
     * @author hanqf
     * @date2020/4/10 11:20
     * @since
     */
    public abstract void start(Map<String, Object> map);

    /**
     * 初始化
     */
    public void init(Map<String, Object> map) {
        if (map != null && map.containsKey("cookies")) {
            setCookies((String[]) map.get("cookies"));
        }
    }


    /**
     * 根据栏目关键字替换启动URL，并封装
     *
     * @return a
     * @throws
     * @author hanqf
     * @date 2020/4/10 11:19
     * @since
     */
    public abstract List<HttpRequest> makeHttpUrls(Map<String, Object> map);


    /**
     * <p>功能说明</p>
     *
     * @param classPath   gecoo扫描类路径
     * @param requestList 请求列表
     * @return void
     * @throws
     * @author hanqf
     * @date 2020/4/10 23:10
     * @since
     */
    protected void makeGeccoEngine(String classPath, List<HttpRequest> requestList) {
        GeccoEngine.create().pipelineFactory(springPipelineFactory)
                //Gecco搜索的包路径，这里要特别注意，所有需要gecco用到的class都必须在这个路径下才能被发现
                //推荐将实现类放到扫描包下，然后这里使用当前类包名称
                .classpath(classPath)
                //抓取失败的重试次数
                .retry(retry)
                //开始抓取的页面地址
                .start(requestList)
                //开启几个爬虫线程，这个数量要与上面的start中的请求数量对应，要小于或者等于start请求的数量
                .thread(thread)
                //单个爬虫每次抓取完一个请求后的间隔时间
                .interval(interval)
                //设置全局cookie
                .cookies(cookies)
                .start();

    }
}
