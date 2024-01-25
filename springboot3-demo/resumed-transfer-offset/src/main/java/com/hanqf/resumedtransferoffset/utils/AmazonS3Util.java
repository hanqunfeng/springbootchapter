package com.hanqf.resumedtransferoffset.utils;


import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.event.ProgressEventType;
import com.amazonaws.event.ProgressListener;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import lombok.extern.slf4j.Slf4j;

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

@Slf4j
public class AmazonS3Util {
    private static final String BUCKET_NAME = "myBucket";
    private static final Regions REGION = Regions.fromName("myRegionName");
    private static final String AWS_ACCESS_KEY = "myAccessKey";
    private static final String AWS_SECRET_KEY = "mySecretKey";

    /**
     * S3client最大连接数
    */
    private static final int MAX_CONNECTIONS = 500;
    /**
     * 分段上传条件，每段必须>=5M，<=5G，这里要注意，分段上传要求除最后一段外，其余段的大小不能小于5M
     */
    public static final int MIN_PART_SIZE = 5 * 1024 * 1024;

    private static final AmazonS3 S3;

    static {
        ClientConfiguration clientConfiguration = new ClientConfiguration();
        clientConfiguration.setMaxConnections(MAX_CONNECTIONS);
        S3 = AmazonS3Client.builder()
                .withClientConfiguration(clientConfiguration)
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(AWS_ACCESS_KEY, AWS_SECRET_KEY)))
                .withRegion(REGION)
                .build();
    }

    /**
     * 将文件上传至S3
     *
     * @param tempFile       目标文件
     * @param remoteFileName 文件名
     */
    public static void uploadToS3(File tempFile, String remoteFileName) {
        //上传文件,remoteFileName相同会覆盖
        S3.putObject(new PutObjectRequest(BUCKET_NAME, remoteFileName, tempFile));
    }


    public static void uploadToS3(InputStream inputStream, String remoteFileName, ObjectMetadata objectMetadata) {
        S3.putObject(new PutObjectRequest(BUCKET_NAME, remoteFileName, inputStream, objectMetadata));
    }

    public static void uploadToS3(InputStream inputStream, String remoteFileName) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        // 声明服务端加密，只在传输时和存储时对数据对象加密，不影响数据对象的查看
        objectMetadata.setSSEAlgorithm(ObjectMetadata.AES_256_SERVER_SIDE_ENCRYPTION);
        uploadToS3(inputStream, remoteFileName, objectMetadata);
    }

    public static void uploadToS3(byte[] bytes, String remoteFileName, ObjectMetadata objectMetadata) {
        //上传文件,remoteFileName相同会覆盖
        S3.putObject(new PutObjectRequest(BUCKET_NAME, remoteFileName, new ByteArrayInputStream(bytes), objectMetadata));
    }

    public static void uploadToS3(byte[] bytes, String remoteFileName) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        // 声明服务端加密，只在传输时和存储时对数据对象加密，不影响数据对象的查看
        objectMetadata.setSSEAlgorithm(ObjectMetadata.AES_256_SERVER_SIDE_ENCRYPTION);
        uploadToS3(bytes, remoteFileName, objectMetadata);
    }

    /**
     * 获取文件基本信息
     *
     * @param remoteFileName 文件名
     * @return ObjectMetadata    返回类型  S3文件元信息
     */
    public static ObjectMetadata getObjectMetadata(String remoteFileName) {
        return S3.getObjectMetadata(BUCKET_NAME, remoteFileName);
    }

    /**
     * 获取文件2进制流
     *
     * @param remoteFileName 文件名
     * @return S3ObjectInputStream    返回类型  数据流
     */
    public static S3ObjectInputStream getContentFromS3(String remoteFileName) {
        try {
            GetObjectRequest request = new GetObjectRequest(BUCKET_NAME, remoteFileName);
            S3Object object = S3.getObject(request);
            return object.getObjectContent();
        } catch (Exception e) {
            log.error("获取文件2进制流发生异常", e);
        }
        return null;
    }


    /**
     * 将文件下载到本地路径
     *
     * @param remoteFileName 文件名
     * @param dirPath        保存到的本地目录
     */
    public static String downFromS3(String remoteFileName, String dirPath) {

        String fileName = remoteFileName;
        if (remoteFileName.contains("/")) {
            fileName = remoteFileName.substring(remoteFileName.lastIndexOf("/") + 1);
        }
        String filePath = dirPath + File.separator + fileName;
        Path newFilePath = Paths.get(filePath);
        try {
            Files.deleteIfExists(newFilePath);
            Files.createDirectories(newFilePath.getParent());

            GetObjectRequest request = new GetObjectRequest(BUCKET_NAME, remoteFileName);
            S3.getObject(request, newFilePath.toFile());
        } catch (Exception e) {
            log.error("下载文件到本地时发生异常", e);
        }

        return filePath;
    }


    /**
     * 验证s3上是否存在名称为bucketName的Bucket
     *
     * @param bucketName 存储桶名称
     * @return boolean
     */
    public static boolean checkBucketExists(String bucketName) {
        List<Bucket> buckets = S3.listBuckets();
        for (Bucket bucket : buckets) {
            if (Objects.equals(bucket.getName(), bucketName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * <h2>文件是否存在于S3</h2>
     *
     * @param remoteFileName 文件名称
     * @return boolean
     */
    public static boolean isExistFromS3(String remoteFileName) {
        try {
            ObjectMetadata s3Object = S3.getObjectMetadata(BUCKET_NAME, remoteFileName);
            if (s3Object != null) {
                return true;
            }
        } catch (AmazonS3Exception e) {
            log.info("S3 isExist e.getMessage()===" + e.getMessage());
            if (e.getMessage().startsWith("Not Found")) {
                return false;
            }

        }
        throw new RuntimeException("S3 isExist system error!!!");
    }


    /**
     * 删除文件
     *
     * @param remoteFileName 文件名
     */
    public static void delFromS3(String remoteFileName) {
        S3.deleteObject(BUCKET_NAME, remoteFileName);
    }

    /**
     * <h2>返回指定路径前缀下文件的总数,含子文件夹下的文件</h2>
     *
     * @param prefix 路径前缀
     * @return int     返回指定路径下文件的总数
     */
    public static int listFileCountFromS3(String prefix) {
        final ObjectListing objectListing = S3.listObjects(BUCKET_NAME, prefix);
        return objectListing.getObjectSummaries().size();
    }

    /**
     * <h2>第一步，初始化，声明下面将有一个 Multipart Upload</h2>
     *
     * @param remoteFileName 上传S3后的路径名称
     * @param md5Str         文件Md5值
     * @return java.lang.String 生成一个Multipart UploadId
     */
    private static String initMultipartUploadRequest(String remoteFileName, String md5Str) {
        // 第一步，初始化，声明下面将有一个 Multipart Upload
        InitiateMultipartUploadRequest initRequest = new InitiateMultipartUploadRequest(BUCKET_NAME, remoteFileName);
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setSSEAlgorithm(ObjectMetadata.AES_256_SERVER_SIDE_ENCRYPTION); // 声明服务端加密
        //分段上传时，ETag不再存储对象的md5值，这里自定义属性存储
        Map<String, String> map = new HashMap<>();
        map.put("ObjectMd5", md5Str);
        objectMetadata.setUserMetadata(map);
        initRequest.setObjectMetadata(objectMetadata);
        // 初始化一个Multipart Upload请求
        InitiateMultipartUploadResult initResponse = S3.initiateMultipartUpload(initRequest);
        // 返回 Multipart UploadId用于后续处理
        return initResponse.getUploadId();
    }

    /**
     * <h2>第三步，完成上传，合并分段</h2>
     *
     * @param remoteFileName 上传S3后的路径名称
     * @param uploadId       Multipart UploadId
     * @param partETags      每一段 PartETag 的 List
     */
    private static void complete(String remoteFileName, String uploadId, List<PartETag> partETags) {
        // 第三步，完成上传，合并分段
        CompleteMultipartUploadRequest compRequest = new CompleteMultipartUploadRequest(BUCKET_NAME, remoteFileName, uploadId, partETags);
        S3.completeMultipartUpload(compRequest);
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
            filePosition += Math.min(MIN_PART_SIZE, (fileSize - filePosition));
        }
        log.info("总大小:{} bytes，约为:{} MB，分为{}段", fileSize, new BigDecimal(fileSize).divide(new BigDecimal(1024 * 1024), 2, RoundingMode.HALF_UP), positions.size());
        return positions;
    }

    /**
     * 进度监听器
     */
    private static ProgressListener progressListener(String remoteFileName, String uploadId, int partNumber, int partSize) {
        return event -> {
            // 开始上传
            if (ProgressEventType.TRANSFER_PART_STARTED_EVENT == event.getEventType()) {
                log.info("remoteFileName={},uploadId={},uploading part NO.{}{}", remoteFileName, uploadId, partNumber, " [partSize=" + partSize + " bytes]");
            }
            // 上传完成
            else if (ProgressEventType.TRANSFER_PART_COMPLETED_EVENT == event.getEventType()) {
                log.info("remoteFileName={},uploadId={},part NO.{} has been uploaded.{}", remoteFileName, uploadId, partNumber, " [partSize=" + partSize + " bytes]");
            }
            // 上传过程中
            else {
                displayProgress(uploadId, partNumber, event.getBytesTransferred(), partSize);
            }
        };
    }

    /**
     * <h2>每一段上传</h2>
     * <p>
     * 如果小于5M就是最后一段的上传
     *
     * @param bs             本段要上传的文件字节数组，并非整个文件的字节数组
     * @param remoteFileName 保存到S3的路径
     * @param uploadId       Multipart UploadId
     * @param partNumber     段数，就是第几段，这里是从1开始
     */
    private static PartETag getUploadPartRequest(byte[] bs, String remoteFileName, String uploadId, int partNumber) {
        long time1 = System.currentTimeMillis();
        int partSize = bs.length;
        UploadPartRequest uploadRequest = new UploadPartRequest()
                .withBucketName(BUCKET_NAME)                         // 存储桶
                .withKey(remoteFileName)                            // 保存到S3的路径
                .withUploadId(uploadId)                             // Multipart UploadId
                .withPartNumber(partNumber)                         // 段数,从1开始
                .withInputStream(new ByteArrayInputStream(bs))      // 本次要上传的文件流
                .withGeneralProgressListener(progressListener(remoteFileName, uploadId, partNumber, partSize));

        // 如果小于5M就是最后一段的上传
        if (partSize < MIN_PART_SIZE) {
            uploadRequest.withPartSize(partSize);
            uploadRequest.withLastPart(true);  // 最后一段上传要设置标志位
        } else {
            uploadRequest.withPartSize(MIN_PART_SIZE);
        }

        final PartETag partETag = S3.uploadPart(uploadRequest).getPartETag();

        long time2 = System.currentTimeMillis();
        log.info("remoteFileName={},uploadId={},第{}段上传完成，耗时：{}ms.{}", remoteFileName, uploadId, partNumber, (time2 - time1), " [partSize=" + partSize + " bytes,约等于 " + new BigDecimal(partSize).divide(new BigDecimal(1024 * 1024), 2, RoundingMode.HALF_UP) + " MB]");
        return partETag;
    }


    /**
     * 保存上传过程进度信息
     */
    private static Map<String, Map<Integer, Long>> progressContext = new HashMap<>();

    /**
     * <h2>上传过程中打印进度信息</h2>
     *
     * @param uploadId         Multipart UploadId
     * @param partNumber       段号，从1开始
     * @param bytesTransferred 监听器监听到的已经上传了的文件大小，并非本段的字节数，因为每段也是一点点上传到S3的
     * @param partSize         partSize文件流长度
     */
    private static void displayProgress(String uploadId, Integer partNumber, Long bytesTransferred, Integer partSize) {
        if (bytesTransferred != 0) {
            Map<Integer, Long> map;
            if (progressContext.containsKey(uploadId)) {
                map = progressContext.get(uploadId);
            } else {
                map = new HashMap<>();
                progressContext.put(uploadId, map);
            }

            if (map.containsKey(partNumber)) {
                map.put(partNumber, bytesTransferred + map.get(partNumber));
            } else {
                map.put(partNumber, bytesTransferred);
            }

            if ((map.get(partNumber) % (1024 * 1024)) == 0) {
                log.info("uploadId: " + uploadId + ", part NO." + partNumber
                        + ", partSize: " + new BigDecimal(partSize).divide(new BigDecimal(1024 * 1024), 2, RoundingMode.HALF_UP) + " MB"
                        + ", uploadedSize: " + (map.get(partNumber) / (1024 * 1024)) + " MB");

            }
        }
    }


    /**
     * <h2>异步上传</h2>
     *
     * @param bytes          文件字节数组
     * @param remoteFileName 保存到S3的路径
     * @param uploadId       Multipart UploadId
     * @param partETags      分段上传的 partETag List
     * @param positions      分段信息，每个分段的起始字节位置
     */
    private static void uploadAsync(byte[] bytes, String remoteFileName, String uploadId, List<PartETag> partETags, List<Long> positions) {
        //声明线程池
        ExecutorService exec = Executors.newFixedThreadPool(10);
        for (int i = 0; i < positions.size(); i++) {
            int finalI = i;
            // 多线程上传
            exec.execute(() -> {
                byte[] bs;
                // 计算每段要发送的字节数组
                if (finalI == positions.size() - 1) {
                    bs = Arrays.copyOfRange(bytes, positions.get(finalI).intValue(), bytes.length);
                } else {
                    bs = Arrays.copyOfRange(bytes, positions.get(finalI).intValue(), positions.get(finalI + 1).intValue());
                }
                // 上传本段字节数组
                int partNumber = finalI + 1;
                partETags.add(getUploadPartRequest(bs, remoteFileName, uploadId, partNumber));
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
     * <h2>同步上传</h2>
     *
     * @param bytes          文件字节数组
     * @param remoteFileName 保存到S3的路径
     * @param uploadId       Multipart UploadId
     * @param partETags      分段上传的 partETag List
     * @param positions      分段信息，每个分段的起始字节位置
     */
    private static void uploadSync(byte[] bytes, String remoteFileName, String uploadId, List<PartETag> partETags, List<Long> positions) {
        for (int i = 0; i < positions.size(); i++) {
            int finalI = i;
            byte[] bs;
            if (finalI == positions.size() - 1) {
                bs = Arrays.copyOfRange(bytes, positions.get(finalI).intValue(), bytes.length);
            } else {
                bs = Arrays.copyOfRange(bytes, positions.get(finalI).intValue(), positions.get(finalI + 1).intValue());
            }
            int partNumber = finalI + 1;
            partETags.add(getUploadPartRequest(bs, remoteFileName, uploadId, partNumber));
        }
    }

    /**
     * <h2>发生异常，中止多部分上传</h2>
     *
     * @param remoteFileName 保存到S3的路径名称
     * @param uploadId       Multipart UploadId
     */
    private static void abortMultipartUpload(String remoteFileName, String uploadId) {
        S3.abortMultipartUpload(new AbortMultipartUploadRequest(BUCKET_NAME, remoteFileName, uploadId));
    }


    /**
     * <h2>分段上传</h2>
     *
     * @param file           文件
     * @param remoteFileName 保存到S3的路径名称
     * @param async          是否为异步上传  true：异步  false：同步
     */
    public static void uploadByMultipartToS3(File file, String remoteFileName, boolean async) throws IOException {
        final byte[] bytes = MyFileUtil.fileToBytes(file);
        uploadByMultipartToS3(bytes, remoteFileName, async);
    }

    /**
     * <h2>分段上传</h2>
     *
     * @param inputStream    文件流
     * @param remoteFileName 保存到S3的路径名称
     * @param async          是否为异步上传  true：异步  false：同步
     */
    public static void uploadByMultipartToS3(InputStream inputStream, String remoteFileName, boolean async) throws IOException {
        final byte[] bytes = IOUtils.toByteArray(inputStream);
        uploadByMultipartToS3(bytes, remoteFileName, async);
    }

    /**
     * <h2>分段上传</h2>
     *
     * @param bytes          文件字节数组
     * @param remoteFileName 保存到S3的路径名称
     * @param async          是否为异步上传  true：异步  false：同步
     */
    public static void uploadByMultipartToS3(byte[] bytes, String remoteFileName, boolean async) throws IOException {

        //小于5M不进行分段上传
        if (bytes.length < MIN_PART_SIZE) {
            uploadToS3(bytes, remoteFileName);
        } else {
            // 创建一个列表保存所有分段的 PartETag, 在分段完成后会用到
            List<PartETag> partETags = Collections.synchronizedList(new ArrayList<>());
            // 第一步，初始化，声明下面将有一个 Multipart Upload
            final String fileMD5 = MyFileUtil.getBytesMD5(bytes);
            // 获取Multipart UploadId
            final String uploadId = initMultipartUploadRequest(remoteFileName, fileMD5);
            long begin = System.currentTimeMillis();
            try {
                // 得到总共的段数，和分段后每个段的开始上传的字节位置
                List<Long> positions = positions(bytes.length);
                // 第二步，分段上传
                if (async) {
                    // 异步上传
                    uploadAsync(bytes, remoteFileName, uploadId, partETags, positions);
                } else {
                    // 同步上传
                    uploadSync(bytes, remoteFileName, uploadId, partETags, positions);
                }
                // 第三步，完成上传，合并分段
                complete(remoteFileName, uploadId, partETags);
            } catch (Exception e) {
                // 发生异常，中止多部分上传
                abortMultipartUpload(remoteFileName, uploadId);
                log.error("Failed to upload, " + e.getMessage());
            }
            long end = System.currentTimeMillis();
            log.info("remoteFileName={},uploadId={},fileSize={} bytes,总上传耗时：{}ms", remoteFileName, uploadId, bytes.length, (end - begin));
        }
    }


    /*
    分段下载
     */

    /**
     * <h2>获取文件指定字节范围的流</h2>
     *
     * @param remoteFileName 文件名
     * @param start          开始字节数
     * @param end            结束字节数
     * @return java.io.InputStream
     */
    private static InputStream getSubsectionInputStream(String remoteFileName, long start, long end) {
        InputStream is = null;
        try {
            GetObjectRequest getObjectRequest = new GetObjectRequest(BUCKET_NAME, remoteFileName);
            // 设定获取文件流的开始字节数和结束字节数
            getObjectRequest.setRange(start, end);
            S3Object s3Object = S3.getObject(getObjectRequest);
            if (s3Object != null) {
                is = s3Object.getObjectContent();
            }
        } catch (Exception e) {
            log.error("获取指定字节范围的流时失败", e);
        }
        log.info("remoteFileName= " + remoteFileName + " result=" + (is == null ? "false" : "true"));
        return is;
    }

    /**
     * <h2>获取文件指定字节范围的流并写到输出流中</h2>
     *
     * @param remoteFileName 文件名
     * @param start          开始字节数
     * @param end            结束字节数
     * @param os             输出流
     */
    public static void downloadSubsectionToOutputStream(String remoteFileName, long start, long end, OutputStream os) {
        try (InputStream inputStream = getSubsectionInputStream(remoteFileName, start, end)) {
            long length = end - start + 1;
            MyIOUtil.copyDataFromInputStreamToOutputStream(inputStream, os, length);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * <h2>获取文件指定字节范围的流并写到输出流中后关闭输出流</h2>
     *
     * @param remoteFileName 文件名
     * @param start          开始字节数
     * @param end            结束字节数
     * @param os             输出流
     */
    public static void downloadSubsectionAndCloseOutputStream(String remoteFileName, long start, long end, OutputStream os) {
        try {
            downloadSubsectionToOutputStream(remoteFileName, start, end, os);
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
     * 分段下载
     *
     * @param remoteFileName 文件名称
     * @param outDirPath     文件输出目录
     * @return String 下载后的文件路径
     */
    public static String downloadSubsection(String remoteFileName, String outDirPath) throws IOException {
        final ObjectMetadata objectMetadata = getObjectMetadata(remoteFileName);
        final long contentLength = objectMetadata.getContentLength();
        //小于5M不进行分段下载
        if (contentLength < MIN_PART_SIZE) {
            return downFromS3(remoteFileName, outDirPath);
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
                        downloadSubsectionToOutputStream(remoteFileName, positions.get(finalI - 1), positions.get(finalI) - 1, fileOutputStream);
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

            String fileName = remoteFileName;
            if (remoteFileName.contains("/")) {
                fileName = remoteFileName.substring(remoteFileName.lastIndexOf("/") + 1);
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
        String remoteFileName = "cover/node-v16.17.0-darwin-x64.tar.gz";

        if (isExistFromS3(remoteFileName)) {
            delFromS3(remoteFileName);
        }

        //分段上传
        uploadByMultipartToS3(new File(filePath), remoteFileName, true);


        // 分段下载
        String outPath = "/Users/hanqf/Desktop/download/";
        downloadSubsection(remoteFileName, outPath);

    }
}
