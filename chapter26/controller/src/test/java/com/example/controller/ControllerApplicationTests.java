package com.example.controller;

import com.example.controller.utils.RestTemplateUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
class ControllerApplicationTests {

    static String URL = "http://localhost:8080/demo/";
    static String URL_GET = URL + "get";
    static String URL_POST = URL + "post";
    static String URL_POST_FORM = URL + "form";
    static String URL_POST_JSON = URL + "json";
    static String URL_POST_STREAM = URL + "stream";
    static String URL_POST_FILES = URL + "files";
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

    @Autowired
    private RestTemplateUtil restTemplateUtil;

    @Test
    void get() {
        System.out.println("get=" + restTemplateUtil.get(URL_GET));
        System.out.println("getParam=" + restTemplateUtil.get(URL_GET,map));
    }

    @Test
    void post() {
        System.out.println("post=" + restTemplateUtil.post(URL_POST));
        System.out.println("postParam=" + restTemplateUtil.post(URL_POST_FORM,map));
        try {
            System.out.println("postJson=" + restTemplateUtil.postJson(URL_POST_JSON, JSON));
            //注意这里一定要发给流处理连接，压缩之后都是流
            System.out.println("postJsonZip=" + restTemplateUtil.postJson(URL_POST_STREAM, JSON,true));

            System.out.println("postBytes=" + restTemplateUtil.postBytes(URL_POST_STREAM, BYTES));
            System.out.println("postBytesZip=" + restTemplateUtil.postBytes(URL_POST_STREAM, BYTES,true));

            System.out.println("postStream=" + restTemplateUtil.postStream(URL_POST_STREAM, INPUTSTREAM));
            System.out.println("postStreamZip=" + restTemplateUtil.postStream(URL_POST_STREAM, INPUTSTREAM2,true));
        }catch (Exception e){
            e.printStackTrace();
        }

        System.out.println("postFiles=" + restTemplateUtil.postFiles(URL_POST_FILES, files));
        System.out.println("postParamFiles=" + restTemplateUtil.postFiles(URL_POST_FILES,map, files));
    }



}
