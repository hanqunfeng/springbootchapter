package com.example.support;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
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
    public OssResult uploadFile(File file, String fileName);

    /**
     * 上传文件-自定义路径
     *
     * @param file     上传文件
     * @param fileName 上传至OSS的文件完整路径，例：cf/abc.png
     *                 上传至根目录，例：abc.png
     * @return
     */
    public OssResult uploadFile(MultipartFile file, String fileName);

    /**
     * 上传文件-自定义路径
     *
     * @param inputStream 上传文件流
     * @param fileType    文件类型，例：png
     * @param fileName    上传至OSS的文件完整路径，例：cf/abc.png
     *                    上传至根目录，例：abc.png
     * @return
     */
    public OssResult uploadInputStream(InputStream inputStream, String fileType, String fileName);



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
