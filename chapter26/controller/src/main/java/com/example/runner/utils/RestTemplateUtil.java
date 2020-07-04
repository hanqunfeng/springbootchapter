package com.example.runner.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

/**
 * <p>RestTemplate工具类</p>
 * Created by hanqf on 2020/4/22 16:54.
 */

@Component
public class RestTemplateUtil {
    @Autowired
    private RestTemplate restTemplate;

    public String get(String url) {
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
    public String get(String url, Map<String, Object> map) {
        if (map != null && map.size() > 0) {
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


    public String post(String url) {
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
    public String post(String url, Map<String, Object> params) {

        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        if (params != null && params.size() > 0) {
            map.setAll(params);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>(map, headers);
        return restTemplate.postForObject(url, httpEntity, String.class);
    }

    public String postJson(String url, String json) throws IOException {
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
    public String postJson(String url, String json, boolean gzip) throws IOException {
        HttpHeaders headers = new HttpHeaders();

        if (gzip) {
            headers.setContentType(new MediaType("application", "octet-stream"));

            headers.add("Content-Encoding", "gzip");
            ByteArrayOutputStream originalContent = new ByteArrayOutputStream();
            originalContent.write(json.getBytes(StandardCharsets.UTF_8));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            GZIPOutputStream gzipOut = new GZIPOutputStream(baos);
            originalContent.writeTo(gzipOut);
            gzipOut.finish();

            HttpEntity<byte[]> httpEntity = new HttpEntity<>(baos.toByteArray(), headers);
            return restTemplate.postForObject(url, httpEntity, String.class);
        } else {
            headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));
            //headers.add("Content-Type", "application/json;charset=utf8");
            HttpEntity<String> httpEntity = new HttpEntity<>(json, headers);
            return restTemplate.postForObject(url, httpEntity, String.class);
        }
    }

    public String postBytes(String url, byte[] bytes) throws IOException {
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
    public String postBytes(String url, byte[] bytes, boolean gzip) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "octet-stream"));
        HttpEntity<byte[]> httpEntity = null;
        if (gzip) {
            headers.add("Content-Encoding", "gzip");
            ByteArrayOutputStream originalContent = new ByteArrayOutputStream();
            originalContent.write(bytes);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            GZIPOutputStream gzipOut = new GZIPOutputStream(baos);
            originalContent.writeTo(gzipOut);
            gzipOut.finish();

            httpEntity = new HttpEntity<>(baos.toByteArray(), headers);

        } else {
            httpEntity = new HttpEntity<>(bytes, headers);
        }
        return restTemplate.postForObject(url, httpEntity, String.class);

    }


    public String postStream(String url, InputStream is) throws IOException {
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
    public String postStream(String url, InputStream is, boolean gzip) throws IOException {
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

    public String postFiles(String url, File[] files) {
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
    public String postFiles(String url, Map<String, Object> params, File[] files) {

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
