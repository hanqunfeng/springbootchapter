package com.hanqf.config;


import com.hanqf.support.jpa.BaseJpaRepositoryImpl;
import com.hanqf.support.jpa.CP_EnableJpaRepositories;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateSettings;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * one数据源配置
 */
@Slf4j
@Configuration
@CP_EnableJpaRepositories(basePackages = "${spring.jpa.repositories.packages}", repositoryBaseClass = BaseJpaRepositoryImpl.class, entityManagerFactoryRef = "entityManagerFactory", transactionManagerRef = "transactionManager")
public class JpaConfig {

    public static final String PERSISTENCE_UNIT = "persistenceUnit";

    @Value("${spring.jpa.entity.packages}")
    private String[] MODEL_PACKAGES;
    //注入数据源
    @Autowired
    @Qualifier("dataSource")
    private DataSource dataSource;

    //注入jpa配置实体
    @Autowired
    private JpaProperties jpaProperties;

    @Autowired
    private HibernateProperties hibernateProperties;

    @Value("${spring.jpa.hibernate.dialect}")
    private String dialect;// 获取对应的数据库方言


    //配置EntityManager实体
    @Primary
    @Bean(name = "entityManager")
    public EntityManager entityManager(EntityManagerFactoryBuilder builder) {
        return entityManagerFactory(builder).getObject().createEntityManager();
    }

    //配置EntityManager工厂实体
    @Primary
    @Bean(name = "entityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(dataSource)
                .properties(getVendorProperties())
                .packages(MODEL_PACKAGES) //设置应用creditDataSource的基础包名
                .persistenceUnit(PERSISTENCE_UNIT)
                .build();
    }

    private Map<String, Object> getVendorProperties() {
        Map<String, String> map = new HashMap<>();
        map.put("hibernate.dialect", dialect);// 设置对应的数据库方言
        jpaProperties.setProperties(map);
        return hibernateProperties.determineHibernateProperties(
                jpaProperties.getProperties(), new HibernateSettings());
    }

    //配置事务
    @Primary
    @Bean(name = "transactionManager")
    public PlatformTransactionManager transactionManager(EntityManagerFactoryBuilder builder) {
        log.info("开启注解事务");
        return new JpaTransactionManager(entityManagerFactory(builder).getObject());
    }

}
