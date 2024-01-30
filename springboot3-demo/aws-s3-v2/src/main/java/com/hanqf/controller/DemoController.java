package com.hanqf.controller;

import com.hanqf.utils.AmazonS3V2Util;
import com.hanqf.utils.MyIOUtil;
import com.hanqf.utils.S3ClientFactory;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

@Slf4j
@RestController
public class DemoController {

    /**
     * 文件下载，支持断点续传，支持分段下载
     * 分段下载的好处就是可以进行多线程下载，提高下载速度，下载完成后再将所有的分段文件进行合并成一个完整的文件
     * <p>
     * 可以先下载一个字节的数据，此时可以获取文件总的字节数，然后基于这个总字节数决定分段大小
     */
    @GetMapping("/file-down-range")
    public void fileDownRange(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 服务器上的文件路径，这里只是为了演示方便，实际使用时，需要根据文件的实际存储方式进行处理
        String localFileName = request.getParameter("fileName");
        if (!StringUtils.hasText(localFileName)) {
            throw new RuntimeException("没有上传fileName！");
        }

        File file = new File(localFileName);
        if (!file.exists()) {
            throw new RuntimeException(localFileName + ":文件不存在！");
        }

        final long contentLength = file.length();

        String range = request.getHeader("Range");

        String start = null;
        String end = null;
        if (StringUtils.hasText(range) && (range.contains("bytes=") && range.contains("-"))) {
            start = org.apache.commons.lang.StringUtils.substringBetween(range, "bytes=", "-");
            end = org.apache.commons.lang.StringUtils.substringAfter(range, "-");
        }

        long startIndex = 0;
        long endIndex = contentLength - 1;

        if (StringUtils.hasText(start)) {
            startIndex = Long.parseLong(start);
        }
        if (StringUtils.hasText(end)) {
            endIndex = Long.parseLong(end);
        }
        log.info("range:" + startIndex + "~" + endIndex);

        String fileName = localFileName;
        if (localFileName.contains("/")) {
            fileName = localFileName.substring(localFileName.lastIndexOf("/") + 1);
        }

        if (StringUtils.hasText(start)) {
            fileName = fileName + "." + startIndex + "-" + endIndex;
        }
        long length = endIndex - startIndex + 1;

        response.setContentType("application/octet-stream");
        response.setHeader("Accept-Ranges", "bytes");
        response.setHeader("Content-Range", "bytes " + startIndex + "-" + endIndex + "/" + contentLength);
        response.setHeader("Content-Length", length + "");
        response.addHeader("Content-Disposition", "attachment;filename=" + fileName);

        // 读取文件流
        try (ServletOutputStream outputStream = response.getOutputStream(); InputStream inputStream = Files.newInputStream(file.toPath())) {
            MyIOUtil.copyDataFromInputStreamToOutputStream(inputStream, startIndex, length, outputStream, true);
        }
    }


    /**
     * S3文件下载，分段下载
     * num 分段号，从1开始
     */
    @GetMapping("/s3-down/{num}")
    public void s3Down(@PathVariable int num, HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 分段下载测试
        String remoteFileName = request.getParameter("fileName");
        if (!StringUtils.hasText(remoteFileName)) {
            throw new RuntimeException(remoteFileName + ":文件不存在！");
        }

        String fileName = remoteFileName;
        if (remoteFileName.contains("/")) {
            fileName = remoteFileName.substring(remoteFileName.lastIndexOf("/") + 1);
        }

        fileName = fileName + "." + num;

        final long contentLength = AmazonS3V2Util.getObjectInfo(S3ClientFactory.BUCKET_NAME, remoteFileName).contentLength();
        final java.util.List<Long> positions = AmazonS3V2Util.positions(contentLength);
        positions.add(contentLength);
        long start = positions.get(num - 1);
        long end = positions.get(num) - 1;
        log.info("range:" + start + "~" + end);

        response.setContentType("application/octet-stream");
        response.setHeader("Accept-Ranges", "bytes");
        response.setHeader("Content-Range", "bytes " + start + "-" + end + "/" + contentLength);
        response.setHeader("Content-Length", (end - start + 1) + "");
        response.addHeader("Content-Disposition", "attachment;filename=" + fileName);
        ServletOutputStream outputStream = response.getOutputStream();
        AmazonS3V2Util.downloadSubsectionAndCloseOutputStream(S3ClientFactory.BUCKET_NAME, remoteFileName, start, end, outputStream);

    }

    /**
     * S3文件下载，支持断点续传，支持分段下载
     */
    @GetMapping("/s3-down-range")
    public void s3DownRange(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 分段下载测试
        String remoteFileName = request.getParameter("fileName");
        if (!StringUtils.hasText(remoteFileName)) {
            throw new RuntimeException(remoteFileName + ":文件不存在！");
        }
        final long contentLength = AmazonS3V2Util.getObjectInfo(S3ClientFactory.BUCKET_NAME, remoteFileName).contentLength();
        String range = request.getHeader("Range");

        String start = null;
        String end = null;
        if (StringUtils.hasText(range) && (range.contains("bytes=") && range.contains("-"))) {
            start = org.apache.commons.lang.StringUtils.substringBetween(range, "bytes=", "-");
            end = org.apache.commons.lang.StringUtils.substringAfter(range, "-");

        }

        long startIndex = 0;
        long endIndex = contentLength - 1;

        if (StringUtils.hasText(start)) {
            startIndex = Long.parseLong(start);
        }
        if (StringUtils.hasText(end)) {
            endIndex = Long.parseLong(end);
        }
        log.info("range:" + startIndex + "~" + endIndex);

        String fileName = remoteFileName;
        if (remoteFileName.contains("/")) {
            fileName = remoteFileName.substring(remoteFileName.lastIndexOf("/") + 1);
        }

        if (StringUtils.hasText(start)) {
            fileName = fileName + "." + startIndex + "-" + endIndex;
        }

        response.setContentType("application/octet-stream");
        response.setHeader("Accept-Ranges", "bytes");
        response.setHeader("Content-Range", "bytes " + startIndex + "-" + endIndex + "/" + contentLength);
        response.setHeader("Content-Length", (endIndex - startIndex + 1) + "");
        response.addHeader("Content-Disposition", "attachment;filename=" + fileName);
        ServletOutputStream outputStream = response.getOutputStream();
        AmazonS3V2Util.downloadSubsectionAndCloseOutputStream(S3ClientFactory.BUCKET_NAME, remoteFileName, startIndex, endIndex, outputStream);

    }

    /**
     * 上传文件到S3，支持分段上传
    */
    @PostMapping("/uploadToS3")
    public String uploadFileToS3(MultipartFile file, String remoteFileName) throws IOException {
        if (file != null && StringUtils.hasText(remoteFileName)) {
            log.info("上传文件到S3：" + remoteFileName);
            AmazonS3V2Util.multipartUpload(S3ClientFactory.BUCKET_NAME, remoteFileName, file.getBytes(), true);
            return "上传成功";
        }
        return "上传失败";
    }

}

