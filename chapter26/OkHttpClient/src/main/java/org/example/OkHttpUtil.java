package org.example;


import com.alibaba.fastjson.JSONArray;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okio.BufferedSink;
import okio.GzipSink;
import okio.Okio;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>OkHttp工具类</p>
 * Created by hanqf on 2020/4/18 21:54.
 */

@Slf4j
public class OkHttpUtil {

    /**
     * 获得OkHttpClient客户端
     */
    private static OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(5L, TimeUnit.SECONDS) //连接超时时间，5秒
            .readTimeout(5L, TimeUnit.SECONDS) //读超时时间，5秒
            .writeTimeout(5L, TimeUnit.SECONDS) //写超时时间，5秒
            .followRedirects(true) //设置是否允许重定向，默认true
            //注意拦截器的顺序
            .addInterceptor(new GzipRequestInterceptor()) //开启gzip压缩，支持对流或Json进行gzip压缩，服务端需要支持解压缩
            .addInterceptor(new RetryIntercepter()) //重试拦截器，默认3次
            .addInterceptor(new HeadersLoggingInterceper()) //header拦截器
            .build();
    
    
    /**
     * <p>异步调用</p>
     * @author hanqf
     * 2020/4/22 20:37
     * @param request
     */
    private static void executeAsync(Request request){
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
                    responseResult = response.body().string();
                }
                Headers headers = response.headers();
                log.info(String.format("响应头信息: [%s]", headers.toString()));
                log.info(String.format("响应结果：%s",responseResult));
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
    private static String execute(Request request) {
        String responseResult = null;
        try {
            long t1 = System.nanoTime();//请求发起的时间
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                responseResult = response.body().string();
            }
            long t2 = System.nanoTime();//收到响应的时间
            Headers headers = response.headers();

            log.info(String.format("响应头信息: [%s]", headers.toString()));

            log.info(String.format("执行时间: [%.1fms]", (t2 - t1) / 1e6d));
        } catch (IOException e) {
            log.info("Get responseResult：", e);
            e.printStackTrace();
        }
        return responseResult;

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
            StringBuffer stringBuffer = new StringBuffer();
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
        if (params != null && params.size() > 0) {
            JSONArray jArray = new JSONArray();
            jArray.add(params);
            log.info(String.format("请求参数: %s", jArray.toJSONString()));
        }

        //executeAsync(request);
        return execute(request);

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
    public static String post(String url, Map<String, Object> params) {
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
        if (params != null && params.size() > 0) {
            JSONArray jArray = new JSONArray();
            jArray.add(params);
            log.info(String.format("请求参数: %s", jArray.toJSONString()));
        }
        log.info(String.format("请求类型: %s", request.body().contentType().toString()));

        return execute(request);

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
        log.info(String.format("请求类型: %s", request.body().contentType().toString()));
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
        log.info(String.format("请求类型: %s", request.body().contentType().toString()));
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
        log.info(String.format("请求类型: %s", request.body().contentType().toString()));
        return execute(request);
    }


    /**
     * This interceptor compresses the HTTP request body. Many webservers can't handle this!
     */
    private static class GzipRequestInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request originalRequest = chain.request();
            if (originalRequest.body() == null || originalRequest.header("Content-Encoding") != null) {
                return chain.proceed(originalRequest);
            }

            MediaType mediaType = originalRequest.body().contentType();
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

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            Response response = chain.proceed(request);
            while (!response.isSuccessful() && retryNum < maxRetry) {
                retryNum++;
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
            log.info(String.format("请求头信息: %s", request.headers().toString()));
            return chain.proceed(request);
        }
    }
}
