package org.example;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    static String URL = "http://localhost:8080/demo/";
    static String URL_GET = URL + "get";
    static String URL_POST = URL + "post";
    static String URL_POST_FORM = URL + "form";
    static String URL_POST_JSON = URL + "json";
    static String URL_POST_STREAM = URL + "stream";
    static String URL_POST_FILES = URL + "files";
    static String URL_GET_BYTES = URL + "getBytes";
    static Map<String,Object> map = new HashMap<>();
    static {
        map.put("name","张三");
        map.put("age",20);
        map.put("salary",10000.12);
    }
    static String JSON = "{\"name\":\"张三\",\"salary\":\"10000.12\",\"age\":\"20\"}";

    static byte[] BYTES = JSON.getBytes(Charset.forName("UTF-8"));

    static InputStream INPUTSTREAM = new ByteArrayInputStream(BYTES);
    static InputStream INPUTSTREAM2 = new ByteArrayInputStream(BYTES);

    static File[] files = new File[]{
            new File("../file.txt"),
            new File("../文件.txt")
    };

    @Test
    public void getTest() throws UnsupportedEncodingException {
        System.out.println("get=" + HttpUtil.get(URL_GET));
        System.out.println("getParam=" + HttpUtil.get(URL_GET,map));
        System.out.println("getBytes=" + new String(HttpUtil.getBytes(URL_GET_BYTES,map),"utf-8"));
    }

    @Test
    public void postTest() throws UnsupportedEncodingException {
        System.out.println("post=" + HttpUtil.post(URL_POST));
        System.out.println("postParam=" + HttpUtil.post(URL_POST,map));
        System.out.println("postParamGetBytes=" + new String(HttpUtil.postBytes(URL_GET_BYTES,map),"utf-8"));
        System.out.println("postForm=" + HttpUtil.post(URL_POST_FORM,map));
        System.out.println("postJson=" + HttpUtil.post(URL_POST_JSON,JSON));
        System.out.println("postJsonZip=" + HttpUtil.post(URL_POST_JSON,JSON,true));
        System.out.println("postBytes=" + HttpUtil.postBytes(URL_POST_STREAM,BYTES));
        System.out.println("postBytesZip=" + HttpUtil.postBytes(URL_POST_STREAM,BYTES,true));
        System.out.println("postStream=" + HttpUtil.postInputStream(URL_POST_STREAM,INPUTSTREAM));
        System.out.println("postStreamZip=" + HttpUtil.postInputStream(URL_POST_STREAM,INPUTSTREAM2,true));
        System.out.println("postFiles=" + HttpUtil.postFile(URL_POST_FILES,map,files));
    }
}
