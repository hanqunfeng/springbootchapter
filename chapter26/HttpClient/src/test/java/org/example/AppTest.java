package org.example;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
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
    public void getTest()
    {
        System.out.println("get=" + HttpClientUtil.get(URL_GET));
        System.out.println("getParam=" + HttpClientUtil.get(URL_GET,map));
    }

    @Test
    public void postTest(){
        System.out.println("post=" + HttpClientUtil.post(URL_POST));
        System.out.println("postParam=" + HttpClientUtil.post(URL_POST,map));
        System.out.println("postForm=" + HttpClientUtil.postForm(URL_POST_FORM,map));
        System.out.println("postJson=" + HttpClientUtil.postJson(URL_POST_JSON,JSON));
        System.out.println("postJsonZip=" + HttpClientUtil.postJson(URL_POST_JSON,JSON,true));
        System.out.println("postBytes=" + HttpClientUtil.postInputBytes(URL_POST_STREAM,BYTES));
        System.out.println("postBytesZip=" + HttpClientUtil.postInputBytes(URL_POST_STREAM,BYTES,true));
        System.out.println("postStream=" + HttpClientUtil.postInputStream(URL_POST_STREAM,INPUTSTREAM));
        System.out.println("postStreamZip=" + HttpClientUtil.postInputStream(URL_POST_STREAM,INPUTSTREAM2,true));
        System.out.println("postFiles=" + HttpClientUtil.postFile(URL_POST_FILES,map,files));
    }
}
