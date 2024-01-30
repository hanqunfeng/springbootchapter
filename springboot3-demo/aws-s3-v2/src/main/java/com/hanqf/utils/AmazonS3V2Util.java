package com.hanqf.utils;

import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * <h1>aws s3 v2工具类</h1>
 * Created by hanqf on 2024/1/26 15:12.
 */

@Slf4j
public class AmazonS3V2Util {

    /**
     * 适用于所有功能(不支持目录的上传下载，可以使用 transferManager)
     */
    private static final S3Client S3;

    /*
     * 初始化
     */
    static {
        S3 = S3ClientFactory.s3Client;
    }

    /**
     * 上传文件到s3
     *
     * @param objectKey  上传到s3的文件名
     * @param objectPath 上传文件的路径
     */
    public static void putS3Object(String bucketName, String objectKey, String objectPath) {
        PutObjectRequest putOb = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();

        S3.putObject(putOb, RequestBody.fromFile(new File(objectPath)));
        log.info("Successfully placed " + objectKey + " into bucket " + bucketName);
    }

    /**
     * 上传文件到s3
     *
     * @param objectKey 上传到s3的文件名
     * @param bytes     上传文件的字节
     */
    public static void putS3Object(String bucketName, String objectKey, byte[] bytes) {
        PutObjectRequest putOb = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();

        S3.putObject(putOb, RequestBody.fromBytes(bytes));
        log.info("Successfully placed " + objectKey + " into bucket " + bucketName);
    }

    /**
     * 上传文件到s3
     *
     * @param objectKey   上传到s3的文件名
     * @param inputStream 上传文件的流
     */
    public static void putS3Object(String bucketName, String objectKey, InputStream inputStream) throws IOException {
        final byte[] bytes = MyFileUtil.toByteArray(inputStream);
        putS3Object(bucketName, objectKey, bytes);
    }

    /**
     * 从s3下载文件
     *
     * @param objectKey 下载文件的文件名
     * @return 下载文件的字节
     */
    public static byte[] getObjectBytes(String bucketName, String objectKey) {
        GetObjectRequest objectRequest = GetObjectRequest
                .builder()
                .key(objectKey)
                .bucket(bucketName)
                .build();

        ResponseBytes<GetObjectResponse> objectBytes = S3.getObjectAsBytes(objectRequest);
        return objectBytes.asByteArray();
    }

    /**
     * 从s3下载文件
     *
     * @param objectKey  下载文件的文件名
     * @param outDirPath 下载文件的目录
     */
    public static String downFromS3(String bucketName, String objectKey, String outDirPath) throws IOException {

        byte[] data = getObjectBytes(bucketName, objectKey);

        // Write the data to a local file.
        String filePath = outDirPath + File.separator + objectKey.substring(objectKey.lastIndexOf("/") + 1);
        Path newFilePath = Paths.get(filePath);
        Files.deleteIfExists(newFilePath);
        Files.createDirectories(newFilePath.getParent());

        File myFile = new File(filePath);
        try (OutputStream os = new FileOutputStream(myFile)) {
            os.write(data);
            log.info("Successfully obtained bytes from an S3 object");
        }

        return filePath;
    }


