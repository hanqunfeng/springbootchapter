package org.example;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * <p>RestTemplate工具类</p>
 * Created by hanqf on 2020/4/22 16:54.
 */

@Slf4j
public class RestTemplateUtil {
    /**
     * RestTemplate只是对其他的HTTP客户端的封装，其本身并没有实现HTTP相关的基础功能。
     * RestTemplate 支持至少三种HTTP客户端库。
     * SimpleClientHttpRequestFactory。对应的HTTP库是java JDK自带的HttpURLConnection，此为默认值。
     * HttpComponentsAsyncClientHttpRequestFactory。对应的HTTP库是Apache HttpComponents。
     * OkHttp3ClientHttpRequestFactory。对应的HTTP库是OkHttp。
     * <p>
     * 各种HTTP客户端性能以及易用程度评测来看，OkHttp 优于 Apache HttpComponents、Apache HttpComponents优于HttpURLConnection。
     */
    //private static final SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
    //private static final HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
    private static final OkHttp3ClientHttpRequestFactory factory = new OkHttp3ClientHttpRequestFactory();
    private static final RestTemplate restTemplate;

    static {
        factory.setReadTimeout(5000);//单位为ms
        factory.setConnectTimeout(5000);//单位为ms

        //拦截器
        List<ClientHttpRequestInterceptor> list = new ArrayList<>();
        list.add(new RetryIntercepter()); //重试拦截器
        list.add(new HeadersLoggingInterceper()); //header拦截器

        restTemplate = new RestTemplate(factory);
        restTemplate.setInterceptors(list);

        //非200的http状态码导致默认的restTemplate调用直接抛异常而不是直接得到对方的错误json信息
        //这里增加自定义的错误处理器，不管状态码是200还是其它的都会返回结果
        ResponseErrorHandler responseErrorHandler = new ResponseErrorHandler() {
            //hasError默认写死true，都进；
            @Override
            public boolean hasError(ClientHttpResponse response) throws IOException {
                return true;
            }

            //handleError 为空，相当于跳过错误抛出
            @Override
            public void handleError(ClientHttpResponse response) throws IOException {
                // 只要重写此方法，不去抛出HttpClientErrorException异常即可
                HttpStatus statusCode = response.getStatusCode();
                log.error("错误码::[{}]",statusCode);
            }
        };
        restTemplate.setErrorHandler(responseErrorHandler);
    }

    public static String get(String url) {
        return get(url, new HashMap<>());
    }

    /**
     * <p>get请求</p>
     *
     * @param url
     * @param map
     * @return java.lang.String
     * @author hanqf
     * 2020/4/22 16:57
     */
    public static String get(String url, Map<String, Object> map) {
        if (map.size() > 0) {
            StringBuilder stringBuffer = new StringBuilder();
            stringBuffer.append(url);
            if (url.contains("?")) {
                stringBuffer.append("&");
            } else {
                stringBuffer.append("?");
            }
            for (String key : map.keySet()) {
                stringBuffer.append(key).append("=").append(map.get(key).toString()).append("&");
            }
            url = stringBuffer.toString();
        }
        return restTemplate.getForObject(url, String.class);
    }

