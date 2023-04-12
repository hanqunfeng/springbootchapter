package com.example.config;

import com.example.api.HttpApi;
import com.example.callAdapterFactory.ResponseCallAdapterFactory;
import com.example.converterFactory.BasicTypeConverterFactory;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.util.concurrent.TimeUnit;

/**
 * <h1></h1>
 * Created by hanqf on 2023/4/11 17:33.
 */


@Configuration
public class RetrofitConfig {

    @Value("${openai-api-web.baseUrl}")
    private String BASE_URL;

    @Bean
    public ObjectMapper defaultObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        return mapper;
    }

    @Bean
    public OkHttpClient defaultClient() {
        return new OkHttpClient.Builder()
                .connectionPool(new ConnectionPool(5, 1, TimeUnit.SECONDS))
                .readTimeout(5, TimeUnit.MINUTES)
                .build();
    }

    @Bean
    public Retrofit defaultRetrofit(OkHttpClient client, ObjectMapper mapper) {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                //基础类型转换器，这个要排在第一个
                .addConverterFactory(BasicTypeConverterFactory.INSTANCE)
                .addConverterFactory(JacksonConverterFactory.create(mapper))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                //适配Response类型的返回值
                .addCallAdapterFactory(ResponseCallAdapterFactory.INSTANCE)
                .build();
    }

    @Bean
    public HttpApi buildApi(Retrofit retrofit) {
        return retrofit.create(HttpApi.class);
    }
}
