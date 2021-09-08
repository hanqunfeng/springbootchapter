package com.example.config;

import com.example.jpa.JpaDto;
import com.example.jpa.JpaUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.support.DefaultConversionService;

import javax.annotation.PostConstruct;
import java.util.Map;

/**
 * <h1></h1>
 * Created by hanqf on 2021/9/6 11:15.
 */
@Slf4j
@AutoConfigureAfter(value = JpaConfig.class)
@Configuration
public class JpaDtoConfig {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private JpaUtil jpaUtil;


    /**
     * 初始化注入@JpaDto对应的Converter
     */
    @PostConstruct
    public void init() {
        Map<String, Object> map = applicationContext.getBeansWithAnnotation(JpaDto.class);
        for (Object o : map.values()) {
            Class c = o.getClass();
            log.info("Jpa添加Converter,class={}", c.getName());
            DefaultConversionService defaultConversionService = ((DefaultConversionService) DefaultConversionService.getSharedInstance());
            defaultConversionService.addConverter(Map.class, c, m -> {
                try {
                    //通过json作为中间媒介
                    return jpaUtil.mapToObject(m, c, false);
                } catch (Exception e) {
                    throw new FatalBeanException("Jpa结果转换出错,class=" + c.getName(), e);
                }
            });
        }
    }


}
