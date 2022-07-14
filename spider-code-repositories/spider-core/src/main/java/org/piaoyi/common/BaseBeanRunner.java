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

import com.geccocrawler.gecco.GeccoEngine;

import java.util.Map;

/**
 * @author hanqf
 * @date 2020/4/5 16:04
 */
public abstract class BaseBeanRunner extends BaseRunner {

    /**
     * 根据栏目关键字替换启动URL，并封装
     *
     * @return a
     * @throws
     * @author hanqf
     * @date 2020/4/10 11:19
     * @since
     */
    public abstract String[] makeHttpUrls();


    /**
     * <p>功能说明</p>
     *
     * @param urls
     * @param thread
     * @param interval
     * @return void
     * @throws
     * @author hanqf
     * @date 2020/4/10 23:10
     * @since
     */
    private void makeGeccoEngine(String[] urls) {

        GeccoEngine.create()
                .pipelineFactory(springPipelineFactory)
                //Gecco搜索的包路径，这里要特别注意，所有需要gecco用到的class都必须在这个路径下才能被发现
                //推荐将实现类放到扫描包下，然后这里使用当前类包名称
                .classpath(this.getClass().getPackage().getName())
                //抓取失败的重试次数
                .retry(10)
                //开始抓取的页面地址
                .start(urls)
                //开启几个爬虫线程，这个数量要与上面的start中的请求数量对应，要小于或者等于start请求的数量
                .thread(thread)
                //单个爬虫每次抓取完一个请求后的间隔时间
                .interval(interval)
                //设置全局cookie
                .cookies(cookies)
                .start();

    }


    /**
     * 启动抓取线程
     */
    @Override
    public void start(Map<String,Object> map) {
        init(map);
        makeGeccoEngine(makeHttpUrls());
    }

    @Override
    public void init(Map<String,Object> map) {
        //subClass toDo
    }
}
