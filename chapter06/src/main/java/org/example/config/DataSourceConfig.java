package org.example.config;/**
 * Created by hanqf on 2020/3/5 16:09.
 */


import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.filter.logging.Slf4jLogFilter;
import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author hanqf
 * @date 2020/3/5 16:09
 */
@Configuration
//加载资源文件
@PropertySource({"classpath:db.properties"})
//开启spring-boot对配置依赖的支持，如@ConfigurationProperties
@EnableConfigurationProperties
public class DataSourceConfig {
    private static final Logger logger = LoggerFactory.getLogger(DataSourceConfig.class);
    /*
     * 绑定资源属性
     */
    @Value("${jdbc.driver}")
    String driverClass;
    @Value("${jdbc.url}")
    String url;
    @Value("${jdbc.username}")
    String userName;
    @Value("${jdbc.password}")
    String passWord;

    @Bean(name = "dataSource")
    public DataSource dataSource() {
        logger.info("DataSource");
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driverClass);
        dataSource.setUrl(url);
        dataSource.setUsername(userName);
        dataSource.setPassword(passWord);
        return dataSource;
    }

    @Autowired
    private DruidConfigProperties druidConfigProperties;

    @Bean(name = "druidDataSource",initMethod = "init",destroyMethod = "close")
    //相同类型要设置主类，以便@Autowired注入时可以找到唯一
    @Primary
    public DruidDataSource druidDataSource(){
        logger.info("druidDataSource");
        //详细属性及过滤器配置查看：https://blog.csdn.net/poiuyppp/article/details/88372293
        DruidDataSource druidDataSource = new DruidDataSource();

        druidDataSource.setDriverClassName(driverClass);
        druidDataSource.setUrl(url);
        druidDataSource.setUsername(userName);
        druidDataSource.setPassword(passWord);

        System.out.println(druidConfigProperties);
        BeanUtils.copyProperties(druidConfigProperties,druidDataSource);
        //druidDataSource.setMaxActive(druidConfigProperties.getMaxActive());
        //druidDataSource.setInitialSize(druidConfigProperties.getInitialSize());
        //druidDataSource.setMinIdle(druidConfigProperties.getMinIdle());
        //druidDataSource.setMaxWait(druidConfigProperties.getMaxWait());
        //druidDataSource.setTimeBetweenEvictionRunsMillis(druidConfigProperties.getTimeBetweenEvictionRunsMillis());
        //druidDataSource.setMinEvictableIdleTimeMillis(druidConfigProperties.getMinEvictableIdleTimeMillis());
        //druidDataSource.setTestWhileIdle(druidConfigProperties.isTestWhileIdle());
        //druidDataSource.setTestOnBorrow(druidConfigProperties.isTestOnBorrow());
        //druidDataSource.setTestOnReturn(druidConfigProperties.isTestOnReturn());
        //druidDataSource.setPoolPreparedStatements(druidConfigProperties.isPoolPreparedStatements());
        //druidDataSource.setMaxOpenPreparedStatements(druidConfigProperties.getMaxOpenPreparedStatements());

        System.out.println(druidDataSource.getValidationQuery());

        //try {
        //    druidDataSource.setFilters("stat,slf4j,wall");
        //} catch (SQLException e) {
        //    e.printStackTrace();
        //}

        //监控过滤器
        StatFilter statFilter = new StatFilter();
        //SQL合并
        statFilter.setMergeSql(true);
        //慢SQL记录
        statFilter.setLogSlowSql(true);
        //超过5秒则认为时慢sql
        statFilter.setSlowSqlMillis(5000);


        //日志过滤器
        Slf4jLogFilter slf4jLogFilter = new Slf4jLogFilter();
        slf4jLogFilter.setConnectionLogEnabled(true);
        slf4jLogFilter.setConnectionCloseAfterLogEnabled(true);
        slf4jLogFilter.setConnectionCommitAfterLogEnabled(true);
        slf4jLogFilter.setConnectionConnectAfterLogEnabled(true);
        slf4jLogFilter.setConnectionConnectBeforeLogEnabled(true);
        slf4jLogFilter.setConnectionLogErrorEnabled(true);
        slf4jLogFilter.setDataSourceLogEnabled(true);
        slf4jLogFilter.setResultSetLogEnabled(true);
        slf4jLogFilter.setStatementLogEnabled(true);
        slf4jLogFilter.setStatementSqlPrettyFormat(true);

        WallFilter wallFilter = new WallFilter();

        wallFilter.setLogViolation(true);
        wallFilter.setThrowException(true);

        WallConfig wallConfig = new WallConfig();
        wallConfig.setAlterTableAllow(false);
        wallConfig.setTruncateAllow(false);
        wallConfig.setDropTableAllow(false);
        wallConfig.setNoneBaseStatementAllow(false);
        wallConfig.setUpdateWhereNoneCheck(true);
        wallConfig.setSelectIntoOutfileAllow(false);
        wallConfig.setMetadataAllow(true);
        wallConfig.setMultiStatementAllow(true);

        wallFilter.setConfig(wallConfig);

        List<Filter> list = new ArrayList<>();
        list.add(statFilter);
        list.add(slf4jLogFilter);
        list.add(wallFilter);

        druidDataSource.setProxyFilters(list);

        return druidDataSource;

    }

    @Bean(name="jdbcTemplate")
    public JdbcTemplate jdbcTemplate(@Autowired DataSource dataSource){
        return new JdbcTemplate(dataSource);
    }
}

