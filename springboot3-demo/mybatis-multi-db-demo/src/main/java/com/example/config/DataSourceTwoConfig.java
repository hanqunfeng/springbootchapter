package com.example.config;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import com.github.pagehelper.PageInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * <h1>two db</h1>
 * Created by hanqf on 2023/8/16 17:30.
 */

@Slf4j
@Configuration
@MapperScan(basePackages = "com.example.dao.two", sqlSessionTemplateRef = "twoSqlSessionTemplate")
public class DataSourceTwoConfig {

    @Bean(name = "twoDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.druid.two")
    public DataSource twoDataSource() {
        log.info("spring.datasource.druid.two init");
        return DruidDataSourceBuilder.create().build();
    }

    @Bean(name = "twoSqlSessionFactory")
    public SqlSessionFactory twoSqlSessionFactory(@Qualifier("twoDataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);

        //实体别名查找路径 @Alias("oneUser")
        bean.setTypeAliasesPackage("com.example.model.two");

        ////分页插件设置，需要关闭PageHelperAutoConfiguration
        PageInterceptor pageInterceptor = new PageInterceptor();
        Properties properties = new Properties();
        properties.setProperty("reasonable", "true");
        properties.setProperty("supportMethodsArguments", "true");
        properties.setProperty("helper-dialect", "mysql");
        properties.setProperty("params", "count=countSql");
        pageInterceptor.setProperties(properties);

        //添加分页插件
        bean.setPlugins(pageInterceptor);

        //添加Mapper配置文件的目录
        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mappers/two/*.xml"));

        return bean.getObject();
    }

    @Bean(name = "twoTransactionManager")
    public DataSourceTransactionManager testTransactionManager(@Qualifier("twoDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name = "twoSqlSessionTemplate")
    public SqlSessionTemplate testSqlSessionTemplate(@Qualifier("twoSqlSessionFactory") SqlSessionFactory sqlSessionFactory) throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
