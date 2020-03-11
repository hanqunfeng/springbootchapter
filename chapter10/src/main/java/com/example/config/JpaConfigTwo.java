package com.example.config;/**
 * Created by hanqf on 2020/3/8 17:12.
 */


import com.example.util.AtomikosJtaPlatform;
import com.example.util.JpaProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;
import java.util.HashMap;

/**
 * @author hanqf
 * @date 2020/3/8 17:12
 */
@Configuration
@AutoConfigureAfter(DataSourceConfig.class)
//定义JPA接口扫描包路径
@EnableJpaRepositories(basePackages = "com.example.dao.two", entityManagerFactoryRef = "twoEntityManagerFactory", transactionManagerRef = "transactionManager")
public class JpaConfigTwo {

    //注入数据源
    @Autowired
    @Qualifier("twoDataSource")
    private DataSource twoDataSource;

    @Autowired
    private JpaProperties jpaProperties;



    @Bean(name = "twoEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean twoEntityManagerFactory() {
        HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
        //显示sql
        adapter.setShowSql(jpaProperties.isShowSql());
        //设置数据库类型
        adapter.setDatabase(Database.MYSQL);
        //adapter.setDatabasePlatform("org.hibernate.dialect.MySQL5Dialect");
        //自动生成/更新表
        adapter.setGenerateDdl(jpaProperties.isGenerateDdl());

        HashMap<String, Object> properties = new HashMap<String, Object>();
        properties.put("hibernate.transaction.jta.platform", AtomikosJtaPlatform.class.getName());
        properties.put("javax.persistence.transactionType", "JTA");
        properties.put("hibernate.format_sql", jpaProperties.getFormatSql());

        LocalContainerEntityManagerFactoryBean entityManager = new LocalContainerEntityManagerFactoryBean();
        entityManager.setPersistenceUnitName("twoPersistenceUnit");
        entityManager.setDataSource(twoDataSource);
        entityManager.setJpaVendorAdapter(adapter);
        entityManager.setPackagesToScan("com.example.model.two");// entity package
        entityManager.setJpaPropertyMap(properties);


        return entityManager;
    }
}
