package org.example;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

/**
 * <p>HttpClient工具类</p>
 * Created by hanqf on 2020/4/18 14:54.
 */

@Slf4j
public class HttpClientUtil {
    /**
     * 配置信息
     */
    private static RequestConfig requestConfig = RequestConfig.custom()
            // 设置连接超时时间(单位毫秒)
            .setConnectTimeout(5000)
            // 设置请求超时时间(单位毫秒)
            .setConnectionRequestTimeout(5000)
            // socket读写超时时间(单位毫秒)
            .setSocketTimeout(5000)
            // 设置是否允许重定向(默认为true)
            .setRedirectsEnabled(true)
            //是否启用内容压缩，默认true
            .setContentCompressionEnabled(true)
            .build();
    /**
     * 获得Http客户端
     */
    private static CloseableHttpClient httpClient = HttpClientBuilder.create()
            .setRetryHandler(new DefaultHttpRequestRetryHandler()) //失败重试，默认3次
            .build();

    /**
     * 异步Http客户端
     */
    private static CloseableHttpAsyncClient httpAsyncClient = HttpAsyncClients.custom()
            .setDefaultRequestConfig(requestConfig)
            .build();


    /**
     * <p>异步请求</p>
     *
     * @param httpRequestBase
     * @author hanqf
     * 2020/4/22 21:16
     */
    private static void executeAsync(HttpRequestBase httpRequestBase) {
        httpAsyncClient.start();
        httpAsyncClient.execute(httpRequestBase, new FutureCallback<HttpResponse>() {
            @SneakyThrows
            @Override
            public void completed(HttpResponse httpResponse) {
                log.info(" callback thread id is : " + Thread.currentThread().getId());
                log.info(httpRequestBase.getRequestLine() + "->" + httpResponse.getStatusLine());

                StringBuffer stringBuffer = new StringBuffer();
                for (Header header : httpRequestBase.getAllHeaders()) {
                    stringBuffer.append(header.toString()).append(",");
                }
                log.info(String.format("请求头信息: [%s]", stringBuffer.toString()));


                String responseResult = null;
                HttpEntity responseEntity = httpResponse.getEntity();
                log.debug("响应状态为:" + httpResponse.getStatusLine());
                if (responseEntity != null) {
                    responseResult = EntityUtils.toString(responseEntity, StandardCharsets.UTF_8);
                    log.info("响应内容为:" + responseResult);

                }

                stringBuffer = new StringBuffer();
                for (Header header : httpResponse.getAllHeaders()) {
                    stringBuffer.append(header.toString()).append(",");
                }
                log.info(String.format("响应头信息: [%s]", stringBuffer.toString()));


            }

            @Override
            public void failed(Exception e) {
                log.info(" callback thread id is : " + Thread.currentThread().getId());
                log.info("Get responseResult：", e);
                e.printStackTrace();
            }

            @Override
            public void cancelled() {
                log.info(httpRequestBase.getRequestLine() + " cancelled");
            }
        });
    }


