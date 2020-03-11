package com.example.config;/**
 * Created by hanqf on 2020/3/10 22:13.
 */


import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author hanqf
 * @date 2020/3/10 22:13
 */
@Configuration
@AutoConfigureAfter(JpaConfigOne.class)
public class MybatisConfigOne {

    //很奇怪，这个bean必须配置到单独的配置类中，而且不能起名字，否则会导致datasource初始化失败
    @Bean
    public MapperScannerConfigurer mapperScannerConfigurerOne() {
        MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
        //绑定datasorce的sqlSessionFactory
        mapperScannerConfigurer.setSqlSessionFactoryBeanName("sqlSessionFactoryOne");
        //扫描one目录来注册Mapper接口
        mapperScannerConfigurer.setBasePackage("com.example.mapper.one");
        return mapperScannerConfigurer;
    }

}
