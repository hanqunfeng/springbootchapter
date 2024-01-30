package com.hanqf.utils;

import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.core.async.AsyncResponseTransformer;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.*;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * <h1>aws s3 v2工具类</h1>
 * Created by hanqf on 2024/1/26 15:12.
 */

@Slf4j
public class AmazonS3CRTV2Util {

    /**
     * 异步
     * 适用于所有功能(不支持目录的上传下载，可以使用 transferManager)
     */
    private static final S3AsyncClient s3AsyncClient;


    /*
     * 初始化
     */
    static {
        s3AsyncClient = S3ClientFactory.s3AsyncClient;
    }

    /**
     * 上传文件到s3
     *
     * @param objectKey  上传到s3的文件名
     * @param objectPath 上传文件的路径
     */
    public static CompletableFuture<PutObjectResponse> putS3Object(String bucketName, String objectKey, String objectPath) {
        PutObjectRequest putOb = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();

        final CompletableFuture<PutObjectResponse> putObjectResponseCompletableFuture = s3AsyncClient.putObject(putOb, AsyncRequestBody.fromFile(new File(objectPath)));
        log.info("Successfully placed " + objectKey + " into bucket " + bucketName);
        return putObjectResponseCompletableFuture;
    }

    /**
     * 上传文件到s3
     *
     * @param objectKey 上传到s3的文件名
     * @param bytes     上传文件的字节
     */
    public static CompletableFuture<PutObjectResponse> putS3Object(String bucketName, String objectKey, byte[] bytes) {
        PutObjectRequest putOb = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();

        final CompletableFuture<PutObjectResponse> putObjectResponseCompletableFuture = s3AsyncClient.putObject(putOb, AsyncRequestBody.fromBytes(bytes));
        log.info("Successfully placed " + objectKey + " into bucket " + bucketName);
        return putObjectResponseCompletableFuture;
    }

    /**
     * 上传文件到s3
     *
     * @param objectKey   上传到s3的文件名
     * @param inputStream 上传文件的流
     */
    public static CompletableFuture<PutObjectResponse> putS3Object(String bucketName, String objectKey, InputStream inputStream) throws IOException {
        final byte[] bytes = MyFileUtil.toByteArray(inputStream);
        return putS3Object(bucketName, objectKey, bytes);
    }

    /**
     * 从s3下载文件
     *
     * @param objectKey 下载文件的文件名
     * @return 下载文件的字节
     */
    public static CompletableFuture<byte[]> getObjectBytes(String bucketName, String objectKey) {
        GetObjectRequest objectRequest = GetObjectRequest
                .builder()
                .key(objectKey)
                .bucket(bucketName)
                .build();

        final CompletableFuture<ResponseBytes<GetObjectResponse>> bytesCompletableFuture = s3AsyncClient.getObject(objectRequest, AsyncResponseTransformer.toBytes());
        return bytesCompletableFuture.thenApply(ResponseBytes::asByteArray);

    }

    /**
     * 从s3下载文件
     *
     * @param objectKey  下载文件的文件名
     * @param outDirPath 下载文件的目录
     */
    public static CompletableFuture<String> downFromS3(String bucketName, String objectKey, String outDirPath) {

        final CompletableFuture<byte[]> objectBytes = getObjectBytes(bucketName, objectKey);
        return objectBytes.thenApply(data -> {
            // Write the data to a local file.
            String filePath = outDirPath + File.separator + objectKey.substring(objectKey.lastIndexOf("/") + 1);
            Path newFilePath = Paths.get(filePath);
            try {
                Files.deleteIfExists(newFilePath);
                Files.createDirectories(newFilePath.getParent());

                File myFile = new File(filePath);
                try (OutputStream os = new FileOutputStream(myFile)) {
                    os.write(data);
                    log.info("Successfully obtained bytes from an S3 object");
                }
                return filePath;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        });


    }


    /**
     * 删除文件
     *
     * @param objectKey 文件名
     */
    public static CompletableFuture<DeleteObjectResponse> delFromS3(String bucketName, String objectKey) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .key(objectKey)
                .bucket(bucketName)
                .build();
        return s3AsyncClient.deleteObject(deleteObjectRequest);
    }

