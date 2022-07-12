/*
 * ************************************************************
 * Copyright (c) 2020. Beijing CXZH-Tech Co.,Ltd.
 * ************************************************************
 * File:GeccoConfig.java
 * 修改历史：(主要历史变动原因及说明)
 *  YYYY-MM-DD      |     Author    |    Change Description
 *  2020-04-11            hanqf           Created
 * ************************************************************
 */

package org.piaoyi.config;

import com.geccocrawler.gecco.pipeline.ConsolePipeline;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ${DESCRIPTION}
 * Created by hanqf on 2018/11/28 16:23.
 */

@Configuration
public class GeccoConfig {
    @Bean
    public SpringPipelineFactory springPipelineFactory() {
        return new SpringPipelineFactory();
    }

    //因为自定义了PipelineFactory，所以这里需要声明ConsolePipeline，否则无法使用
    @Bean(name="consolePipeline")
    public ConsolePipeline consolePipeline() {
        return new ConsolePipeline();
    }
}
