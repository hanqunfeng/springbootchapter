package com.example.config;/**
 * Created by hanqf on 2020/3/8 17:12.
 */


import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.plugins.pagination.optimize.JsqlParserCountOptimize;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import org.apache.ibatis.plugin.Interceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * @author hanqf
 * @date 2020/3/8 17:12
 */
@Configuration
@AutoConfigureAfter(DataSourceConfig.class)
public class JpaConfigTwo {

    //注入数据源
    @Autowired
    @Qualifier("twoDataSource")
    private DataSource twoDataSource;


    //基于注解式Mapper
    @Bean(name = "sqlSessionFactoryTwo")
    public MybatisSqlSessionFactoryBean sqlSessionFactoryBeanTwo() {
        MybatisSqlSessionFactoryBean bean = new MybatisSqlSessionFactoryBean();
        bean.setDataSource(twoDataSource);

        //实体别名查找路径
        bean.setTypeAliasesPackage("com.example.model.two");

        ////分页插件设置
        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
        // 设置请求的页面大于最大页后操作， true调回到首页，false 继续请求  默认false
        // paginationInterceptor.setOverflow(false);
        // 设置最大单页限制数量，默认 500 条，-1 不受限制
        // paginationInterceptor.setLimit(500);
        // 开启 count 的 join 优化,只针对部分 left join
        paginationInterceptor.setCountSqlParser(new JsqlParserCountOptimize(true));
        //添加分页插件
        bean.setPlugins(new Interceptor[]{paginationInterceptor});


        try {
            return bean;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }



}
