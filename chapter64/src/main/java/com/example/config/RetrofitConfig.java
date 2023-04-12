package com.example.config;

import com.github.lianjiatech.retrofit.spring.boot.core.RetrofitScan;
import org.springframework.context.annotation.Configuration;

/**
 * <h1></h1>
 * Created by hanqf on 2023/4/11 17:33.
 */


@Configuration
@RetrofitScan(value = {"com.example.api"})
public class RetrofitConfig {

}
