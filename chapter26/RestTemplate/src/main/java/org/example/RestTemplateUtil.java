package org.example;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

/**
 * <p>RestTemplate工具类</p>
 * Created by hanqf on 2020/4/22 16:54.
 */


public class RestTemplateUtil {

    private static SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
    private static RestTemplate restTemplate = null;

    static {
        factory.setReadTimeout(5000);//单位为ms
        factory.setConnectTimeout(5000);//单位为ms

        restTemplate = new RestTemplate(factory);
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
            StringBuffer stringBuffer = new StringBuffer();
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
                originalContent.write(json.getBytes("utf-8"));
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
            headers.setContentType(new MediaType("application", "json", Charset.forName("utf-8")));
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


}
