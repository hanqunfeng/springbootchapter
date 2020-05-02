package org.example;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Unit test for simple App.
 */
public class AppTest {
    static String URL = "http://localhost:8080/demo/";
    static String URL_GET = URL + "get";
    static String URL_POST = URL + "post";
    static String URL_POST_FORM = URL + "form";
    static String URL_POST_JSON = URL + "json";
    static String URL_POST_STREAM = URL + "stream";
    static String URL_POST_FILES = URL + "files";
    static String URL_GET_BYTES = URL + "getBytes";
    static String URL_GET_BYTES_ZIP = URL + "getBytesZip";
    static Map<String, Object> map = new HashMap<>();
    static String JSON = "{\"name\":\"张三\",\"salary\":\"10000.12\",\"age\":\"20\"}";
    static byte[] BYTES = JSON.getBytes(StandardCharsets.UTF_8);
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


    @Test
    public void get() {
        System.out.println("get=" + RestTemplateUtil.get(URL_GET));
        System.out.println("getParam=" + RestTemplateUtil.get(URL_GET, map));
        System.out.println("getBytes=" + new String(RestTemplateUtil.getBytes(URL_GET_BYTES, map), StandardCharsets.UTF_8));
        System.out.println("getBytesZip=" + new String(RestTemplateUtil.getBytes(URL_GET_BYTES_ZIP, map), StandardCharsets.UTF_8));
    }

    @Test
    public void post() {
        System.out.println("post=" + RestTemplateUtil.post(URL_POST));
        System.out.println("postParam=" + RestTemplateUtil.post(URL_POST_FORM, map));
        System.out.println("postJson=" + RestTemplateUtil.postJson(URL_POST_JSON, JSON));
        System.out.println("postJsonZip=" + RestTemplateUtil.postJson(URL_POST_STREAM, JSON, true));
        System.out.println("postBytes=" + RestTemplateUtil.postBytes(URL_POST_STREAM, BYTES));
        System.out.println("postBytesZip=" + RestTemplateUtil.postBytes(URL_POST_STREAM, BYTES, true));
        System.out.println("postStream=" + RestTemplateUtil.postStream(URL_POST_STREAM, INPUTSTREAM));
        System.out.println("postStreamZip=" + RestTemplateUtil.postStream(URL_POST_STREAM, INPUTSTREAM2, true));
        System.out.println("postFiles=" + RestTemplateUtil.postFiles(URL_POST_FILES, files));
        System.out.println("postParamFiles=" + RestTemplateUtil.postFiles(URL_POST_FILES, map, files));

        System.out.println("postParamGetBytes=" + new String(RestTemplateUtil.postBytes(URL_GET_BYTES, map), StandardCharsets.UTF_8));
        System.out.println("postParamGetBytesZip=" + new String(RestTemplateUtil.postBytes(URL_GET_BYTES_ZIP, map), StandardCharsets.UTF_8));

    }


}