    /**
     * <p>请求的执行方法，需要提前封装好httpRequestBase对象，如请求url和请求参数</p>
     *
     * @param httpRequestBase
     * @return java.lang.String
     * @author hanqf
     * 2020/4/18 22:08
     */
    private static String execute(HttpRequestBase httpRequestBase) {
        log.info(String.format("请求地址: [%s]", httpRequestBase.getURI().toString()));
        log.info(String.format("请求类型: [%s]", httpRequestBase.getMethod()));

        StringBuffer stringBuffer = new StringBuffer();
        for (Header header : httpRequestBase.getAllHeaders()) {
            stringBuffer.append(header.toString()).append(",");
        }
        log.info(String.format("请求头信息: [%s]", stringBuffer.toString()));


        log.info(String.format("请求参数: [%s]", httpRequestBase.getURI().getQuery()));

        String responseResult = null;
        // 响应模型
        CloseableHttpResponse response = null;
        try {
            // 将上面的配置信息 运用到这个Get请求里
            httpRequestBase.setConfig(requestConfig);
            long t1 = System.nanoTime();//请求发起的时间
            response = httpClient.execute(httpRequestBase);
            // 从响应模型中获取响应实体
            HttpEntity responseEntity = response.getEntity();
            log.debug("响应状态为:" + response.getStatusLine());
            long t2 = System.nanoTime();//收到响应的时间
            if (responseEntity != null) {

                responseResult = EntityUtils.toString(responseEntity, StandardCharsets.UTF_8);

                log.debug("响应内容为:" + responseResult);

            }

            stringBuffer = new StringBuffer();
            for (Header header : response.getAllHeaders()) {
                stringBuffer.append(header.toString()).append(",");
            }
            log.info(String.format("响应头信息: [%s]", stringBuffer.toString()));

            log.info(String.format("执行时间: [%.1fms]", (t2 - t1) / 1e6d));

        } catch (Exception e) {
            log.info("Get responseResult：", e);
            e.printStackTrace();
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return responseResult;

    }

    private static byte[] executeBytes(HttpRequestBase httpRequestBase) {
        log.info(String.format("请求地址: [%s]", httpRequestBase.getURI().toString()));
        log.info(String.format("请求类型: [%s]", httpRequestBase.getMethod()));
        StringBuffer stringBuffer = new StringBuffer();
        for (Header header : httpRequestBase.getAllHeaders()) {
            stringBuffer.append(header.toString()).append(",");
        }
        log.info(String.format("请求头信息: [%s]", stringBuffer.toString()));


        log.info(String.format("请求参数: [%s]", httpRequestBase.getURI().getQuery()));

        byte[] bytes = null;
        // 响应模型
        CloseableHttpResponse response = null;
        try {
            // 将上面的配置信息 运用到这个Get请求里
            httpRequestBase.setConfig(requestConfig);
            long t1 = System.nanoTime();//请求发起的时间
            response = httpClient.execute(httpRequestBase);
            // 从响应模型中获取响应实体
            HttpEntity responseEntity = response.getEntity();
            log.debug("响应状态为:" + response.getStatusLine());
            long t2 = System.nanoTime();//收到响应的时间
            if (responseEntity != null) {
                bytes = EntityUtils.toByteArray(responseEntity);
                log.debug("响应byte长度:" + bytes.length);
            }

            stringBuffer = new StringBuffer();
            for (Header header : response.getAllHeaders()) {
                stringBuffer.append(header.toString()).append(",");
            }
            log.info(String.format("响应头信息: [%s]", stringBuffer.toString()));

            log.info(String.format("执行时间: [%.1fms]", (t2 - t1) / 1e6d));

        } catch (Exception e) {
            log.info("Get responseResult：", e);
            e.printStackTrace();
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return bytes;

    }

    /**
     * <p>get请求</p>
     *
     * @param url 请求地址
     * @return java.lang.String 响应结果
     * @author hanqf
     * 2020/4/18 15:57
     */
    public static String get(String url) {
        return get(url, new HashMap<>());
    }

    /**
     * <p>get请求</p>
     *
     * @param url 请求地址
     * @return java.lang.String 响应结果
     * @author hanqf
     * 2020/4/18 15:49
     */
    public static String get(String url, Map<String, Object> params) {
        HttpGet httpGet = null;
        List<NameValuePair> list = new ArrayList<>();
        for (String key : params.keySet()) {
            list.add(new BasicNameValuePair(key, params.get(key).toString()));
        }

        // 由客户端执行(发送)Get请求
        try {
            URI uri = new URIBuilder(url).addParameters(list).build();
            // 创建Get请求
            httpGet = new HttpGet(uri);

        } catch (Exception e) {
            log.info("Get responseResult：", e);
            e.printStackTrace();
        }
        return execute(httpGet);
    }

    public static byte[] getBytes(String url, Map<String, Object> params) {
        HttpGet httpGet = null;
        List<NameValuePair> list = new ArrayList<>();
        for (String key : params.keySet()) {
            list.add(new BasicNameValuePair(key, params.get(key).toString()));
        }

        // 由客户端执行(发送)Get请求
        try {
            URI uri = new URIBuilder(url).addParameters(list).build();
            // 创建Get请求
            httpGet = new HttpGet(uri);

        } catch (Exception e) {
            log.info("Get responseResult：", e);
            e.printStackTrace();
        }
        return executeBytes(httpGet);
    }

    /**
     * <p>post请求</p>
     *
     * @param url 请求地址
     * @return java.lang.String 响应结果
     * @author hanqf
     * 2020/4/18 15:54
     */
    public static String post(String url) {
        return post(url, new HashMap<>());
    }

    /**
     * <p>post请求</p>
     *
     * @param url    请求地址
     * @param params 请求参数
     * @return java.lang.String 响应结果
     * @author hanqf
     * 2020/4/18 15:50
     */
    public static String post(String url, Map<String, Object> params) {
        HttpPost httpPost = null;
        List<NameValuePair> list = new ArrayList<>();
        for (String key : params.keySet()) {
            list.add(new BasicNameValuePair(key, params.get(key).toString()));
        }

        try {
            URI uri = new URIBuilder(url).addParameters(list).build();
            httpPost = new HttpPost(uri);
        } catch (Exception e) {
            log.info("Get responseResult：", e);
            e.printStackTrace();
        }

        return execute(httpPost);
    }

    public static byte[] postBytes(String url, Map<String, Object> params) {
        HttpPost httpPost = null;
        List<NameValuePair> list = new ArrayList<>();
        for (String key : params.keySet()) {
            list.add(new BasicNameValuePair(key, params.get(key).toString()));
        }

        try {
            URI uri = new URIBuilder(url).addParameters(list).build();
            httpPost = new HttpPost(uri);
        } catch (Exception e) {
            log.info("Get responseResult：", e);
            e.printStackTrace();
        }

        return executeBytes(httpPost);
    }

    /**
     * <p>post请求，请求体为json</p>
     *
     * @param url  请求地址
     * @param json 请求json
     * @return java.lang.String 响应结果
     * @author hanqf
     * 2020/4/18 16:02
     */
    public static String postJson(String url, String json) {
        return postJson(url, json, false);
    }

    /**
     * <p>post请求，请求体为json</p>
     *
     * @param url  请求地址
     * @param json 请求json
     * @param gzip 是否开启gzip压缩
     * @return java.lang.String 响应结果
     * @author hanqf
     * 2020/4/18 16:02
     */
    public static String postJson(String url, String json, boolean gzip) {
        HttpPost httpPost = null;
        try {
            URI uri = new URIBuilder(url).build();
            httpPost = new HttpPost(uri);

            // post请求是将参数放在请求体里面传过去的,这里将entity放入post请求体中

            httpPost.setHeader("Content-Type", "application/json;charset=utf8");

            if (gzip) {
                httpPost.setHeader("Content-Encoding", "gzip");
                ByteArrayOutputStream originalContent = new ByteArrayOutputStream();
                originalContent.write(json.getBytes(Charset.forName("UTF-8")));
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                GZIPOutputStream gzipOut = new GZIPOutputStream(baos);
                originalContent.writeTo(gzipOut);
                gzipOut.finish();
                httpPost.setEntity(new ByteArrayEntity(baos
                        .toByteArray(), ContentType.create("text/plain", "utf-8")));
            } else {
                StringEntity entity = new StringEntity(json, "UTF-8");
                httpPost.setEntity(entity);

            }

        } catch (Exception e) {
            log.info("Get responseResult：", e);
            e.printStackTrace();
        }
        return execute(httpPost);
    }

    /**
     * <p>post请求，请求参数中有中文的话可以使用这个请求</p>
     *
     * @param url    请求地址
     * @param params 请求参数，只可以为中文
     * @return java.lang.String
     * @author hanqf
     * 2020/4/18 16:30
     */
    public static String postForm(String url, Map<String, Object> params) {
        HttpPost httpPost = null;
        List<NameValuePair> list = new ArrayList<>();
        for (String key : params.keySet()) {
            list.add(new BasicNameValuePair(key, params.get(key).toString()));
        }

        try {
            UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(list, StandardCharsets.UTF_8);
            URI uri = new URIBuilder(url).build();
            httpPost = new HttpPost(uri);
            httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
            httpPost.setEntity(urlEncodedFormEntity);


        } catch (Exception e) {
            log.info("Get responseResult：", e);
            e.printStackTrace();
        }

        return execute(httpPost);
    }

    /**
     * <p>post请求，输入流</p>
     *
     * @param url   请求地址
     * @param bytes 请求输入流
     * @return java.lang.String
     * @author hanqf
     * 2020/4/18 16:37
     */
    public static String postInputBytes(String url, byte[] bytes) {
        return postInputBytes(url, bytes, false);
    }

    /**
     * <p>post请求，输入流</p>
     *
     * @param url   请求地址
     * @param bytes 请求输入流
     * @param gzip  是否开启gzip压缩
     * @return java.lang.String
     * @author hanqf
     * 2020/4/18 16:37
     */
    public static String postInputBytes(String url, byte[] bytes, boolean gzip) {
        HttpPost httpPost = null;
        try {
            URI uri = new URIBuilder(url).build();
            httpPost = new HttpPost(uri);

            // post请求是将参数放在请求体里面传过去的,这里将entity放入post请求体中
            if (gzip) {
                httpPost.setHeader("Content-Encoding", "gzip");
                ByteArrayOutputStream originalContent = new ByteArrayOutputStream();
                originalContent.write(bytes);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                GZIPOutputStream gzipOut = new GZIPOutputStream(baos);
                originalContent.writeTo(gzipOut);
                gzipOut.finish();
                httpPost.setEntity(new ByteArrayEntity(baos
                        .toByteArray(), ContentType.create("text/plain", "utf-8")));
            } else {
                ByteArrayEntity entity = new ByteArrayEntity(bytes, ContentType.create("text/plain", "utf-8"));
                httpPost.setEntity(entity);
            }

        } catch (Exception e) {
            log.info("Get responseResult：", e);
            e.printStackTrace();
        }

        return execute(httpPost);
    }

    /**
     * <p>post请求，流</p>
     *
     * @param url
     * @param is
     * @return java.lang.String
     * @author hanqf
     * 2020/4/21 22:24
     */
    public static String postInputStream(String url, InputStream is) {
        return postInputStream(url, is, false);
    }

    /**
     * <p>post请求，流</p>
     *
     * @param url
     * @param is
     * @param gzip
     * @return java.lang.String
     * @author hanqf
     * 2020/4/21 22:24
     */
    public static String postInputStream(String url, InputStream is, boolean gzip) {
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
        return postInputBytes(url, bytes, gzip);
    }

    /**
     * <p>post请求，传输附件</p>
     *
     * @param url   请求地址
     * @param files 文件列表
     * @return java.lang.String
     * @author hanqf
     * 2020/4/18 16:52
     */
    public static String postFile(String url, File[] files) {
        return postFile(url, new HashMap<>(), files);
    }

    /**
     * <p>post请求，传输附件</p>
     *
     * @param url    请求地址
     * @param params 其它参数
     * @param files  文件列表
     * @return java.lang.String
     * @author hanqf
     * 2020/4/18 16:50
     */
    public static String postFile(String url, Map<String, Object> params, File[] files) {
        HttpPost httpPost = null;
        try {
            URI uri = new URIBuilder(url).build();
            httpPost = new HttpPost(uri);

            MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
            String filesKey = "files";
            for (File file : files) {
                //multipartEntityBuilder.addPart(filesKey,new FileBody(file)); //与下面的语句作用相同
                //multipartEntityBuilder.addBinaryBody(filesKey, file);

                // 防止服务端收到的文件名乱码。 我们这里可以先将文件名URLEncode，然后服务端拿到文件名时在URLDecode。就能避免乱码问题。
                // 文件名其实是放在请求头的Content-Disposition里面进行传输的，如其值为form-data; name="files"; filename="头像.jpg"
                multipartEntityBuilder.addBinaryBody(filesKey, file, ContentType.DEFAULT_BINARY, URLEncoder.encode(file.getName(), "utf-8"));

            }

            // 其它参数(注:自定义contentType，设置UTF-8是为了防止服务端拿到的参数出现乱码)
            ContentType contentType = ContentType.create("text/plain", Charset.forName("UTF-8"));
            for (String key : params.keySet()) {
                multipartEntityBuilder.addTextBody(key, params.get(key).toString(), contentType);
            }
            HttpEntity entity = multipartEntityBuilder.build();

            // post请求是将参数放在请求体里面传过去的,这里将entity放入post请求体中
            httpPost.setEntity(entity);

        } catch (Exception e) {
            log.info("Get responseResult：", e);
            e.printStackTrace();
        }

        return execute(httpPost);
    }


}
