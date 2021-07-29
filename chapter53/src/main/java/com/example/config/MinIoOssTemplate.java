package com.example.config;


import cn.hutool.core.io.FastByteArrayOutputStream;
import cn.hutool.core.io.IoUtil;
import com.example.support.OssResult;
import com.example.support.OssTemplate;
import io.minio.*;
import io.minio.http.Method;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

/**
 * MinIO OSS工具类
 */
@Component
public class MinIoOssTemplate implements OssTemplate {

    /**
     * oss 工具客户端
     */
    private MinioClient minioClient;



    /**
     * 上传文件
     *
     * @param inputStream
     * @param fileType
     * @param fileName
     * @return
     */
    @Override
    public OssResult putFile(InputStream inputStream, String fileType, String fileName) {
        try {
            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                    .bucket(OssConfig.BUCKET_NAME)
                    .object(fileName)
                    .contentType(fileType)
                    .stream(inputStream, -1, OssConfig.MAX_SIZE * 1024 * 1024)
                    .build();
            //上传文件
            minioClient.putObject(putObjectArgs);

            //获取上传成功的文件地址
            return new OssResult(true, fileName, getOssUrl(fileName), "上传成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new OssResult(false, fileName, null, e.getMessage());
        }
    }

    /**
     * 根据文件名生成文件的访问地址（带过期时间）
     *
     * @param fileName
     * @return
     */
    @Override
    public String getOssUrl(String fileName) {
        try {
            GetPresignedObjectUrlArgs getPresignedObjectUrlArgs = GetPresignedObjectUrlArgs.builder()
                    .bucket(OssConfig.BUCKET_NAME)
                    .object(fileName)
                    .method(Method.GET)
                    .expiry(OssConfig.POLICY_EXPIRE.intValue(), TimeUnit.SECONDS)
                    .build();

            return minioClient.getPresignedObjectUrl(getPresignedObjectUrlArgs);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 通过文件名下载文件
     *
     * @param fileName      要下载的文件名（OSS服务器上的）
     *                      例如：4DB049D0604047989183CB68D76E969D.jpg
     * @param localFileName 本地要创建的文件名（下载到本地的）
     *                      例如：C:\Users\Administrator\Desktop\test.jpg
     */
    @Override
    public boolean downloadFile(String fileName, String localFileName) {
        // 下载OSS文件到指定目录。如果指定的本地文件存在会覆盖，不存在则新建。
        try {
            DownloadObjectArgs downloadObjectArgs = DownloadObjectArgs.builder()
                    .bucket(OssConfig.BUCKET_NAME)
                    .object(fileName)
                    .filename(localFileName)
                    .build();
            minioClient.downloadObject(downloadObjectArgs);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 通过文件名获取文件流
     *
     * @param fileName 要下载的文件名（OSS服务器上的）
     *                 例如：4DB049D0604047989183CB68D76E969D.jpg
     */
    @Override
    public InputStream getInputStream(String fileName) {
        // 下载OSS文件到流。
        try {
            GetObjectArgs getObjectArgs = GetObjectArgs.builder()
                    .bucket(OssConfig.BUCKET_NAME)
                    .object(fileName)
                    .build();
            GetObjectResponse response = minioClient.getObject(getObjectArgs);
            return response;

        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }

    /**
     * 通过文件名获取byte[]
     *
     * @param fileName 要下载的文件名（OSS服务器上的）
     *                 例如：4DB049D0604047989183CB68D76E969D.jpg
     */
    @Override
    public byte[] getBytes(String fileName) {
        InputStream inputStream = getInputStream(fileName);
        FastByteArrayOutputStream fastByteArrayOutputStream = IoUtil.read(inputStream);
        return fastByteArrayOutputStream.toByteArray();
    }

    /**
     * 根据文件名删除文件
     *
     * @param fileName 需要删除的文件名
     * @return boolean 是否删除成功
     * 例如：4DB049D0604047989183CB68D76E969D.jpg
     */
    @Override
    public boolean deleteFile(String fileName) {
        try {
            if (OssConfig.BUCKET_NAME == null || fileName == null) {
                return false;
            }
            RemoveObjectArgs removeObjectArgs = RemoveObjectArgs.builder()
                    .bucket(OssConfig.BUCKET_NAME)
                    .object(fileName)
                    .build();

            minioClient.removeObject(removeObjectArgs);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }


    /**
     * 初始化 oss 客户端
     */
    @PostConstruct
    public void init() {
        minioClient = MinioClient.builder()
                .endpoint(OssConfig.END_POINT)
                .credentials(OssConfig.ACCESS_KEY_ID, OssConfig.ACCESS_KEY_SECRET)
                .build();
    }


}


