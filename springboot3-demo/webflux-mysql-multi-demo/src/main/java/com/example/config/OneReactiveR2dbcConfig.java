package com.example.config;

import com.example.r2dbc.SimpleBaseR2dbcRepository;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import io.r2dbc.spi.Option;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.r2dbc.connection.R2dbcTransactionManager;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.util.StringUtils;

import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * <h1></h1>
 * Created by hanqf on 2023/8/31 15:19.
 */

@Slf4j
@Configuration
// 这里必须指定 basePackages，否则 repositoryBaseClass 指定的实现类不能被关联到 各个 Repository 中
@EnableR2dbcRepositories(basePackages = "com.example.mysql.one.repository", repositoryBaseClass = SimpleBaseR2dbcRepository.class, entityOperationsRef = "oneR2dbcEntityTemplate")
public class OneReactiveR2dbcConfig {

    @Primary
    @Bean("oneR2dbcProperties")
    @ConfigurationProperties(prefix = "spring.r2dbc.one")
    public R2dbcProperties oneR2dbcProperties() {
        return new R2dbcProperties();
    }

    @Primary
    @Bean("oneR2dbcEntityTemplate")
    public R2dbcEntityTemplate oneR2dbcEntityTemplate() {
        log.info("oneR2dbcEntityTemplate");
        return new R2dbcEntityTemplate(oneConnectionFactory());
    }

    @Primary
    @Bean("oneConnectionFactory")
    public ConnectionFactory oneConnectionFactory() {
        log.info("oneConnectionFactory");
        R2dbcProperties r2dbcProperties = oneR2dbcProperties();
        ConnectionFactoryOptions urlOptions = ConnectionFactoryOptions.parse(r2dbcProperties.getUrl());
        ConnectionFactoryOptions.Builder optionsBuilder = urlOptions.mutate();

        configureIf(optionsBuilder, urlOptions, ConnectionFactoryOptions.USER, r2dbcProperties::getUsername,
                StringUtils::hasText);
        configureIf(optionsBuilder, urlOptions, ConnectionFactoryOptions.PASSWORD, r2dbcProperties::getPassword,
                StringUtils::hasText);
        configureIf(optionsBuilder, urlOptions, ConnectionFactoryOptions.DATABASE,
                () -> determineDatabaseName(r2dbcProperties), StringUtils::hasText);

        if (r2dbcProperties.getProperties() != null) {
            r2dbcProperties.getProperties()
                    .forEach((key, value) -> optionsBuilder.option(Option.valueOf(key), value));
        }
        final ConnectionFactoryOptions options = optionsBuilder.build();
        return ConnectionFactories.get(options);
    }

    @Primary
    @Bean("oneR2dbcTransactionManager")
    public ReactiveTransactionManager oneR2dbcTransactionManager() {
        return new R2dbcTransactionManager(oneConnectionFactory());
    }

    @Primary
    @Bean("oneTransactionalOperator")
    public TransactionalOperator oneTransactionalOperator() {
        DefaultTransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
        transactionDefinition.setName("oneTransactionalOperator");
        // 这是默认行为
        transactionDefinition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        return TransactionalOperator.create(oneR2dbcTransactionManager(), transactionDefinition);
    }


    private <T extends CharSequence> void configureIf(ConnectionFactoryOptions.Builder optionsBuilder,
                                                      ConnectionFactoryOptions originalOptions, Option<T> option, Supplier<T> valueSupplier,
                                                      Predicate<T> setIf) {
        if (originalOptions.hasOption(option)) {
            return;
        }
        T value = valueSupplier.get();
        if (setIf.test(value)) {
            optionsBuilder.option(option, value);
        }
    }

    private String determineDatabaseName(R2dbcProperties properties) {
        if (properties.isGenerateUniqueName()) {
            return properties.determineUniqueName();
        }
        if (StringUtils.hasLength(properties.getName())) {
            return properties.getName();
        }
        return null;
    }
}
