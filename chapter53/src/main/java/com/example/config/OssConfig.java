package com.example.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 阿里云OSS配置
 */
@Data
@Slf4j
@Configuration
@EnableConfigurationProperties(OssProperties.class)
public class OssConfig implements InitializingBean {

    @Autowired
    private OssProperties properties;

    // 方便直接获取
    public static String END_POINT;
    public static String ACCESS_KEY_ID;
    public static String ACCESS_KEY_SECRET;
    public static String BUCKET_NAME;
    public static Long POLICY_EXPIRE;
    public static Long MAX_SIZE;


    @Override
    public void afterPropertiesSet() {
        END_POINT = properties.getEndpoint();
        ACCESS_KEY_ID = properties.getAccessKeyId();
        ACCESS_KEY_SECRET = properties.getAccessKeySecret();
        BUCKET_NAME = properties.getBucketName();
        POLICY_EXPIRE = properties.getPolicyExpire();
        MAX_SIZE = properties.getMaxSize();
    }


}


