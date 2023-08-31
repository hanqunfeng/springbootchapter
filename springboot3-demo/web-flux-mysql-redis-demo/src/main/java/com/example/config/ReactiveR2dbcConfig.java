package com.example.config;

import com.example.r2dbc.SimpleBaseR2dbcRepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

/**
 * <h1></h1>
 * Created by hanqf on 2023/8/31 15:19.
 */

@Configuration
// 这里必须指定 basePackages，否则 repositoryBaseClass 指定的实现类不能被关联到 各个 Repository 中
@EnableR2dbcRepositories(basePackages = "com.example.mysql", repositoryBaseClass = SimpleBaseR2dbcRepository.class)
public class ReactiveR2dbcConfig {

}