    /**
     * 删除文件
     *
     * @param objectKey 文件名
     */
    public static void delFromS3(String bucketName, String objectKey) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .key(objectKey)
                .bucket(bucketName)
                .build();
        S3.deleteObject(deleteObjectRequest);
    }

    /**
     * 删除多个文件
     *
     * @param listObjectKey 文件名列表
     */
    public static void delFromS3(String bucketName, List<String> listObjectKey) {
        if (listObjectKey != null && !listObjectKey.isEmpty()) {
            ArrayList<ObjectIdentifier> keys = new ArrayList<>();
            for (String objectKey : listObjectKey) {
                keys.add(ObjectIdentifier.builder()
                        .key(objectKey)
                        .build());
            }

            S3.deleteObjects(deleteObjectsRequest -> deleteObjectsRequest
                    .bucket(bucketName)
                    .delete(del -> del.objects(keys))
            );
        }
    }


    /**
     * 验证s3上是否存在名称为bucketName的Bucket
     *
     * @param bucketName 存储桶名称
     * @return boolean
     */
    public static boolean checkBucketExists(String bucketName) {
        HeadBucketRequest headBucketRequest = HeadBucketRequest.builder()
                .bucket(bucketName)
                .build();
        try {
            S3.headBucket(headBucketRequest);
            return true;
        } catch (S3Exception e) {
            if (e.awsErrorDetails().errorCode().equals("NoSuchBucket")) {
                return false;
            } else {
                throw e;
            }
        }
    }

    /**
     * <h2>文件是否存在于S3</h2>
     *
     * @param objectKey  文件名称
     * @param bucketName 存储桶名称
     * @return boolean
     */
    public static boolean isExistFromS3(String bucketName, String objectKey) {
        HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();
        try {
            S3.headObject(headObjectRequest);
            return true;
        } catch (S3Exception e) {
            if (e.awsErrorDetails().errorCode().equals("NoSuchKey")) {
                return false;
            } else {
                throw e;
            }
        }
    }


    /**
     * 列出所有存储桶
     *
     * @return 存储桶名称列表
     */
    public static List<String> listBucketName() {
        ListBucketsRequest listBucketsRequest = ListBucketsRequest.builder().build();
        ListBucketsResponse listBucketsResponse = S3.listBuckets(listBucketsRequest);
        return listBucketsResponse.buckets().stream().map(Bucket::name).toList();
    }


    /**
     * 列出存储桶中所有对象
     *
     * @param bucketName 存储桶名称
     * @return 存储桶中所有对象的列表
     */
    public static List<S3Object> listObjectsInBucket(String bucketName) {
        return S3.listObjects(ListObjectsRequest.builder()
                        .bucket(bucketName)
                        .build())
                .contents();
    }

    /**
     * 分页列出存储桶中所有对象
     *
     * @param bucketName        存储桶名称
     * @param maxKeys           每页最大数量
     * @param continuationToken 下一个结果页的token ,通过 response.nextContinuationToken()获取，第一页为null
     * @return ListObjectsV2Response response
     */
    public static ListObjectsV2Response listBucketObjectsByPage(String bucketName, int maxKeys, String continuationToken) {

        ListObjectsV2Request listReq = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .maxKeys(maxKeys)
                .continuationToken(continuationToken)
                .build();

        return S3.listObjectsV2(listReq);
    }


    /**
     * 列出存储桶中指定前缀的所有对象
     *
     * @param bucketName 存储桶名称
     * @param maxKeys    每页最大数量
     * @param prefix     文件名前缀
     * @return 存储桶中所有对象的列表
     */
    public static List<S3Object> listBucketObjects(String bucketName, int maxKeys, String prefix) {

        ListObjectsV2Request listReq = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .maxKeys(maxKeys)
                .prefix(prefix)
                .build();

        return S3.listObjectsV2(listReq).contents();
    }

    /**
     * 分页列出存储桶中指定的前缀的所有对象
     *
     * @param bucketName        存储桶名称
     * @param maxKeys           每页最大数量
     * @param continuationToken 下一个结果页的token ,通过 response.nextContinuationToken()获取，第一页为null
     * @param prefix            文件名前缀
     * @return ListObjectsV2Response response
     */
    public static ListObjectsV2Response listBucketObjectsByPage(String bucketName, int maxKeys, String continuationToken, String prefix) {

        ListObjectsV2Request listReq = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .maxKeys(maxKeys)
                .prefix(prefix)
                .continuationToken(continuationToken)
                .build();

        return S3.listObjectsV2(listReq);
    }

    /**
     * 初始化分片上传
     *
     * @param bucketName 存储桶名称
     * @param keyName    文件名称
     * @param md5Str     文件md5值
     * @return uploadId
     */
    private static String initiateMultipartUpload(String bucketName, String keyName, String md5Str) {
        // 存储对象的md5值，这里自定义属性存储
        Map<String, String> metadata = new HashMap<>();
        metadata.put("ObjectMd5", md5Str);

        CreateMultipartUploadRequest createMultipartUploadRequest = CreateMultipartUploadRequest.builder()
                .bucket(bucketName)
                .key(keyName)
                .metadata(metadata)
                .build();

        CreateMultipartUploadResponse response = S3.createMultipartUpload(createMultipartUploadRequest);

        return response.uploadId();
    }

    /**
     * <h2>得到总共的段数，和分段后每个段的开始上传的字节位置</h2>
     *
     * @param fileSize 文件总大小
     * @return java.util.List&lt;java.lang.Long&gt;   每个段的开始上传的字节位置的 List
     */
    public static List<Long> positions(long fileSize) {
        // 得到总共的段数，和分段后每个段的开始上传的字节位置
        List<Long> positions = Collections.synchronizedList(new ArrayList<>());
        long filePosition = 0;
        while (filePosition < fileSize) {
            positions.add(filePosition);
            filePosition += Math.min(S3ClientFactory.MIN_PART_SIZE, (fileSize - filePosition));
        }
        log.info("总大小:{} bytes，约为:{} MB，分为{}段", fileSize, new BigDecimal(fileSize).divide(new BigDecimal(1024 * 1024), 2, RoundingMode.HALF_UP), positions.size());
        return positions;
    }

    /**
     * <h2>上传分片</h2>
     *
     * @param bucketName 存储桶名称
     * @param keyName    文件名称
     * @param uploadId   上传id
     * @param bytes      该部分文件内容
     * @param partNumber 分片号
     * @return CompletedPart
     */
    private static CompletedPart uploadPart(String bucketName,
                                            String keyName,
                                            String uploadId,
                                            byte[] bytes,
                                            int partNumber) {
        long time1 = System.currentTimeMillis();
        int partSize = bytes.length;
        UploadPartRequest uploadPartRequest = UploadPartRequest.builder()
                .bucket(bucketName).key(keyName)
                .uploadId(uploadId)
                .partNumber(partNumber)
                .build();

        UploadPartResponse uploadPartResponse = S3.uploadPart(uploadPartRequest, RequestBody.fromBytes(bytes));

        final CompletedPart completedPart = CompletedPart.builder().partNumber(partNumber).eTag(uploadPartResponse.eTag()).build();
        long time2 = System.currentTimeMillis();
        log.info("remoteFileName={},uploadId={},第{}段上传完成，耗时：{}ms.{}", keyName, uploadId, partNumber, (time2 - time1), " [partSize=" + partSize + " bytes,约等于 " + new BigDecimal(partSize).divide(new BigDecimal(1024 * 1024), 2, RoundingMode.HALF_UP) + " MB]");
        return completedPart;
    }

    /**
     * <h2>完成分片上传</h2>
     */
    private static void completeMultipartUpload(
            String bucketName,
            String keyName,
            String uploadId,
            List<CompletedPart> parts) {

        CompleteMultipartUploadRequest completeMultipartUploadRequest = CompleteMultipartUploadRequest.builder()
                .bucket(bucketName)
                .key(keyName)
                .uploadId(uploadId)
                .multipartUpload(completedMultipartUpload -> completedMultipartUpload.parts(parts))
                .build();

        S3.completeMultipartUpload(completeMultipartUploadRequest);
    }

    /**
     * <h2>取消分片上传</h2>
     *
     * @param bucketName 存储桶名称
     * @param keyName    文件名称
     * @param uploadId   上传id
     */
    private static void abortUpload(String bucketName, String keyName, String uploadId) {
        AbortMultipartUploadRequest abortMultipartUploadRequest = AbortMultipartUploadRequest.builder()
                .bucket(bucketName)
                .key(keyName)
                .uploadId(uploadId)
                .build();
        S3.abortMultipartUpload(abortMultipartUploadRequest);
    }


    /**
     * <h2>同步上传</h2>
     *
     * @param bucketName     存储桶名称
     * @param bytes          文件字节数组
     * @param keyName        保存到S3的路径
     * @param uploadId       Multipart UploadId
     * @param completedParts 分段上传的 completedParts List
     * @param positions      分段信息，每个分段的起始字节位置
     */
    private static void uploadSync(String bucketName, byte[] bytes, String keyName, String uploadId, List<CompletedPart> completedParts, List<Long> positions) {
        for (int i = 0; i < positions.size(); i++) {
            byte[] bs;
            if (i == positions.size() - 1) {
                bs = Arrays.copyOfRange(bytes, positions.get(i).intValue(), bytes.length);
            } else {
                bs = Arrays.copyOfRange(bytes, positions.get(i).intValue(), positions.get(i + 1).intValue());
            }
            int partNumber = i + 1;

            CompletedPart part = uploadPart(bucketName, keyName, uploadId, bs, partNumber);
            completedParts.add(part);
        }
    }

    /**
     * <h2>异步上传</h2>
     *
     * @param bucketName     存储桶名称
     * @param bytes          文件字节数组
     * @param keyName        保存到S3的路径
     * @param uploadId       Multipart UploadId
     * @param completedParts 分段上传的 completedParts List
     * @param positions      分段信息，每个分段的起始字节位置
     */
    private static void uploadAsync(String bucketName, byte[] bytes, String keyName, String uploadId, List<CompletedPart> completedParts, List<Long> positions) {
        //声明线程池
        ExecutorService exec = Executors.newFixedThreadPool(10);

        for (int i = 0; i < positions.size(); i++) {
            int finalI = i;
            // 多线程上传
            exec.execute(() -> {
                byte[] bs;
                if (finalI == positions.size() - 1) {
                    bs = Arrays.copyOfRange(bytes, positions.get(finalI).intValue(), bytes.length);
                } else {
                    bs = Arrays.copyOfRange(bytes, positions.get(finalI).intValue(), positions.get(finalI + 1).intValue());
                }
                int partNumber = finalI + 1;

                CompletedPart part = uploadPart(bucketName, keyName, uploadId, bs, partNumber);
                completedParts.add(part);
            });
        }

        //任务结束关闭线程池
        exec.shutdown();
        //判断线程池是否结束，不加会直接结束方法
        while (true) {
            if (exec.isTerminated()) {
                break;
            }
        }
    }

    /**
     * 分片上传
     *
     * @param bucketName 存储桶名称
     * @param keyName    文件名称
     * @param bytes      文件字节流
     */
    public static void multipartUpload(String bucketName, String keyName, byte[] bytes, boolean async) {

        //小于等于5M不进行分段上传
        if (bytes.length <= S3ClientFactory.MIN_PART_SIZE) {
            putS3Object(bucketName, keyName, bytes);
        } else {
            long begin = System.currentTimeMillis();
            // 第一步，初始化，声明下面将有一个 Multipart Upload
            String uploadId = initiateMultipartUpload(bucketName, keyName, MyFileUtil.getBytesMD5(bytes));

            try {
                List<CompletedPart> completedParts = new ArrayList<>();
                // 第二步，分段上传
                // 得到总共的段数，和分段后每个段的开始上传的字节位置
                List<Long> positions = positions(bytes.length);
                if (async) {
                    // 异步上传
                    uploadAsync(bucketName, bytes, keyName, uploadId, completedParts, positions);
                    //在完成多部分上传时提交的列表需要按照分段号的升序顺序进行排列，否则提交失败
                    completedParts.sort(Comparator.comparingInt(CompletedPart::partNumber));
                } else {
                    // 同步上传
                    uploadSync(bucketName, bytes, keyName, uploadId, completedParts, positions);
                }

                // 第三步，完成上传，合并分段
                completeMultipartUpload(bucketName, keyName, uploadId, completedParts);
            } catch (Exception e) {
                abortUpload(bucketName, keyName, uploadId);
                log.error(e.getMessage());
            }
            long end = System.currentTimeMillis();
            log.info("remoteFileName={},uploadId={},fileSize={} bytes,总上传耗时：{}ms", keyName, uploadId, bytes.length, (end - begin));
        }
    }

    public static void multipartUpload(String bucketName, String keyName, String filePath, boolean async) {
        final byte[] bytes = MyFileUtil.fileToBytes(new File(filePath));
        multipartUpload(bucketName, keyName, bytes, async);
    }

    public static void multipartUpload(String bucketName, String keyName, InputStream inputStream, boolean async) {
        final byte[] bytes;
        try {
            bytes = MyFileUtil.toByteArray(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        multipartUpload(bucketName, keyName, bytes, async);
    }


    /**
     * <h2>获取文件指定字节范围的流</h2>
     *
     * @param bucketName 存储桶名称
     * @param keyName    文件名
     * @param start      开始字节数
     * @param end        结束字节数
     * @return java.io.InputStream
     */
    private static InputStream getSubsectionInputStream(String bucketName, String keyName, long start, long end) {
        InputStream is = null;
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(keyName)
                    .range("bytes=" + start + "-" + end)
                    .build();

            is = S3.getObject(getObjectRequest, ResponseTransformer.toInputStream());
        } catch (Exception e) {
            log.error("获取指定字节范围的流时失败", e);
        }
        log.info("remoteFileName= " + keyName + " result=" + (is == null ? "false" : "true"));
        return is;
    }

    /**
     * <h2>获取文件指定字节范围的流并写到输出流中</h2>
     *
     * @param bucketName 存储桶名称
     * @param keyName    文件名
     * @param start      开始字节数
     * @param end        结束字节数
     * @param os         输出流
     */
    public static void downloadSubsectionToOutputStream(String bucketName, String keyName, long start, long end, OutputStream os) {
        try (InputStream inputStream = getSubsectionInputStream(bucketName, keyName, start, end)) {
            long length = end - start + 1;
            MyIOUtil.copyDataFromInputStreamToOutputStream(inputStream, os, length);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * <h2>获取文件指定字节范围的流并写到输出流中后关闭输出流</h2>
     *
     * @param bucketName 存储桶名称
     * @param keyName    文件名
     * @param start      开始字节数
     * @param end        结束字节数
     * @param os         输出流
     */
    public static void downloadSubsectionAndCloseOutputStream(String bucketName, String keyName, long start, long end, OutputStream os) {
        try {
            downloadSubsectionToOutputStream(bucketName, keyName, start, end, os);
            os.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                os.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * <h2>获取文件信息</h2>
     */
    public static HeadObjectResponse getObjectInfo(String bucketName, String keyName) {
        HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                .bucket(bucketName)
                .key(keyName)
                .build();

        return S3.headObject(headObjectRequest);
    }

    /**
     * 分段下载
     *
     * @param bucketName 存储桶名称
     * @param keyName    文件名称
     * @param outDirPath 文件输出目录
     * @return String 下载后的文件路径
     */
    public static String downloadSubsection(String bucketName, String keyName, String outDirPath) throws IOException {
        final long contentLength = getObjectInfo(bucketName, keyName).contentLength();
        //小于5M不进行分段下载
        if (contentLength < S3ClientFactory.MIN_PART_SIZE) {
            return downFromS3(bucketName, keyName, outDirPath);
        } else {
            final List<Long> positions = positions(contentLength);
            positions.add(contentLength);
            final Path dirPath = Paths.get(outDirPath);
            Files.createDirectories(dirPath);

            //声明线程池
            ExecutorService exec = Executors.newFixedThreadPool(10);

            for (int i = 1; i < positions.size(); i++) {
                final File file = new File(dirPath + File.separator + i);
                if (file.exists()) {
                    file.delete();
                }
                int finalI = i;
                exec.execute(() -> {
                    try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
                        log.info(finalI + ":" + positions.get(finalI - 1) + "~" + (positions.get(finalI) - 1));
                        downloadSubsectionToOutputStream(bucketName, keyName, positions.get(finalI - 1), positions.get(finalI) - 1, fileOutputStream);
                    } catch (Exception e) {
                        log.error("", e);
                    }
                });

            }
            //任务结束关闭线程池
            exec.shutdown();
            //判断线程池是否结束，不加会直接结束方法
            while (true) {
                if (exec.isTerminated()) {
                    break;
                }
            }

            String fileName = keyName;
            if (keyName.contains("/")) {
                fileName = keyName.substring(keyName.lastIndexOf("/") + 1);
            }

            //合并下载后的分段文件
            log.info("开始合并文件，待合并文件数:" + (positions.size() - 1));
            Path newFilePath = Paths.get(dirPath + File.separator + fileName);

            Files.deleteIfExists(newFilePath);
            Files.createDirectories(newFilePath.getParent());
            Files.createFile(newFilePath);


            for (int i = 1; i < positions.size(); i++) {
                final Path path = Paths.get(dirPath + File.separator + i);
                Files.write(newFilePath, Files.readAllBytes(path), StandardOpenOption.APPEND);
                log.info("合并文件完成No:" + i);
            }
            log.info("合并文件完成!!!");
            //删除分段下载的文件
            for (int i = 1; i < positions.size(); i++) {
                final Path path = Paths.get(dirPath + File.separator + i);
                Files.delete(path);
            }
            log.info("文件保存路径：" + newFilePath);
            return newFilePath.toString();
        }
    }


    public static void main(String[] args) throws Exception {
        String filePath = "/Users/hanqf/develop_soft/node-v16.17.0-darwin-x64.tar.gz";
        String remoteFileName = "test/node-v16.17.0-darwin-x64.tar.gz";
//        AmazonS3V2Util.putS3Object(S3ClientFactory.BUCKET_NAME, remoteFileName, filePath);

        // 下载
        String outPath = "/Users/hanqf/Desktop/download/";
//        AmazonS3V2Util.downFromS3(S3ClientFactory.BUCKET_NAME,remoteFileName, outPath);

        //上传目录
        String sourceDirectory = "/Users/hanqf/Desktop/testDir";
//        System.out.println(AmazonS3V2Util.uploadDirectory(S3ClientFactory.BUCKET_NAME, sourceDirectory));
//        System.out.println(AmazonS3V2Util.downloadObjectsToDirectory(S3ClientFactory.BUCKET_NAME, outPath));
//        System.out.println(AmazonS3V2Util.checkBucketExists(S3ClientFactory.BUCKET_NAME));
//        System.out.println(AmazonS3V2Util.isExistFromS3(S3ClientFactory.BUCKET_NAME, remoteFileName));

        //分段上传
//        AmazonS3V2Util.multipartUpload(S3ClientFactory.BUCKET_NAME, remoteFileName, filePath, true);

        //列出桶
//        AmazonS3V2Util.listBucketName().forEach(System.out::println);

        //列出指定桶中的文件信息
//        AmazonS3V2Util.listObjectsInBucket(S3ClientFactory.BUCKET_NAME).forEach(System.out::println);
//        System.out.println("####################");

        //分页查询
//        int maxKeys = 2;
//        String token = null;
//        do {
//            final ListObjectsV2Response responseList = AmazonS3V2Util.listBucketObjectsByPage(S3ClientFactory.BUCKET_NAME, maxKeys, token);
//            responseList.contents().forEach(System.out::println);
//            token = responseList.nextContinuationToken();
//            System.out.println("###########");
//        } while (token != null);

        //分段下载
        AmazonS3V2Util.downloadSubsection(S3ClientFactory.BUCKET_NAME, remoteFileName, outPath);
    }


}
