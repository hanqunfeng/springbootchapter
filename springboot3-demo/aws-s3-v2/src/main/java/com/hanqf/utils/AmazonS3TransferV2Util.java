package com.hanqf.utils;

import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.transfer.s3.S3TransferManager;
import software.amazon.awssdk.transfer.s3.model.*;
import software.amazon.awssdk.transfer.s3.progress.LoggingTransferListener;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * <h1>aws s3 v2工具类</h1>
 * Created by hanqf on 2024/1/26 15:12.
 */

@Slf4j
public class AmazonS3TransferV2Util {


    /**
     * 适用于文件和目录的上传和下载
     */
    private static final S3TransferManager transferManager;

    /*
     * 初始化
     */
    static {
        transferManager = S3ClientFactory.transferManager;
    }


    /**
     * 从s3下载文件,transferManager,会显示下载进度
     *
     * @param objectKey  下载文件的文件名
     * @param outDirPath 下载文件的目录
     * @return 下载文件的字节
     */
    public static Long downloadFile(String bucketName, String objectKey, String outDirPath) throws IOException {
        String filePath = outDirPath + File.separator + objectKey.substring(objectKey.lastIndexOf("/") + 1);
        Path newFilePath = Paths.get(filePath);
        Files.deleteIfExists(newFilePath);
        Files.createDirectories(newFilePath.getParent());

        DownloadFileRequest downloadFileRequest =
                DownloadFileRequest.builder()
                        .getObjectRequest(b -> b.bucket(bucketName).key(objectKey))
                        .addTransferListener(LoggingTransferListener.create())
                        .destination(newFilePath)
                        .build();

        FileDownload downloadFile = transferManager.downloadFile(downloadFileRequest);

        CompletedFileDownload downloadResult = downloadFile.completionFuture().join();
        log.info("Content length [{}]", downloadResult.response().contentLength());
        return downloadResult.response().contentLength();
    }

    /**
     * 上传文件到s3,transferManager,会显示上传进度
     *
     * @param objectKey  上传到s3的文件名
     * @param objectPath 上传文件的路径
     */
    public static String uploadFile(String bucketName, String objectKey, String objectPath) {
        UploadFileRequest uploadFileRequest =
                UploadFileRequest.builder()
                        .putObjectRequest(b -> b.bucket(bucketName).key(objectKey))
                        .addTransferListener(LoggingTransferListener.create())
                        .source(Paths.get(objectPath))
                        .build();

        FileUpload fileUpload = transferManager.uploadFile(uploadFileRequest);

        CompletedFileUpload uploadResult = fileUpload.completionFuture().join();
        return uploadResult.response().eTag();
    }

    /**
     * 上传目录(含子目录)到s3,transferManager,不支持进度监听
     *
     * @param sourceDirectory 上传目录的路径
     * @return 失败的文件数量
     */
    public static Integer uploadDirectory(String bucketName, String sourceDirectory) {
        DirectoryUpload directoryUpload =
                transferManager.uploadDirectory(UploadDirectoryRequest.builder()
                        .source(Paths.get(sourceDirectory))
                        .bucket(bucketName)
                        .build());


        CompletedDirectoryUpload completedDirectoryUpload = directoryUpload.completionFuture().join();
        completedDirectoryUpload.failedTransfers().forEach(fail ->
                log.warn("Object [{}] failed to transfer", fail.toString()));
        return completedDirectoryUpload.failedTransfers().size();
    }

    /**
     * 下载存储桶到本地,transferManager,不支持进度监听
     *
     * @param destinationPath 下载目录的路径
     * @return 失败的文件数量
     */
    public static Integer downloadObjectsToDirectory(String bucketName, String destinationPath) {
        DirectoryDownload directoryDownload =
                transferManager.downloadDirectory(DownloadDirectoryRequest.builder()
                        .destination(Paths.get(destinationPath))
                        .bucket(bucketName)
                        .build());
        CompletedDirectoryDownload completedDirectoryDownload = directoryDownload.completionFuture().join();

        completedDirectoryDownload.failedTransfers().forEach(fail ->
                log.warn("Object [{}] failed to transfer", fail.toString()));
        return completedDirectoryDownload.failedTransfers().size();
    }



    public static void main(String[] args) throws Exception {
        String filePath = "/Users/hanqf/develop_soft/node-v16.17.0-darwin-x64.tar.gz";
        String remoteFileName = "test/node-v16.17.0-darwin-x64.tar.gz";
        System.out.println(AmazonS3TransferV2Util.uploadFile(S3ClientFactory.BUCKET_NAME, remoteFileName, filePath));

        // 下载
        String outPath = "/Users/hanqf/Desktop/download/";
        System.out.println(AmazonS3TransferV2Util.downloadFile(S3ClientFactory.BUCKET_NAME, remoteFileName, outPath));

        //上传目录
        String sourceDirectory = "/Users/hanqf/Desktop/testDir";
//        System.out.println(AmazonS3TransferV2Util.uploadDirectory(S3ClientFactory.BUCKET_NAME, sourceDirectory));
//        System.out.println(AmazonS3TransferV2Util.downloadObjectsToDirectory(S3ClientFactory.BUCKET_NAME, outPath));


    }

}
