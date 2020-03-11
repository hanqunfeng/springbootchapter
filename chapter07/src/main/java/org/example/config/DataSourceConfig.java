package org.example.config;/**
 * Created by hanqf on 2020/3/6 15:03.
 */


import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.icatch.jta.UserTransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jta.atomikos.AtomikosDataSourceBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.jta.JtaTransactionManager;

import javax.sql.DataSource;
import javax.sql.XADataSource;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

/**
 * @author hanqf
 * @date 2020/3/6 15:03
 */
@Configuration
public class DataSourceConfig {

    @Bean(name = "userTransaction")
    public UserTransaction userTransaction() throws Throwable {
        UserTransactionImp userTransactionImp = new UserTransactionImp();
        userTransactionImp.setTransactionTimeout(300);
        return userTransactionImp;
    }

    @Bean(name = "atomikosTransactionManager", initMethod = "init", destroyMethod = "close")
    public UserTransactionManager atomikosTransactionManager() throws Throwable {
        UserTransactionManager userTransactionManager = new UserTransactionManager();
        userTransactionManager.setForceShutdown(true);
        return userTransactionManager;
    }

    @Bean(name = "transactionManager")
    @DependsOn({ "userTransaction", "atomikosTransactionManager" })
    public PlatformTransactionManager transactionManager() throws Throwable {
        UserTransaction userTransaction = userTransaction();
        TransactionManager atomikosTransactionManager = atomikosTransactionManager();
        JtaTransactionManager jtaTransactionManager = new JtaTransactionManager(userTransaction, atomikosTransactionManager);
        jtaTransactionManager.setAllowCustomIsolationLevels(true);
        return jtaTransactionManager;
    }


    @Primary
    @Bean(name = "oneDataSource",initMethod = "init",destroyMethod = "close")
    @ConfigurationProperties("spring.jta.atomikos.datasource.one")
    public AtomikosDataSourceBean oneDataSource() {
        return new AtomikosDataSourceBean();
    }

    @Bean(name = "twoDataSource",initMethod = "init", destroyMethod = "close")
    @ConfigurationProperties("spring.jta.atomikos.datasource.two")
    public AtomikosDataSourceBean twoDataSource() {
        return new AtomikosDataSourceBean();
    }

    @Primary
    @Bean(name="oneJdbcTemplate")
    public JdbcTemplate oneJdbcTemplate(@Autowired @Qualifier("oneDataSource") DataSource dataSource){
        XADataSource xaDataSource = ((AtomikosDataSourceBean)dataSource).getXaDataSource();
        return new JdbcTemplate(dataSource);
    }

    @Bean(name="twoJdbcTemplate")
    public JdbcTemplate twoJdbcTemplate(@Autowired @Qualifier("twoDataSource") DataSource dataSource){
        return new JdbcTemplate(dataSource);
    }


}
