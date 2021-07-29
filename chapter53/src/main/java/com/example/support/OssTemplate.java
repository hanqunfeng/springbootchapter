package com.example.support;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.example.config.OssConfig;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * <h1>OSS文件操作工具接口</h1>
 * Created by hanqf on 2021/7/24 21:33.
 */


public interface OssTemplate {

    /**
     * 上传文件-自定义路径
     *
     * @param file     上传文件
     * @param fileName 上传至OSS的文件完整路径，例：cf/abc.png
     *                 上传至根目录，例：abc.png
     * @return
     */
    default public OssResult uploadFile(MultipartFile file, String fileName) {
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
    default public OssResult uploadInputStream(InputStream inputStream, String fileType, String fileName) {
        if (inputStream == null) {
            return new OssResult(false, null, null, "文件不能为空");
        }
        if (StrUtil.isBlank(fileName)) {
            return new OssResult(false, null, null, "文件名不能为空");
        }
        // 上传文件最大值 MB->bytes
        long maxSize = OssConfig.MAX_SIZE * 1024 * 1024;
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
     * 上传文件-自定义路径
     *
     * @param file     上传文件
     * @param fileName 上传至OSS的文件完整路径，例：cf/abc.png
     *                 上传至根目录，例：abc.png
     * @return
     */
    default public OssResult uploadFile(File file, String fileName) {
        // 文件流
        InputStream inputStream = FileUtil.getInputStream(file);
        // 获取文件类型
        String fileType = FileUtil.getType(file);

        // 上传文件
        return uploadInputStream(inputStream, fileType, fileName);
    }


    /**
     * 上传文件
     *
     * @param inputStream
     * @param fileType
     * @param fileName
     * @return
     */
    public OssResult putFile(InputStream inputStream, String fileType, String fileName);



    /**
     * 根据文件名生成文件的访问地址（带过期时间）,或者是本地文件路径
     *
     * @param fileName
     * @return
     */
    public String getOssUrl(String fileName);

    /**
     * 通过文件名下载文件
     *
     * @param fileName      要下载的文件名（OSS服务器上的）
     *                      例如：4DB049D0604047989183CB68D76E969D.jpg
     * @param localFileName 本地要创建的文件名（下载到本地的）
     *                      例如：C:\Users\Administrator\Desktop\test.jpg
     */
    public boolean downloadFile(String fileName, String localFileName);

    /**
     * 通过文件名获取文件流
     *
     * @param fileName 要下载的文件名（OSS服务器上的）
     *                 例如：4DB049D0604047989183CB68D76E969D.jpg
     */
    public InputStream getInputStream(String fileName);

    /**
     * 通过文件名获取byte[]
     *
     * @param fileName 要下载的文件名（OSS服务器上的）
     *                 例如：4DB049D0604047989183CB68D76E969D.jpg
     */
    public byte[] getBytes(String fileName);

    /**
     * 根据文件名删除文件
     *
     * @param fileName 需要删除的文件名
     * @return boolean 是否删除成功
     * 例如：4DB049D0604047989183CB68D76E969D.jpg
     */
    public boolean deleteFile(String fileName);

}
