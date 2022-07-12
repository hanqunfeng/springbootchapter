/*
 * ************************************************************
 * Copyright (c) 2020. Beijing CXZH-Tech Co.,Ltd.
 * ************************************************************
 * File:SpringPipelineFactory.java
 * 修改历史：(主要历史变动原因及说明)
 *  YYYY-MM-DD      |     Author    |    Change Description
 *  2020-04-11            hanqf           Created
 * ************************************************************
 */

package org.piaoyi.config;

import com.geccocrawler.gecco.pipeline.Pipeline;
import com.geccocrawler.gecco.pipeline.PipelineFactory;
import com.geccocrawler.gecco.spider.SpiderBean;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * 自定义PipelineFactory，用于加载自定义的Pipeline，以便根据需要处理抓取后的数据
 */
public class SpringPipelineFactory implements PipelineFactory, ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public Pipeline<? extends SpiderBean> getPipeline(String name) {
        try {
            //根据其在spring上下文中注册的bean名称进行匹配
            Object bean = applicationContext.getBean(name);
            if(bean instanceof Pipeline) {
                return (Pipeline<? extends SpiderBean>)bean;
            }
        } catch(NoSuchBeanDefinitionException ex) {
            System.out.println("no such pipeline : " + name);
        }
        return null;
    }

}
