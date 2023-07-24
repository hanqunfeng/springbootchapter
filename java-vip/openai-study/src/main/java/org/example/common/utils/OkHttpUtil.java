package org.example.common.utils;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okio.BufferedSink;
import okio.GzipSink;
import okio.Okio;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;

/**
 * <p>OkHttp工具类</p>
 * Created by hanqf on 2020/4/18 21:54.
 */

@Slf4j
public class OkHttpUtil {
    /**
     * 获得OkHttpClient客户端
     */
    public static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(50L, TimeUnit.SECONDS) //连接超时时间，50秒
            .readTimeout(300L, TimeUnit.SECONDS) //读超时时间，300秒
            .writeTimeout(300L, TimeUnit.SECONDS) //写超时时间，300秒
            .followRedirects(true) //设置是否允许重定向，默认true
            //注意拦截器的顺序
            //header中要设置Content-Encoding=gzip才会开启压缩
            .addInterceptor(new GzipRequestInterceptor()) //开启gzip压缩，支持对流或Json进行gzip压缩，服务端需要支持解压缩
            .addInterceptor(new RetryIntercepter()) //重试拦截器，默认3次
            .addInterceptor(new HeadersLoggingInterceper()) //header拦截器

            //如果调用接口是https协议，并且没有对证书进行认证，则需要开启该配置，强制验证通过
            .hostnameVerifier(NoopHostnameVerifier.INSTANCE)
            .build();


