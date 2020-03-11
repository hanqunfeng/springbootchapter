package com.example.config;/**
 * Created by hanqf on 2020/3/10 22:13.
 */


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Configuration;

/**
 * @author hanqf
 * @date 2020/3/10 22:13
 */
@Configuration
@AutoConfigureAfter(JpaConfigTwo.class)
@MapperScan(basePackages = "com.example.mapper.two",sqlSessionFactoryRef = "sqlSessionFactoryTwo")
public class MybatisConfigTwo {

    //很奇怪，这个bean必须配置到单独的配置类中，而且不能起名字，否则会导致datasource初始化失败
    //@Bean
    //public MapperScannerConfigurer mapperScannerConfigurerTwo() {
    //    MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
    //    //绑定datasorce2的sqlSessionFactory
    //    mapperScannerConfigurer.setSqlSessionFactoryBeanName("sqlSessionFactoryTwo");
    //    //扫描ds2目录来注册Mapper接口
    //    mapperScannerConfigurer.setBasePackage("com.example.mapper.two");
    //
    //    return mapperScannerConfigurer;
    //}

}
