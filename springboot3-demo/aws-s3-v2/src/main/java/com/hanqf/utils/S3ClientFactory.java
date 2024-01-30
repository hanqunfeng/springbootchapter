package com.hanqf.utils;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.transfer.s3.S3TransferManager;

import java.time.Duration;

import static software.amazon.awssdk.transfer.s3.SizeConstant.MB;

/**
 * <h1>S3ClientFactory</h1>
 * Created by hanqf on 2024/1/26 16:34.
 */


public class S3ClientFactory {

    private S3ClientFactory() {
    }

    public static final String BUCKET_NAME = "myBucket";
    private static final Region REGION = Region.of("myRegionName");
    private static final String AWS_ACCESS_KEY = "myAccessKey";
    private static final String AWS_SECRET_KEY = "mySecretKey";

    private static final int MAX_CONNECTIONS = 500;

    /**
     * 分段上传条件，每段必须>=5M，<=5G，这里要注意，分段上传要求除最后一段外，其余段的大小不能小于5M
     */
    public static final Long MIN_PART_SIZE = 5 * MB;

    public static final S3Client s3Client;
    public static final S3AsyncClient s3AsyncClient;
    public static final S3TransferManager transferManager;


    static {
        s3Client = S3Client.builder()
                .region(REGION)
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(AWS_ACCESS_KEY, AWS_SECRET_KEY)))
                .httpClientBuilder(ApacheHttpClient.builder()
                        .maxConnections(MAX_CONNECTIONS)
                        .connectionTimeout(Duration.ofSeconds(5))
                )
                .build();

        s3AsyncClient = S3AsyncClient.crtBuilder()
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(AWS_ACCESS_KEY, AWS_SECRET_KEY)))
                .region(REGION)
                .targetThroughputInGbps(20.0)
                .minimumPartSizeInBytes(MIN_PART_SIZE)
                .build();

        // 创建一个异步的S3客户端
//        s3AsyncClient = S3AsyncClient.builder()
//                .region(REGION) // 设置S3客户端的区域
//                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(AWS_ACCESS_KEY, AWS_SECRET_KEY))) // 设置S3客户端的凭证提供程序
//                .httpClientBuilder(AwsCrtAsyncHttpClient
//                        .builder()
////                        .connectionHealthConfiguration(builder -> builder
////                                .minimumThroughputInBps(32000L) // 设置连接健康检查的最小传输速率
////                                .minimumThroughputTimeout(Duration.ofSeconds(5)) // 设置连接健康检查的最小传输速率超时时间
////                        )
//                        .connectionTimeout(Duration.ofSeconds(5)) // 设置连接超时时间
//                        .maxConcurrency(MAX_CONNECTIONS)) // 设置最大并发连接数
//                .build(); // 构建S3客户端实例


        transferManager = S3TransferManager.builder()
                .s3Client(s3AsyncClient)
                .build();
    }
}
