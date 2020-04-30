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
    static String URL_GET_BYTES_ZIP = URL + "getBytesZip";
    static Map<String,Object> map = new HashMap<>();
    static {
        map.put("name","张三");
        map.put("age",20);
        map.put("salary",10000.12);
    }
    static String JSON = "{\"name\":\"张三\",\"salary\":\"10000.12\",\"age\":\"20\"}";

    static byte[] BYTES = JSON.getBytes(Charset.forName("UTF-8"));

    static InputStream INPUTSTREAM = new ByteArrayInputStream(BYTES);

    static File[] files = new File[]{
            new File("../file.txt"),
            new File("../文件.txt")
    };

    @Test
    public void getTest() throws UnsupportedEncodingException {
        System.out.println("get=" + OkHttpUtil.get(URL_GET));
        System.out.println("getParam=" + OkHttpUtil.get(URL_GET,map));
        System.out.println("getBytes=" + new String(OkHttpUtil.getBytes(URL_GET_BYTES,map),"utf-8"));
        System.out.println("getBytesZip=" + new String(OkHttpUtil.getBytes(URL_GET_BYTES_ZIP,map),"utf-8"));
    }

    @Test
    public void postTest() throws UnsupportedEncodingException {
        System.out.println("post=" + OkHttpUtil.post(URL_POST));
        System.out.println("postParam=" + OkHttpUtil.post(URL_POST,map));
        System.out.println("postForm=" + OkHttpUtil.post(URL_POST_FORM,map));
        System.out.println("postJson=" + OkHttpUtil.post(URL_POST_JSON,JSON));
        System.out.println("postBytes=" + OkHttpUtil.post(URL_POST_STREAM,BYTES));
        System.out.println("postStream=" + OkHttpUtil.post(URL_POST_STREAM,INPUTSTREAM));
        System.out.println("postFiles=" + OkHttpUtil.post(URL_POST_FILES,files));
        System.out.println("postFiles=" + OkHttpUtil.post(URL_POST_FILES,map,files));

        System.out.println("postParamGetBytes=" + new String(OkHttpUtil.postBytes(URL_GET_BYTES,map),"utf-8"));
        System.out.println("postParamGetBytesZip=" + new String(OkHttpUtil.postBytes(URL_GET_BYTES_ZIP, map), "utf-8"));

    }
}
