package com.example.config;/**
 * Created by hanqf on 2020/3/10 22:13.
 */


import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Configuration;

/**
 * @author hanqf
 * @date 2020/3/10 22:13
 */
@Configuration
@AutoConfigureAfter(JpaConfigOne.class)
//与bean的配置方式作用一样
//指定 sqlSessionTemplate ，将忽sqlSessionFactory巧的配置
//sqlSessionTemplateRef =”sqlSessionTemplate ”,
@MapperScan(basePackages = "com.example.mapper.one", sqlSessionFactoryRef = "sqlSessionFactoryOne", annotationClass = Mapper.class)
public class MybatisConfigOne {

    //很奇怪，这个bean必须配置到单独的配置类中，而且不能起名字，否则会导致datasource初始化失败
    //@Bean
    //public MapperScannerConfigurer mapperScannerConfigurerOne() {
    //    MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
    //    //绑定datasorce的sqlSessionFactory
    //    mapperScannerConfigurer.setSqlSessionFactoryBeanName("sqlSessionFactoryOne");
    //    //扫描one目录来注册Mapper接口
    //    mapperScannerConfigurer.setBasePackage("com.example.mapper.one");
    //    //只有标注了该注解才会被扫描到
    //    mapperScannerConfigurer.setAnnotationClass(Mapper.class);
    //    return mapperScannerConfigurer;
    //}

}
