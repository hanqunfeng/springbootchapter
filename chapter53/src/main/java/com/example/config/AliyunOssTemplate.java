package com.example.config;


import cn.hutool.core.io.FastByteArrayOutputStream;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import com.aliyun.oss.ClientConfiguration;
import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.aliyun.oss.model.*;
import com.example.support.OssResult;
import com.example.support.OssTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;

/**
 * 阿里云OSS工具类
 */
public class AliyunOssTemplate implements OssTemplate {

    /**
     * oss 工具客户端
     */
    private OSSClient ossClient;

    /**
     * 上传文件-自定义路径
     *
     * @param file     上传文件
     * @param fileName 上传至OSS的文件完整路径，例：cf/abc.png
     *                 上传至根目录，例：abc.png
     * @return
     */
    @Override
    public OssResult uploadFile(MultipartFile file, String fileName) {
        // 文件流
        InputStream inputStream;
        try {
            inputStream = file.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
            return new OssResult(false, null, null, e.getMessage());
        }
        // 获取文件类型
        String fileType = file.getContentType();

        // 上传文件
        return uploadInputStream(inputStream, fileType, fileName);
    }

    /**
     * 上传文件-自定义路径
     *
     * @param inputStream 上传文件流
     * @param fileType    文件类型，例：png
     * @param fileName    上传至OSS的文件完整路径，例：cf/abc.png
     *                    上传至根目录，例：abc.png
     * @return
     */
    @Override
    public OssResult uploadInputStream(InputStream inputStream, String fileType, String fileName) {
        if (inputStream == null) {
            return new OssResult(false, null, null, "文件不能为空");
        }
        if (StrUtil.isBlank(fileName)) {
            return new OssResult(false, null, null, "文件名不能为空");
        }
        // 上传文件最大值 MB->bytes
        long maxSize = AliyunOssConfig.ALIYUN_MAX_SIZE * 1024 * 1024;
        // 本次上传文件的大小
        long fileSize = 0;
        try {
            fileSize = inputStream.available();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (fileSize <= 0 || fileSize > maxSize) {
            return new OssResult(false, null, null, "文件超过最大限制");
        }

        // 上传文件
        return putFile(inputStream, fileType, fileName);
    }

    /**
     * 上传文件
     *
     * @param inputStream
     * @param fileType
     * @param fileName
     * @return
     */
    private OssResult putFile(InputStream inputStream, String fileType, String fileName) {
        try {
            // 创建上传Object的Metadata
            ObjectMetadata meta = new ObjectMetadata();
            // 设置上传内容类型
            meta.setContentType(fileType);
            //被下载时网页的缓存行为
            meta.setCacheControl("no-cache");
            //创建上传请求
            PutObjectRequest request = new PutObjectRequest(AliyunOssConfig.ALIYUN_BUCKET_NAME, fileName, inputStream, meta);
            //上传文件
            ossClient.putObject(request);

            //获取上传成功的文件地址
            return new OssResult(true, fileName, getOssUrl(fileName), "上传成功");
        } catch (OSSException | ClientException e) {
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
        // 生成过期时间
        long expireEndTime = System.currentTimeMillis() + AliyunOssConfig.ALIYUN_POLICY_EXPIRE * 1000;
        Date expiration = new Date(expireEndTime);// 生成URL
        GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(AliyunOssConfig.ALIYUN_BUCKET_NAME, fileName);
        generatePresignedUrlRequest.setExpiration(expiration);
        URL url = ossClient.generatePresignedUrl(generatePresignedUrlRequest);
        return url.toString();
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
        ossClient.getObject(new GetObjectRequest(AliyunOssConfig.ALIYUN_BUCKET_NAME, fileName), new File(localFileName));
        return true;
    }

    /**
     * 通过文件名获取文件流
     *
     * @param fileName 要下载的文件名（OSS服务器上的）
     *                 例如：4DB049D0604047989183CB68D76E969D.jpg
     */
    @Override
    public InputStream getInputStream(String fileName) {
        // 下载OSS文件到本地文件。如果指定的本地文件存在会覆盖，不存在则新建。
        return ossClient.getObject(new GetObjectRequest(AliyunOssConfig.ALIYUN_BUCKET_NAME, fileName)).getObjectContent();
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
            if (AliyunOssConfig.ALIYUN_BUCKET_NAME == null || fileName == null) {
                return false;
            }
            GenericRequest request = new DeleteObjectsRequest(AliyunOssConfig.ALIYUN_BUCKET_NAME).withKey(fileName);
            ossClient.deleteObject(request);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    /**
     * 初始化 oss 客户端
     */
    @PostConstruct
    public void init() {
        ossClient = new OSSClient(AliyunOssConfig.ALIYUN_END_POINT,
                new DefaultCredentialProvider(AliyunOssConfig.ALIYUN_ACCESS_KEY_ID, AliyunOssConfig.ALIYUN_ACCESS_KEY_SECRET),
                new ClientConfiguration());
    }

    /**
     * 上传文件-自定义路径
     *
     * @param file     上传文件
     * @param fileName 上传至OSS的文件完整路径，例：cf/abc.png
     *                 上传至根目录，例：abc.png
     * @return
     */
    @Override
    public OssResult uploadFile(File file, String fileName) {
        // 文件流
        InputStream inputStream = FileUtil.getInputStream(file);
        // 获取文件类型
        String fileType = FileUtil.getType(file);

        // 上传文件
        return uploadInputStream(inputStream, fileType, fileName);
    }

}


