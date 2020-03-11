package com.example.config;/**
 * Created by hanqf on 2020/3/8 17:12.
 */


import com.github.pagehelper.PageInterceptor;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.Properties;

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
    public SqlSessionFactory sqlSessionFactoryBeanTwo() {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(twoDataSource);

        //实体别名查找路径
        bean.setTypeAliasesPackage("com.example.model.two");

        //分页插件设置，需要关闭PageHelperAutoConfiguration
        PageInterceptor pageInterceptor = new PageInterceptor();
        Properties properties = new Properties();
        properties.setProperty("reasonable", "true");
        properties.setProperty("supportMethodsArguments", "true");
        properties.setProperty("helper-dialect", "mysql");
        properties.setProperty("params", "count=countSql");
        pageInterceptor.setProperties(properties);
        //添加分页插件
        bean.setPlugins(new Interceptor[]{pageInterceptor});


        try {
            return bean.getObject();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Bean(name = "sqlSessionTemplateTwo")
    public SqlSessionTemplate sqlSessionTemplateTwo() {
        return new SqlSessionTemplate(sqlSessionFactoryBeanTwo());
    }


}
