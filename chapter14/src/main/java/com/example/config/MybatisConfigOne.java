package com.example.config;/**
 * Created by hanqf on 2020/3/10 22:13.
 */


import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Configuration;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @author hanqf
 * @date 2020/3/10 22:13
 */
@Configuration
@AutoConfigureAfter(JpaConfigOne.class)
//注意这里是tk.mybatis.spring.annotation.MapperScan
@MapperScan(basePackages = "com.example.mapper.one",sqlSessionFactoryRef = "sqlSessionFactoryOne",properties = {
        "notEmpty=false",
        "mappers=com.example.mapper.BaseMapper",
        "IDENTITY=MYSQL"})
public class MybatisConfigOne {

    //很奇怪，这个bean必须配置到单独的配置类中，而且不能起名字，否则会导致datasource初始化失败
    //@Bean
    //public MapperScannerConfigurer mapperScannerConfigurerOne() {
    //    MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
    //    //绑定datasorce的sqlSessionFactory
    //    mapperScannerConfigurer.setSqlSessionFactoryBeanName("sqlSessionFactoryOne");
    //    //扫描one目录来注册Mapper接口
    //    mapperScannerConfigurer.setBasePackage("com.example.mapper.one");
    //
    //    //初始化扫描器的相关配置，这里我们要创建一个Mapper的父类
    //    Properties properties = new Properties();
    //    properties.setProperty("mappers", "com.example.mapper.BaseMapper");
    //    properties.setProperty("notEmpty", "false");
    //    properties.setProperty("IDENTITY", "MYSQL");
    //
    //    mapperScannerConfigurer.setProperties(properties);
    //
    //    return mapperScannerConfigurer;
    //}

}
