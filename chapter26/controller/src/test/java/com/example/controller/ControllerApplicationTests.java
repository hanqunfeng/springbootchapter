package com.example.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

//这里一定要加上webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT，否则报TestRestTemplate没有定义
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ControllerApplicationTests {

    static Map<String, Object> map = new HashMap<>();
    static String JSON = "{\"name\":\"张三\",\"salary\":\"10000.12\",\"age\":\"20\"}";
    static byte[] BYTES = JSON.getBytes(Charset.forName("UTF-8"));
    static InputStream INPUTSTREAM = new ByteArrayInputStream(BYTES);
    static InputStream INPUTSTREAM2 = new ByteArrayInputStream(BYTES);
    static File[] files = new File[]{
            new File("../file.txt"),
            new File("../文件.txt")
    };

    static {
        map.put("name", "张三");
        map.put("age", 20);
        map.put("salary", 10000.12);
    }

    private String URL = "/demo/";
    private String URL_GET = URL + "get";
    private String URL_POST = URL + "post";
    private String URL_POST_FORM = URL + "form";
    private String URL_POST_JSON = URL + "json";
    private String URL_POST_STREAM = URL + "stream";
    private String URL_POST_FILES = URL + "files";
    private String URL_GET_BYTES = URL + "getBytes";
    private String URL_GET_BYTES_ZIP = URL + "getBytesZip";

    @Autowired
    //TestRestTemplate使用方法与RestTemplate一致，只是这里不需要启动服务就可以测试
    private TestRestTemplate restTemplate;

    @Test
    public void getTest() {
        System.out.println("get=" + get(URL_GET));
        System.out.println("getParam=" + get(URL_GET, map));
        System.out.println("getBytes=" + new String(getBytes(URL_GET_BYTES, map), StandardCharsets.UTF_8));
        System.out.println("getBytesZip=" + new String(getBytes(URL_GET_BYTES_ZIP, map), StandardCharsets.UTF_8));
    }

    @Test
    public void postTest() {
        System.out.println("post=" + post(URL_POST));
        System.out.println("postParam=" + post(URL_POST_FORM, map));
        System.out.println("postJson=" + postJson(URL_POST_JSON, JSON));
        System.out.println("postJsonZip=" + postJson(URL_POST_STREAM, JSON, true));
        System.out.println("postBytes=" + postBytes(URL_POST_STREAM, BYTES));
        System.out.println("postBytesZip=" + postBytes(URL_POST_STREAM, BYTES, true));
        System.out.println("postStream=" + postStream(URL_POST_STREAM, INPUTSTREAM));
        System.out.println("postStreamZip=" + postStream(URL_POST_STREAM, INPUTSTREAM2, true));
        System.out.println("postFiles=" + postFiles(URL_POST_FILES, files));
        System.out.println("postParamFiles=" + postFiles(URL_POST_FILES, map, files));

        System.out.println("postParamGetBytes=" + new String(postBytes(URL_GET_BYTES, map), StandardCharsets.UTF_8));
        System.out.println("postParamGetBytesZip=" + new String(postBytes(URL_GET_BYTES_ZIP, map), StandardCharsets.UTF_8));

    }


    public String get(String url) {
        return get(url, new HashMap<>());
    }

    public String post(String url) {
        return post(url, new HashMap<>());
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
    public String postStream(String url, InputStream is, boolean gzip) {
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
     * 2020/5/7 09:41
     */
    public byte[] getBytes(String url, Map<String, Object> map) {
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

    /**
     * <p>post请求，返回字节数组</p>
     *
     * @param url
     * @param params
     * @return byte[]
     * @author hanqf
     * 2020/5/7 09:41
     */
    public byte[] postBytes(String url, Map<String, Object> params) {

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

    public String postJson(String url, String json) {
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
    public String postJson(String url, String json, boolean gzip) {
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

    public String postBytes(String url, byte[] bytes) {
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
    public String postBytes(String url, byte[] bytes, boolean gzip) {
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

    public String postStream(String url, InputStream is) {
        return postStream(url, is, false);
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