    /**
     * 删除多个文件
     *
     * @param listObjectKey 文件名列表
     */
    public static CompletableFuture<DeleteObjectsResponse> delFromS3(String bucketName, List<String> listObjectKey) {
        if (listObjectKey != null && !listObjectKey.isEmpty()) {
            ArrayList<ObjectIdentifier> keys = new ArrayList<>();
            for (String objectKey : listObjectKey) {
                keys.add(ObjectIdentifier.builder()
                        .key(objectKey)
                        .build());
            }

            return s3AsyncClient.deleteObjects(deleteObjectsRequest -> deleteObjectsRequest
                    .bucket(bucketName)
                    .delete(del -> del.objects(keys))
            );
        } else {
            throw new RuntimeException("listObjectKey is null");
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
            s3AsyncClient.headBucket(headBucketRequest).join();
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
            s3AsyncClient.headObject(headObjectRequest).join();
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
        ListBucketsResponse listBucketsResponse = s3AsyncClient.listBuckets(listBucketsRequest).join();
        return listBucketsResponse.buckets().stream().map(Bucket::name).toList();
    }


    /**
     * 列出存储桶中所有对象
     *
     * @param bucketName 存储桶名称
     * @return 存储桶中所有对象的列表
     */
    public static List<S3Object> listObjectsInBucket(String bucketName) {
        return s3AsyncClient.listObjects(ListObjectsRequest.builder()
                        .bucket(bucketName)
                        .build()).join()
                .contents();
    }




    /**
     * 初始化分片上传
     *
     * @param bucketName 存储桶名称
     * @param keyName    文件名称
     * @param md5Str     文件md5值
     * @return uploadId
     */
    private static CompletableFuture<String> initiateMultipartUpload(String bucketName, String keyName, String md5Str) {
        // 存储对象的md5值，这里自定义属性存储
        Map<String, String> metadata = new HashMap<>();
        metadata.put("ObjectMd5", md5Str);

        CreateMultipartUploadRequest createMultipartUploadRequest = CreateMultipartUploadRequest.builder()
                .bucket(bucketName)
                .key(keyName)
                .metadata(metadata)
                .build();

        final CompletableFuture<CreateMultipartUploadResponse> multipartUploadResponse = s3AsyncClient.createMultipartUpload(createMultipartUploadRequest);
        return multipartUploadResponse.thenApply(CreateMultipartUploadResponse::uploadId);
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
    private static CompletableFuture<CompletedPart> uploadPart(String bucketName,
                                                               String keyName,
                                                               String uploadId,
                                                               byte[] bytes,
                                                               int partNumber) {

        UploadPartRequest uploadPartRequest = UploadPartRequest.builder()
                .bucket(bucketName).key(keyName)
                .uploadId(uploadId)
                .partNumber(partNumber)
                .build();

        final CompletableFuture<UploadPartResponse> uploadPartResponseCompletableFuture = s3AsyncClient.uploadPart(uploadPartRequest, AsyncRequestBody.fromBytes(bytes));
        final CompletableFuture<CompletedPart> completedPartCompletableFuture = uploadPartResponseCompletableFuture.thenApply(uploadPartResponse -> CompletedPart.builder().partNumber(partNumber).eTag(uploadPartResponse.eTag()).build());
        return completedPartCompletableFuture;
    }

    /**
     * <h2>完成分片上传</h2>
     */
    private static CompletableFuture<CompleteMultipartUploadResponse> completeMultipartUpload(
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

        return s3AsyncClient.completeMultipartUpload(completeMultipartUploadRequest);
    }

    /**
     * <h2>取消分片上传</h2>
     *
     * @param bucketName 存储桶名称
     * @param keyName    文件名称
     * @param uploadId   上传id
     */
    private static CompletableFuture<AbortMultipartUploadResponse> abortUpload(String bucketName, String keyName, String uploadId) {
        AbortMultipartUploadRequest abortMultipartUploadRequest = AbortMultipartUploadRequest.builder()
                .bucket(bucketName)
                .key(keyName)
                .uploadId(uploadId)
                .build();
        return s3AsyncClient.abortMultipartUpload(abortMultipartUploadRequest);
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
            long time1 = System.currentTimeMillis();
            uploadPart(bucketName, keyName, uploadId, bs, partNumber).whenComplete((part, err) -> {
                if (part != null) {
                    completedParts.add(part);
                    long time2 = System.currentTimeMillis();
                    log.info("remoteFileName={},uploadId={},第{}段上传完成，耗时：{}ms.{}", keyName, uploadId, partNumber, (time2 - time1), " [partSize=" + bs.length + " bytes,约等于 " + new BigDecimal(bs.length).divide(new BigDecimal(1024 * 1024), 2, RoundingMode.HALF_UP) + " MB]");
                } else {
                    log.error("uploadPart error:{}", err.getMessage());
                }
            }).join();
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
                long time1 = System.currentTimeMillis();
                uploadPart(bucketName, keyName, uploadId, bs, partNumber).whenComplete((part, err) -> {
                    if (part != null) {
                        completedParts.add(part);
                        long time2 = System.currentTimeMillis();
                        log.info("remoteFileName={},uploadId={},第{}段上传完成，耗时：{}ms.{}", keyName, uploadId, partNumber, (time2 - time1), " [partSize=" + bs.length + " bytes,约等于 " + new BigDecimal(bs.length).divide(new BigDecimal(1024 * 1024), 2, RoundingMode.HALF_UP) + " MB]");
                    } else {
                        log.error("uploadPart error:{}", err.getMessage());
                    }
                }).join();
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
        long begin = System.currentTimeMillis();
        // 第一步，初始化，声明下面将有一个 Multipart Upload
        String uploadId = initiateMultipartUpload(bucketName, keyName, MyFileUtil.getBytesMD5(bytes)).join();

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
            completeMultipartUpload(bucketName, keyName, uploadId, completedParts).join();
        } catch (Exception e) {
            abortUpload(bucketName, keyName, uploadId).join();
            log.error(e.getMessage());
        }
        long end = System.currentTimeMillis();
        log.info("remoteFileName={},uploadId={},fileSize={} bytes,总上传耗时：{}ms", keyName, uploadId, bytes.length, (end - begin));
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


    public static void main(String[] args) throws Exception {
        String filePath = "/Users/hanqf/develop_soft/node-v16.17.0-darwin-x64.tar.gz";
        String remoteFileName = "test/node-v16.17.0-darwin-x64.tar.gz";
//        AmazonS3CRTV2Util.putS3Object(S3ClientFactory.BUCKET_NAME, remoteFileName, filePath).join();

        // 下载
        String outPath = "/Users/hanqf/Desktop/download/";
//        AmazonS3CRTV2Util.downFromS3(S3ClientFactory.BUCKET_NAME, remoteFileName, outPath).join();

//        System.out.println(AmazonS3V2Util.checkBucketExists(S3ClientFactory.BUCKET_NAME));
//        System.out.println(AmazonS3V2Util.isExistFromS3(S3ClientFactory.BUCKET_NAME, remoteFileName));

        //分段上传
//        AmazonS3CRTV2Util.delFromS3(S3ClientFactory.BUCKET_NAME, remoteFileName).join();
        AmazonS3CRTV2Util.multipartUpload(S3ClientFactory.BUCKET_NAME, remoteFileName, filePath, true);
    }

}
