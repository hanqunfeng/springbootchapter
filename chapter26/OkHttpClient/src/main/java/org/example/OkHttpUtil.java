package org.example;


import com.alibaba.fastjson.JSONArray;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okio.BufferedSink;
import okio.GzipSink;
import okio.Okio;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.URLEncoder;
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
    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(5L, TimeUnit.SECONDS) //连接超时时间，5秒
            .readTimeout(5L, TimeUnit.SECONDS) //读超时时间，5秒
            .writeTimeout(5L, TimeUnit.SECONDS) //写超时时间，5秒
            .followRedirects(true) //设置是否允许重定向，默认true
            //注意拦截器的顺序
            //.addInterceptor(new GzipRequestInterceptor()) //开启gzip压缩，支持对流或Json进行gzip压缩，服务端需要支持解压缩
            .addInterceptor(new RetryIntercepter()) //重试拦截器，默认3次
            .addInterceptor(new HeadersLoggingInterceper()) //header拦截器
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
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                log.info("Get responseResult：", e);
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseResult = null;
                if (response.isSuccessful()) {
                    responseResult = Objects.requireNonNull(response.body()).string();
                }
                Headers headers = response.headers();
                StringBuilder stringBuffer = new StringBuilder();
                for (int i = 0; i < headers.size(); i++) {
                    stringBuffer.append(headers.name(i)).append(":").append(headers.value(i)).append(",");
                }
                log.info(String.format("响应头信息: [%s]", stringBuffer.toString()));
                log.info(String.format("响应结果：%s", responseResult));
            }
        });
    }

    /**
     * <p>请求的执行方法，需要提前封装好Request对象，如请求url和请求参数</p>
     *
     * @param request
     * @return java.lang.String
     * @author hanqf
     * 2020/4/18 23:47
     */
    private static String execute(Request request, int... codes) {
        String responseResult = null;
        try {
            long t1 = System.nanoTime();//请求发起的时间
            Response response = client.newCall(request).execute();
            log.info("状态码::[{}]", response.code());
            if (response.isSuccessful() || (codes != null && ArrayUtils.contains(codes, response.code()))) {
                responseResult = Objects.requireNonNull(response.body()).string();
                //byte[] bytes = response.body().bytes();
                //responseResult = new String(bytes,"utf-8");
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
     * <p>get请求</p>
     *
     * @param url 请求url
     * @return java.lang.String
     * @author hanqf
     * 2020/4/18 23:48
     */
    public static String get(String url) {
        return get(url, new HashMap<>());

    }

    /**
     * <p>get请求</p>
     *
     * @param url
     * @param params
     * @return java.lang.String
     * @author hanqf
     * 2020/4/20 16:40
     */
    public static String get(String url, Map<String, Object> params) {
        if (params.size() > 0) {
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

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        log.info(String.format("请求地址: [%s]", request.url()));
        if (params.size() > 0) {
            JSONArray jArray = new JSONArray();
            jArray.add(params);
            log.info(String.format("请求参数: %s", jArray.toJSONString()));
        }

        //executeAsync(request);
        return execute(request);

    }

    public static byte[] getBytes(String url, Map<String, Object> params) {
        if (params.size() > 0) {
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

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        log.info(String.format("请求地址: [%s]", request.url()));
        if (params.size() > 0) {
            JSONArray jArray = new JSONArray();
            jArray.add(params);
            log.info(String.format("请求参数: %s", jArray.toJSONString()));
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
    public static String post(String url) {
        return post(url, new HashMap<>());
    }

    /**
     * <p>post请求</p>
     * form请求
     *
     * @param url    请求url
     * @param params 请求参数
     * @return java.lang.String
     * @author hanqf
     * 2020/4/18 23:49
     */
    public static String post(String url, Map<String, Object> params, int... codes) {
        //请求头会加入：application/x-www-form-urlencoded
        FormBody.Builder builder = new FormBody.Builder();
        for (String key : params.keySet()) {
            builder.add(key, params.get(key).toString());
        }
        RequestBody requestBody = builder.build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        log.info(String.format("请求地址: [%s]", request.url()));
        if (params.size() > 0) {
            JSONArray jArray = new JSONArray();
            jArray.add(params);
            log.info(String.format("请求参数: %s", jArray.toJSONString()));
        }
        log.info(String.format("请求类型: %s", Objects.requireNonNull(Objects.requireNonNull(request.body()).contentType()).toString()));

        return execute(request,codes);

    }

    public static byte[] postBytes(String url, Map<String, Object> params) {
        //请求头会加入：application/x-www-form-urlencoded
        FormBody.Builder builder = new FormBody.Builder();
        for (String key : params.keySet()) {
            builder.add(key, params.get(key).toString());
        }
        RequestBody requestBody = builder.build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        log.info(String.format("请求地址: [%s]", request.url()));
        if (params.size() > 0) {
            JSONArray jArray = new JSONArray();
            jArray.add(params);
            log.info(String.format("请求参数: %s", jArray.toJSONString()));
        }
        log.info(String.format("请求类型: %s", Objects.requireNonNull(Objects.requireNonNull(request.body()).contentType()).toString()));

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
    public static String post(String url, String json) {

        MediaType mediaType = MediaType.parse("application/json; charset=utf8");
        RequestBody requestBody = RequestBody.create(json, mediaType);
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        log.info(String.format("请求地址: [%s]", request.url()));
        log.info(String.format("请求参数: %s", json));
        log.info(String.format("请求类型: %s", Objects.requireNonNull(Objects.requireNonNull(request.body()).contentType()).toString()));
        return execute(request);
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
    public static String post(String url, byte[] bytes) {

        MediaType mediaType = MediaType.parse("application/octet-stream");
        RequestBody requestBody = null;
        requestBody = RequestBody.create(bytes, mediaType);

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        log.info(String.format("请求地址: [%s]", request.url()));
        log.info(String.format("请求类型: %s", Objects.requireNonNull(Objects.requireNonNull(request.body()).contentType()).toString()));
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
    public static String post(String url, InputStream is) {
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
        return post(url, bytes);
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
    public static String post(String url, File[] files) {
        return post(url, new HashMap<>(), files);
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
    public static String post(String url, Map<String, Object> params, File[] files) {

        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);
        int i = 1;
        try {
            String filesKey = "files";
            for (File file : files) {
                //URLEncoder.encode(file.getName(), "utf-8") //中文文件名使用encode，服务端使用decode解析
                builder.addFormDataPart(filesKey, URLEncoder.encode(file.getName(), "utf-8"),
                        RequestBody.create(file, MediaType.parse("multipart/form-data")));
                i++;
            }

            for (String key : params.keySet()) {
                builder.addFormDataPart(key, params.get(key).toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        RequestBody requestBody = builder.build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        log.info(String.format("请求地址: [%s]", request.url()));
        if (params != null && params.size() > 0) {
            JSONArray jArray = new JSONArray();
            jArray.add(params);
            log.info(String.format("请求参数: %s", jArray.toJSONString()));
        }
        if (files != null && files.length > 0) {
            JSONArray jArray = new JSONArray();
            jArray.add(files);
            log.info(String.format("请求参数: %s", jArray.toJSONString()));
        }
        log.info(String.format("请求类型: %s", Objects.requireNonNull(Objects.requireNonNull(request.body()).contentType()).toString()));
        return execute(request);
    }


    public static String postImage(String url, String json, File[] files) {

        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);
        int i = 1;
        try {
            String filesKey = "files";
            for (File file : files) {
                //URLEncoder.encode(file.getName(), "utf-8") //中文文件名使用encode，服务端使用decode解析
                builder.addFormDataPart(filesKey, URLEncoder.encode(file.getName(), "utf-8"),
                        RequestBody.create(file, MediaType.parse("multipart/form-data")));
                i++;
            }

            final RequestBody requestBody = RequestBody.create(json, MediaType.parse("application/json; charset=utf8"));
            builder.addFormDataPart("imageInfo", "imageInfo", requestBody);


        } catch (Exception e) {
            e.printStackTrace();
        }

        RequestBody requestBody = builder.build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        log.info(String.format("请求地址: [%s]", request.url()));

        log.info(String.format("请求参数: %s", json));

        if (files != null && files.length > 0) {
            JSONArray jArray = new JSONArray();
            jArray.add(files);
            log.info(String.format("请求参数: %s", jArray.toJSONString()));
        }
        log.info(String.format("请求类型: %s", Objects.requireNonNull(Objects.requireNonNull(request.body()).contentType()).toString()));
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
            if (originalRequest.body() == null || originalRequest.header("Content-Encoding") != null) {
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
                public void writeTo(@NotNull BufferedSink sink) throws IOException {
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
        public Response intercept(@NotNull Chain chain) throws IOException {
            Request request = chain.request();
            Headers headers = request.headers();
            StringBuilder stringBuffer = new StringBuilder();
            for (int i = 0; i < headers.size(); i++) {
                stringBuffer.append(headers.name(i)).append(":").append(headers.value(i)).append(",");
            }
            log.info(String.format("请求头信息: [%s]", stringBuffer.toString()));
            return chain.proceed(request);
        }
    }
}
