package com.example.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * <h1>配置属性</h1>
 * Created by hanqf on 2021/7/29 22:36.
 */
@Data
@ConfigurationProperties(prefix = "oss")
public class OssProperties {

    /**
     * oss 站点
     */
    private String endpoint;
    /**
     * oss 公钥
     */
    private String accessKeyId;
    /**
     * oss 私钥
     */
    private String accessKeySecret;
    /**
     * oss 文件根目录
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
}
