package com.cas.config;

import com.cas.security.CustomWebflowConfigurer;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.web.flow.CasWebflowConfigurer;
import org.apereo.cas.web.flow.CasWebflowExecutionPlan;
import org.apereo.cas.web.flow.CasWebflowExecutionPlanConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.webflow.definition.registry.FlowDefinitionRegistry;
import org.springframework.webflow.engine.builder.support.FlowBuilderServices;

/**
 * <p>更改CAS的Web流程的配置代理</p>
 * Created by hanqf on 2020/9/22 17:33.
 */

@Configuration("customerAuthWebflowConfiguration")
@EnableConfigurationProperties(CasConfigurationProperties.class)
public class CustomerAuthWebflowConfiguration implements CasWebflowExecutionPlanConfigurer {
    @Autowired
    private CasConfigurationProperties casProperties;

    @Autowired
    @Qualifier("loginFlowRegistry")
    private FlowDefinitionRegistry loginFlowDefinitionRegistry;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private FlowBuilderServices flowBuilderServices;

    @Bean
    public CasWebflowConfigurer customWebflowConfigurer() {
        // 实例化自定义的表单配置类
        final CustomWebflowConfigurer customWebflowConfigurer = new CustomWebflowConfigurer(flowBuilderServices, loginFlowDefinitionRegistry,
                applicationContext, casProperties);
        // 初始化
        customWebflowConfigurer.initialize();
        // 返回对象
        return customWebflowConfigurer;
    }

    @Override
    public void configureWebflowExecutionPlan(final CasWebflowExecutionPlan plan) {
        plan.registerWebflowConfigurer(customWebflowConfigurer());
    }
}
