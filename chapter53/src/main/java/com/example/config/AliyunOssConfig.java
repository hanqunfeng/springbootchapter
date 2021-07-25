package com.example.config;

import com.example.support.OssTemplate;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 阿里云OSS配置
 */
@Data
@Slf4j
@Configuration
@ConfigurationProperties(prefix = "aliyun.oss")
public class AliyunOssConfig implements InitializingBean {

    // 方便直接获取
    public static String ALIYUN_END_POINT;
    public static String ALIYUN_ACCESS_KEY_ID;
    public static String ALIYUN_ACCESS_KEY_SECRET;
    public static String ALIYUN_BUCKET_NAME;
    public static Long ALIYUN_POLICY_EXPIRE;
    public static Long ALIYUN_MAX_SIZE;
    /**
     * 阿里云 oss 站点
     */
    private String endpoint;
    /**
     * 阿里云 oss 公钥
     */
    private String accessKeyId;
    /**
     * 阿里云 oss 私钥
     */
    private String accessKeySecret;
    /**
     * 阿里云 oss 文件根目录
     */
    private String bucketName;
    /**
     * url有效期(S)
     */
    private Long policyExpire;
    /**
     * 上传文件大小(M)
     */
    private Long maxSize;

    @Override
    public void afterPropertiesSet() {
        ALIYUN_END_POINT = endpoint;
        ALIYUN_ACCESS_KEY_ID = accessKeyId;
        ALIYUN_ACCESS_KEY_SECRET = accessKeySecret;
        ALIYUN_BUCKET_NAME = bucketName;
        ALIYUN_POLICY_EXPIRE = policyExpire;
        ALIYUN_MAX_SIZE = maxSize;
    }

    @Bean
    public OssTemplate ossTemplate(){
        log.info("AliyunOssTemplate");
        return new AliyunOssTemplate();
    }

}