    /**
     * <p>get请求，返回字节数组</p>
     *
     * @param url
     * @param map
     * @return byte[]
     * @author hanqf
     * 2020/5/7 09:42
     */
    public static byte[] getBytes(String url, Map<String, Object> map) {
        if (map.size() > 0) {
            StringBuilder stringBuffer = new StringBuilder();
            stringBuffer.append(url);
            if (url.contains("?")) {
                stringBuffer.append("&");
            } else {
                stringBuffer.append("?");
            }
            for (String key : map.keySet()) {
                stringBuffer.append(key).append("=").append(map.get(key).toString()).append("&");
            }
            url = stringBuffer.toString();
        }
        //return restTemplate.getForObject(url, byte[].class);
        ResponseEntity<byte[]> exchange = restTemplate.exchange(url, HttpMethod.GET, null, byte[].class);
        byte[] bytes = exchange.getBody();


        //判断是否需要解压，即服务器返回是否经过了gzip压缩--start
        List<String> strings = exchange.getHeaders().get("Content-Encoding");
        if (strings != null && strings.contains("gzip")) {
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

        return bytes;

    }

    public static String post(String url) {
        return post(url, new HashMap<>());
    }

    /**
     * <p>post请求</p>
     *
     * @param url
     * @param params
     * @return java.lang.String
     * @author hanqf
     * 2020/4/22 16:59
     */
    public static String post(String url, Map<String, Object> params) {

        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        if (params != null && params.size() > 0) {
            map.setAll(params);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>(map, headers);
        return restTemplate.postForObject(url, httpEntity, String.class);
    }

    /**
     * <p>post请求，返回字节数组</p>
     *
     * @param url
     * @param params
     * @return byte[]
     * @author hanqf
     * 2020/5/7 09:42
     */
    public static byte[] postBytes(String url, Map<String, Object> params) {

        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        if (params != null && params.size() > 0) {
            map.setAll(params);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>(map, headers);
        //byte[] bytes = restTemplate.postForObject(url, httpEntity, byte[].class);

        ResponseEntity<byte[]> exchange = restTemplate.exchange(url, HttpMethod.POST, httpEntity, byte[].class);
        byte[] bytes = exchange.getBody();


        //判断是否需要解压，即服务器返回是否经过了gzip压缩--start
        List<String> strings = exchange.getHeaders().get("Content-Encoding");
        if (strings != null && strings.contains("gzip")) {
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

        return bytes;
    }

    public static String postJson(String url, String json) {
        return postJson(url, json, false);
    }

    /**
     * <p>post请求json</p>
     *
     * @param url
     * @param json
     * @return java.lang.String
     * @author hanqf
     * 2020/4/22 17:04
     */
    public static String postJson(String url, String json, boolean gzip) {
        HttpHeaders headers = new HttpHeaders();


        if (gzip) {
            headers.setContentType(new MediaType("application", "octet-stream"));
            HttpEntity<byte[]> httpEntity = null;
            try {
                headers.add("Content-Encoding", "gzip");
                ByteArrayOutputStream originalContent = new ByteArrayOutputStream();
                originalContent.write(json.getBytes(StandardCharsets.UTF_8));
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                GZIPOutputStream gzipOut = new GZIPOutputStream(baos);
                originalContent.writeTo(gzipOut);
                gzipOut.finish();
                httpEntity = new HttpEntity<>(baos.toByteArray(), headers);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return restTemplate.postForObject(url, httpEntity, String.class);
        } else {
            headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));
            //headers.add("Content-Type", "application/json;charset=utf8");
            HttpEntity<String> httpEntity = new HttpEntity<>(json, headers);
            return restTemplate.postForObject(url, httpEntity, String.class);
        }
    }

    public static String postBytes(String url, byte[] bytes) {
        return postBytes(url, bytes, false);
    }

    /**
     * <p>post字节流</p>
     *
     * @param url
     * @param bytes
     * @param gzip
     * @return java.lang.String
     * @author hanqf
     * 2020/4/22 17:10
     */
    public static String postBytes(String url, byte[] bytes, boolean gzip) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "octet-stream"));
        HttpEntity<byte[]> httpEntity = null;
        if (gzip) {
            try {
                headers.add("Content-Encoding", "gzip");
                ByteArrayOutputStream originalContent = new ByteArrayOutputStream();
                originalContent.write(bytes);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                GZIPOutputStream gzipOut = new GZIPOutputStream(baos);
                originalContent.writeTo(gzipOut);
                gzipOut.finish();
                httpEntity = new HttpEntity<>(baos.toByteArray(), headers);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            httpEntity = new HttpEntity<>(bytes, headers);
        }
        return restTemplate.postForObject(url, httpEntity, String.class);

    }

    public static String postStream(String url, InputStream is) {
        return postStream(url, is, false);
    }

    /**
     * <p>post请求，流</p>
     *
     * @param url
     * @param is
     * @param gzip
     * @return java.lang.String
     * @author hanqf
     * 2020/4/22 17:12
     */
    public static String postStream(String url, InputStream is, boolean gzip) {
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

        return postBytes(url, bytes, gzip);

    }

    public static String postFiles(String url, File[] files) {
        return postFiles(url, new HashMap<>(), files);
    }

    /**
     * <p>post请求，传输文件</p>
     *
     * @param url
     * @param params
     * @param files
     * @return java.lang.String
     * @author hanqf
     * 2020/4/22 17:16
     */
    public static String postFiles(String url, Map<String, Object> params, File[] files) {

        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        if (params != null && params.size() > 0) {
            map.setAll(params);
        }

        for (File file : files) {
            map.add("files", new FileSystemResource(file));
        }
        return restTemplate.postForObject(url, map, String.class);

    }

    /**
     * <h2>小文件下载</h2>
     * Created by hanqf on 2020/11/2 16:56. <br>
     *
     * @param fileUrl  文件下载url
     * @param savePath 文件保存路径
     * @author hanqf
     */
    public static void downloadSmallFile(String fileUrl, String savePath) throws IOException {
        ResponseEntity<byte[]> rsp = restTemplate.getForEntity(fileUrl, byte[].class);
        System.out.println("文件下载请求结果状态码：" + rsp.getStatusCode());
        // 将下载下来的文件内容保存到本地
        Files.write(Paths.get(savePath), Objects.requireNonNull(rsp.getBody(),
                "未获取到下载文件"));
    }

    /**
     * <h2>大文件下载</h2>
     * Created by hanqf on 2020/11/2 16:58. <br>
     *
     * @param fileUrl  文件下载url
     * @param savePath 文件保存路径
     * @author hanqf
     */
    public static void downloadBigFile(String fileUrl, String savePath) {
        //设置了请求头APPLICATION_OCTET_STREAM，表示以流的形式进行数据加载
        RequestCallback requestCallback = request -> request.getHeaders()
                .setAccept(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM, MediaType.ALL));
        //对响应进行流式处理而不是将其全部加载到内存中
        restTemplate.execute(fileUrl, HttpMethod.GET, requestCallback, clientHttpResponse -> {
            Files.copy(clientHttpResponse.getBody(), Paths.get(savePath));
            return null;
        });
    }