    /**
     * <p>异步调用</p>
     *
     * @param request
     * @author hanqf
     * 2020/4/22 20:37
     */
    private static void executeAsync(Request request) {
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                log.info("Get responseResult：", e);
                e.printStackTrace();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseResult = null;
                if (response.isSuccessful()) {
                    responseResult = Objects.requireNonNull(response.body()).string();
                }
                Headers headers = response.headers();
                StringBuilder stringBuffer = new StringBuilder();
                for (int i = 0; i < headers.size(); i++) {
                    stringBuffer.append(headers.name(i)).append(":").append(headers.value(i)).append(",");
                }
                log.info(String.format("响应头信息: [%s]", stringBuffer));
                log.info(String.format("响应结果：%s", responseResult));
            }
        });
    }

    /**
     * <p>请求的执行方法，需要提前封装好Request对象，如请求url和请求参数</p>
     *
     * @param request
     * @param codes   可以认为响应成功的状态码
     * @return java.lang.String
     * @author hanqf
     * 2020/4/18 23:47
     */
    private static ResponseResult execute(Request request, int... codes) {
        ResponseResult responseResult = new ResponseResult();
        String content = null;
        try {
            long t1 = System.nanoTime();//请求发起的时间
            Response response = client.newCall(request).execute();
            log.info("响应状态码::[{}]", response.code());
            responseResult.setCode(response.code());
            if (response.isSuccessful() || (codes != null && ArrayUtils.contains(codes, response.code()))) {
                content = Objects.requireNonNull(response.body()).string();
                responseResult.setContent(content);
                log.info(String.format("响应信息: [%s]", content));
            }
            long t2 = System.nanoTime();//收到响应的时间
            Headers headers = response.headers();

            StringBuilder stringBuffer = new StringBuilder();
            for (int i = 0; i < headers.size(); i++) {
                stringBuffer.append(headers.name(i)).append(":").append(headers.value(i)).append(",");
            }
            log.info(String.format("响应头信息: [%s]", stringBuffer));

            log.info(String.format("执行时间: [%.1fms]", (t2 - t1) / 1e6d));
        } catch (IOException e) {
            log.info("Get responseResult：", e);
            e.printStackTrace();
        }

        return responseResult;

    }

    private static byte[] executeBytes(Request request) {
        byte[] bytes = null;
        try {
            long t1 = System.nanoTime();//请求发起的时间
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                bytes = Objects.requireNonNull(response.body()).bytes();

                //判断是否需要解压，即服务器返回是否经过了gzip压缩--start
                String responseHeader = response.header("Content-Encoding");
                if (responseHeader != null && responseHeader.contains("gzip")) {
                    GZIPInputStream gzipInputStream = null;
                    ByteArrayOutputStream out = null;
                    try {
                        gzipInputStream = new GZIPInputStream(new ByteArrayInputStream(bytes));
                        out = new ByteArrayOutputStream();
                        byte[] buffer = new byte[1024];
                        int offset = -1;
                        while ((offset = gzipInputStream.read(buffer)) != -1) {
                            out.write(buffer, 0, offset);
                        }
                        bytes = out.toByteArray();

                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            gzipInputStream.close();
                            out.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                //判断是否需要解压，即服务器返回是否经过了gzip压缩--end

            }
            long t2 = System.nanoTime();//收到响应的时间
            Headers headers = response.headers();
            StringBuilder stringBuffer = new StringBuilder();
            for (int i = 0; i < headers.size(); i++) {
                stringBuffer.append(headers.name(i)).append(":").append(headers.value(i)).append(",");
            }
            log.info(String.format("响应头信息: [%s]", stringBuffer.toString()));

            log.info(String.format("执行时间: [%.1fms]", (t2 - t1) / 1e6d));
        } catch (IOException e) {
            log.info("Get responseResult：", e);
            e.printStackTrace();
        }
        return bytes;

    }


    /**
     * 为请求设置header
     */
    private static Request requestHeaders(Request.Builder builder, Map<String, String> headers) {

        if (headers != null && headers.size() > 0) {
            headers.forEach((k, v) -> builder.addHeader(k, v));
        }

        return builder.build();
    }

    /**
     * <p>get请求</p>
     *
     * @param url 请求url
     * @return java.lang.String
     * @author hanqf
     * 2020/4/18 23:48
     */
    public static ResponseResult get(String url, Map<String, Object> params) {
        return get(url, params, null);
    }

    public static ResponseResult get(String url) {
        return get(url, null, null);
    }

    /**
     * <p>get请求</p>
     *
     * @param url
     * @param params
     * @param headers 请求header
     * @return java.lang.String
     * @author hanqf
     * 2020/4/20 16:40
     */
    public static ResponseResult get(String url, Map<String, Object> params, Map<String, String> headers) {
        if (params != null && params.size() > 0) {
            StringBuilder stringBuffer = new StringBuilder();
            stringBuffer.append(url);
            if (url.contains("?")) {
                stringBuffer.append("&");
            } else {
                stringBuffer.append("?");
            }
            for (String key : params.keySet()) {
                stringBuffer.append(key).append("=").append(params.get(key).toString()).append("&");
            }
            url = stringBuffer.toString();
        }

        Request.Builder builder = new Request.Builder()
                .url(url)
                .get();

        Request request = requestHeaders(builder, headers);

        log.info(String.format("请求地址: [%s]", request.url()));

        if (params != null && params.size() > 0) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                log.info(String.format("请求参数: %s", objectMapper.writeValueAsString(params)));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }

        return execute(request);

    }

    public static byte[] getBytes(String url, Map<String, Object> params, Map<String, String> headers) {

        if (params != null && params.size() > 0) {
            StringBuilder stringBuffer = new StringBuilder();
            stringBuffer.append(url);
            if (url.contains("?")) {
                stringBuffer.append("&");
            } else {
                stringBuffer.append("?");
            }
            for (String key : params.keySet()) {
                stringBuffer.append(key).append("=").append(params.get(key).toString()).append("&");
            }
            url = stringBuffer.toString();
        }

        Request.Builder builder = new Request.Builder()
                .url(url)
                .get();
        Request request = requestHeaders(builder, headers);

        log.info(String.format("请求地址: [%s]", request.url()));

        if (params != null && params.size() > 0) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                log.info(String.format("请求参数: %s", objectMapper.writeValueAsString(params)));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }

        return executeBytes(request);

    }

    /**
     * <p>post请求</p>
     *
     * @param url 请求url
     * @return java.lang.String
     * @author hanqf
     * 2020/4/18 23:48
     */
    public static ResponseResult post(String url) {
        return post(url, new HashMap<>());
    }

    public static ResponseResult post(String url, Map<String, Object> params) {
        return post(url, params, null);
    }

    /**
     * <p>post请求</p>
     * form请求
     *
     * @param url     请求url
     * @param params  请求参数
     * @param headers 请求header
     * @param codes   可以认为响应成功的状态码
     * @return java.lang.String
     * @author hanqf
     * 2020/4/18 23:49
     */
    public static ResponseResult post(String url, Map<String, Object> params, Map<String, String> headers, int... codes) {
        //请求头会加入：application/x-www-form-urlencoded
        FormBody.Builder builder = new FormBody.Builder();
        if (params != null && params.size() > 0) {
            for (String key : params.keySet()) {
                builder.add(key, params.get(key).toString());
            }
        }
        RequestBody requestBody = builder.build();
        Request.Builder post = new Request.Builder()
                .url(url)
                .post(requestBody);
        Request request = requestHeaders(post, headers);

        log.info(String.format("请求地址: [%s]", request.url()));
        if (params != null && params.size() > 0) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                log.info(String.format("请求参数: %s", objectMapper.writeValueAsString(params)));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        log.info(String.format("请求类型: %s", Objects.requireNonNull(Objects.requireNonNull(request.body()).contentType())));

        return execute(request, codes);

    }

    public static byte[] postBytes(String url, Map<String, Object> params, Map<String, String> headers) {
        //请求头会加入：application/x-www-form-urlencoded
        FormBody.Builder builder = new FormBody.Builder();
        if (params != null && params.size() > 0) {
            for (String key : params.keySet()) {
                builder.add(key, params.get(key).toString());
            }
        }
        RequestBody requestBody = builder.build();
        Request.Builder post = new Request.Builder()
                .url(url)
                .post(requestBody);
        Request request = requestHeaders(post, headers);

        log.info(String.format("请求地址: [%s]", request.url()));
        if (params != null && params.size() > 0) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                log.info(String.format("请求参数: %s", objectMapper.writeValueAsString(params)));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        log.info(String.format("请求类型: %s", Objects.requireNonNull(Objects.requireNonNull(request.body()).contentType())));

        return executeBytes(request);

    }


    /**
     * <p>post请求，json请求</p>
     *
     * @param url  请求url
     * @param json json数据
     * @return java.lang.String
     * @author hanqf
     * 2020/4/18 23:50
     */
    public static ResponseResult post(String url, String json, Map<String, String> headers, int... codes) {

        MediaType mediaType = MediaType.parse("application/json; charset=utf8");
        RequestBody requestBody = RequestBody.create(json, mediaType);
        Request.Builder post = new Request.Builder()
                .url(url)
                .post(requestBody);
        Request request = requestHeaders(post, headers);
        log.info(String.format("请求地址: [%s]", request.url()));
        log.info(String.format("请求参数: %s", json));
        log.info(String.format("请求类型: %s", Objects.requireNonNull(Objects.requireNonNull(request.body()).contentType())));
        return execute(request, codes);
    }

    /**
     * <p>post请求，字节流</p>
     *
     * @param url   请求url
     * @param bytes 字节数组
     * @return java.lang.String
     * @author hanqf
     * 2020/4/18 23:51
     */
    public static ResponseResult post(String url, byte[] bytes, Map<String, String> headers) {

        MediaType mediaType = MediaType.parse("application/octet-stream");
        RequestBody requestBody = null;
        requestBody = RequestBody.create(bytes, mediaType);

        Request.Builder post = new Request.Builder()
                .url(url)
                .post(requestBody);
        Request request = requestHeaders(post, headers);

        log.info(String.format("请求地址: [%s]", request.url()));
        log.info(String.format("请求类型: %s", Objects.requireNonNull(Objects.requireNonNull(request.body()).contentType())));
        return execute(request);
    }

    /**
     * <p>post请求，字节流</p>
     *
     * @param url
     * @param is
     * @return java.lang.String
     * @author hanqf
     * 2020/4/21 22:45
     */
    public static ResponseResult post(String url, InputStream is, Map<String, String> headers) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int ch;
        byte[] bytes = null;
        try {
            while ((ch = is.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, ch);
            }
            bytes = byteArrayOutputStream.toByteArray();
            byteArrayOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return post(url, bytes, headers);
    }

    /**
     * <p>post请求，文件传输</p>
     *
     * @param url   请求url
     * @param files 文件列表
     * @return java.lang.String
     * @author hanqf
     * 2020/4/18 23:52
     */
    public static ResponseResult post(String url, File[] files) {
        return post(url, new HashMap<>(), files, null);
    }

    /**
     * <p>post请求，文件传输</p>
     *
     * @param url    请求url
     * @param params 参数map
     * @param files  文件列表
     * @return java.lang.String
     * @author hanqf
     * 2020/4/18 23:52
     */
    public static ResponseResult post(String url, Map<String, Object> params, File[] files, Map<String, String> headers) {

        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);

        try {
            String filesKey = "files";
            for (File file : files) {
                //只能判断几种基本类型，例如图片类型：https://blog.csdn.net/sufu1065/article/details/113699472
                String mimeType = Files.probeContentType(file.toPath());
                //URLEncoder.encode(file.getName(), "utf-8") //中文文件名使用encode，服务端使用decode解析
                builder.addFormDataPart(filesKey, URLEncoder.encode(file.getName(), "utf-8"),
                        RequestBody.create(file, MediaType.parse(mimeType)));
            }

            for (String key : params.keySet()) {
                builder.addFormDataPart(key, params.get(key).toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        RequestBody requestBody = builder.build();
        Request.Builder post = new Request.Builder()
                .url(url)
                .post(requestBody);
        Request request = requestHeaders(post, headers);

        log.info(String.format("请求地址: [%s]", request.url()));
        if (params != null && params.size() > 0) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                log.info(String.format("请求参数: %s", objectMapper.writeValueAsString(params)));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        if (files != null && files.length > 0) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                log.info(String.format("请求参数: %s", objectMapper.writeValueAsString(files)));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        log.info(String.format("请求类型: %s", Objects.requireNonNull(Objects.requireNonNull(request.body()).contentType())));
        return execute(request);
    }

    public static ResponseResult postFilesAndJson(String url, String json, String jsonName, File[] files, Map<String, String> headers) {

        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);
        try {
            String filesKey = "files";
            for (File file : files) {
                //只能判断几种基本类型，例如图片类型：https://blog.csdn.net/sufu1065/article/details/113699472
                String mimeType = Files.probeContentType(file.toPath());
                //URLEncoder.encode(file.getName(), "utf-8") //中文文件名使用encode，服务端使用decode解析
                builder.addFormDataPart(filesKey, URLEncoder.encode(file.getName(), "utf-8"),
                        RequestBody.create(file, MediaType.parse(mimeType)));
            }

            RequestBody requestBody = RequestBody.create(json, MediaType.parse("application/json; charset=utf8"));
            builder.addFormDataPart(jsonName, null, requestBody);


        } catch (Exception e) {
            e.printStackTrace();
        }

        RequestBody requestBody = builder.build();
        Request.Builder post = new Request.Builder()
                .url(url)
                .post(requestBody);
        Request request = requestHeaders(post, headers);

        log.info(String.format("请求地址: [%s]", request.url()));

        log.info(String.format("请求参数: %s", json));

        if (files != null && files.length > 0) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                log.info(String.format("请求参数: %s", objectMapper.writeValueAsString(files)));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        log.info(String.format("请求类型: %s", Objects.requireNonNull(Objects.requireNonNull(request.body()).contentType())));
        return execute(request);
    }

    public static ResponseResult postFileAndJson(String url, String json, String jsonName, File file, String fileName, Map<String, String> headers) {

        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);
        try {

            //只能判断几种基本类型，例如图片类型：https://blog.csdn.net/sufu1065/article/details/113699472
            String mimeType = Files.probeContentType(file.toPath());
            //URLEncoder.encode(file.getName(), "utf-8") //中文文件名使用encode，服务端使用decode解析
            builder.addFormDataPart(fileName, URLEncoder.encode(file.getName(), "utf-8"),
                    RequestBody.create(file, MediaType.parse(mimeType)));

            RequestBody requestBody = RequestBody.create(json, MediaType.parse("application/json; charset=utf8"));
            builder.addFormDataPart(jsonName, null, requestBody);


        } catch (Exception e) {
            e.printStackTrace();
        }

        RequestBody requestBody = builder.build();
        Request.Builder post = new Request.Builder()
                .url(url)
                .post(requestBody);
        Request request = requestHeaders(post, headers);

        log.info(String.format("请求地址: [%s]", request.url()));

        log.info(String.format("请求参数: %s", json));

        if (file != null) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                log.info(String.format("请求参数: %s", objectMapper.writeValueAsString(file)));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        log.info(String.format("请求类型: %s", Objects.requireNonNull(Objects.requireNonNull(request.body()).contentType())));
        return execute(request);
    }


    /**
     * This interceptor compresses the HTTP request body. Many webservers can't handle this!
     */
    private static class GzipRequestInterceptor implements Interceptor {
        @NotNull
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request originalRequest = chain.request();
            if (originalRequest.body() == null || !"gzip".equals(originalRequest.header("Content-Encoding"))) {
                return chain.proceed(originalRequest);
            }

            MediaType mediaType = Objects.requireNonNull(originalRequest.body()).contentType();
            //对流和json开启压缩
            if (mediaType != null && ("application/octet-stream".equals(mediaType.toString()) || "application/json; charset=utf8".equals(mediaType.toString()))) {
                Request compressedRequest = originalRequest.newBuilder()
                        .header("Content-Encoding", "gzip")
                        .method(originalRequest.method(), gzip(originalRequest.body()))
                        .build();
                return chain.proceed(compressedRequest);
            }
            return chain.proceed(originalRequest);
        }


        private RequestBody gzip(final RequestBody body) {
            return new RequestBody() {
                @Override
                public MediaType contentType() {
                    return body.contentType();
                }

                @Override
                public long contentLength() {
                    return -1; // We don't know the compressed length in advance!
                }

                @Override
                public void writeTo(BufferedSink sink) throws IOException {
                    BufferedSink gzipSink = Okio.buffer(new GzipSink(sink));
                    body.writeTo(gzipSink);
                    gzipSink.close();
                }
            };
        }
    }

    /**
     * <p>重试拦截器</p>
     */
    private static class RetryIntercepter implements Interceptor {

        private int maxRetry = 3;//最大重试次数，默认3次
        private int retryNum = 0;

        public RetryIntercepter() {

        }

        public RetryIntercepter(int maxRetry) {
            this.maxRetry = maxRetry;
        }

        @NotNull
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            Response response = chain.proceed(request);
            while (!response.isSuccessful() && retryNum < maxRetry) {
                retryNum++;
                response.close();
                response = chain.proceed(request);
            }
            return response;
        }
    }

    /**
     * <p>Headers拦截器</p>
     */
    private static class HeadersLoggingInterceper implements Interceptor {

        @NotNull
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            Headers headers = request.headers();
            StringBuilder stringBuffer = new StringBuilder();
            for (int i = 0; i < headers.size(); i++) {
                stringBuffer.append(headers.name(i)).append(":").append(headers.value(i)).append(",");
            }
            log.info(String.format("请求头信息: [%s]", stringBuffer));
            return chain.proceed(request);
        }
    }


    @Data
    public static class ResponseResult{
        private Integer code;
        private String content;
    }

    public static void main(String[] args) {
        String url = "http://127.0.0.1:2376/build?t=640863575136.dkr.ecr.us-west-2.amazonaws.com/novel-api:0.1.5&buildargs=%7B%22SOURCE_PROJECT_JAR%22%3A%22novel-api-0.1.5.jar%22%7D";
        String file = "/Users/hanqf/idea_workspaces/lx-novel/novel-web-parent/novel-api/target/DockerDir/Dockerfile.tar.gz";
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/tar");
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            final ResponseResult responseResult = OkHttpUtil.post(url, fileInputStream, headers);
            System.out.println(responseResult.getContent());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
