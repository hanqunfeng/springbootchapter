package org.example;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * <p>RestTemplate工具类</p>
 * Created by hanqf on 2020/4/22 16:54.
 */

@Slf4j
public class RestTemplateUtil {

    private static final SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
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