    /**
     * <p>重试拦截器</p>
     */
    private static class RetryIntercepter implements ClientHttpRequestInterceptor {

        private int maxRetry = 3;//最大重试次数，默认3次
        private int retryNum = 0;

        public RetryIntercepter() {

        }

        public RetryIntercepter(int maxRetry) {
            this.maxRetry = maxRetry;
        }

        @Override
        public ClientHttpResponse intercept(HttpRequest httpRequest, byte[] bytes, ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {
            ClientHttpResponse response = clientHttpRequestExecution.execute(httpRequest, bytes);
            if (!response.getStatusCode().equals(HttpStatus.OK) && retryNum < maxRetry) {
                retryNum++;
                response = clientHttpRequestExecution.execute(httpRequest, bytes);

            }
            return response;
        }
    }

    /**
     * <p>Headers拦截器</p>
     */
    private static class HeadersLoggingInterceper implements ClientHttpRequestInterceptor {

        @Override
        public ClientHttpResponse intercept(HttpRequest httpRequest, byte[] bytes, ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {
            log.info(String.format("请求地址: %s", httpRequest.getURI()));
            log.info(String.format("请求头信息: %s", httpRequest.getHeaders()));
            ClientHttpResponse response = clientHttpRequestExecution.execute(httpRequest, bytes);
            log.info(String.format("响应头信息: %s", response.getHeaders()));
            return response;
        }
    }


}
