package com.example.config;/**
 * Created by hanqf on 2020/3/10 22:13.
 */


import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author hanqf
 * @date 2020/3/10 22:13
 */
@Configuration
@AutoConfigureAfter(JpaConfigTwo.class)
public class MybatisConfigTwo {

    //很奇怪，这个bean必须配置到单独的配置类中，而且不能起名字，否则会导致datasource初始化失败
    @Bean
    public MapperScannerConfigurer mapperScannerConfigurerTwo() {
        MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
        //绑定datasorce2的sqlSessionFactory
        mapperScannerConfigurer.setSqlSessionFactoryBeanName("sqlSessionFactoryTwo");
        //扫描ds2目录来注册Mapper接口
        mapperScannerConfigurer.setBasePackage("com.example.mapper.two");
        //只有标注了该注解才会被扫描到
        mapperScannerConfigurer.setAnnotationClass(Mapper.class);
        return mapperScannerConfigurer;
    }

}
