package com.example.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.activation.MimetypesFileTypeMap;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * <p></p>
 * Created by hanqf on 2020/5/6 22:38.
 */

@SpringBootTest  // 开启 full Spring application context
@AutoConfigureMockMvc //启动自动配置MockMVC
public class MockMvcTests {

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

    static MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
    static {
        if (map != null && map.size() > 0) {
            for (String key : map.keySet()) {
                multiValueMap.add(key, map.get(key).toString());
            }
        }
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
    private MockMvc mockMvc; //只需 autowire

    @Test
    public void getTest() throws Exception {
        System.out.println("getParam=" + get(URL_GET,multiValueMap));
        System.out.println("getBytes=" + new String(getBytes(URL_GET_BYTES,multiValueMap), StandardCharsets.UTF_8));
        System.out.println("getBytesZip=" + new String(getBytes(URL_GET_BYTES_ZIP,multiValueMap), StandardCharsets.UTF_8));
    }
    @Test
    public void postTest() throws Exception {
        System.out.println("post=" + post(URL_POST,multiValueMap));
        System.out.println("postForm=" + postForm(URL_POST_FORM,multiValueMap));
        System.out.println("postJson=" + postJson(URL_POST_JSON,JSON));
        //服务端没有完成gzip解压缩，应该是filter没起作用
        System.out.println("postJsonZip=" + postJson(URL_POST_STREAM, JSON, true));
        System.out.println("postBytes=" + postBytes(URL_POST_STREAM, BYTES));
        System.out.println("postBytesZip=" + postBytes(URL_POST_STREAM, BYTES, true));
        System.out.println("postStream=" + postStream(URL_POST_STREAM, INPUTSTREAM));
        System.out.println("postStreamZip=" + postStream(URL_POST_STREAM, INPUTSTREAM2, true));
        System.out.println("postFiles=" + postFiles(URL_POST_FILES, files));
        System.out.println("postParamFiles=" + postFiles(URL_POST_FILES, multiValueMap, files));

        System.out.println("postParamGetBytes=" + new String(postBytes(URL_GET_BYTES,multiValueMap), StandardCharsets.UTF_8));
        System.out.println("postParamGetBytesZip=" + new String(postBytes(URL_GET_BYTES_ZIP,multiValueMap), StandardCharsets.UTF_8));
    }

    private String exec(RequestBuilder requestBuilder) throws Exception {
        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                //.andDo(MockMvcResultHandlers.print())
                //.andExpect(MockMvcResultMatchers.content().string("{}"))
                .andReturn();
        return mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
    }

    private byte[] execBytes(RequestBuilder requestBuilder) throws Exception {
        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                //.andDo(MockMvcResultHandlers.print())
                //.andExpect(MockMvcResultMatchers.content().string("{}"))
                .andReturn();
        byte[] bytes = mvcResult.getResponse().getContentAsByteArray();

        String header = mvcResult.getResponse().getHeader("Content-Encoding");
        if(header!=null && header.contains("gzip")){
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
        return bytes;
    }



    public String get(String url,MultiValueMap<String, String> multiValueMap) throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(url)
                .params(multiValueMap);
        return exec(requestBuilder);
    }

    public byte[] getBytes(String url,MultiValueMap<String, String> multiValueMap) throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(url)
                .params(multiValueMap);
        return execBytes(requestBuilder);
    }


    public String post(String url,MultiValueMap<String, String> multiValueMap) throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(url)
                .params(multiValueMap);
        return exec(requestBuilder);
    }


    public String postForm(String url,MultiValueMap<String, String> multiValueMap) throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(url)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .params(multiValueMap);
        return exec(requestBuilder);
    }

    public byte[] postBytes(String url,MultiValueMap<String, String> multiValueMap) throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(url)
                .params(multiValueMap);
        return execBytes(requestBuilder);
    }

    public String postJson(String url,String json) throws Exception {
        return postJson(url,json,false);
    }

    public String postJson(String url,String json,boolean gzip) throws Exception {
        MockHttpServletRequestBuilder requestBuilder = null;
        if (gzip) {
            try {

                ByteArrayOutputStream originalContent = new ByteArrayOutputStream();
                originalContent.write(json.getBytes(StandardCharsets.UTF_8));
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                GZIPOutputStream gzipOut = new GZIPOutputStream(baos);
                originalContent.writeTo(gzipOut);
                gzipOut.finish();
                requestBuilder = MockMvcRequestBuilders.post(url)
                        .header("Content-Encoding", "gzip")
                        .contentType(new MediaType("application", "octet-stream"))
                        .content(baos.toByteArray());
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            requestBuilder = MockMvcRequestBuilders.post(url)
                    .contentType(new MediaType("application","json",StandardCharsets.UTF_8))
                    .content(json);
        }


        return exec(requestBuilder);
    }

    public String postBytes(String url,byte[] bytes) throws Exception {
        return postBytes(url,bytes,false);
    }
    public String postBytes(String url,byte[] bytes,boolean gzip) throws Exception {
        MockHttpServletRequestBuilder requestBuilder = null;
        if (gzip) {
            try {
                ByteArrayOutputStream originalContent = new ByteArrayOutputStream();
                originalContent.write(bytes);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                GZIPOutputStream gzipOut = new GZIPOutputStream(baos);
                originalContent.writeTo(gzipOut);
                gzipOut.finish();
                requestBuilder = MockMvcRequestBuilders.post(url)
                        .header("Content-Encoding", "gzip")
                        .contentType(new MediaType("application", "octet-stream"))
                        .content(baos.toByteArray());
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            requestBuilder = MockMvcRequestBuilders.post(url)
                    .content(bytes);
        }

        return exec(requestBuilder);
    }

    public String postStream(String url,InputStream is) throws Exception {
        return postStream(url,is,false);
    }

    public String postStream(String url,InputStream is,boolean gzip) throws Exception {
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

        return postBytes(url,bytes,gzip);
    }

    public String postFiles(String url,File[] files) throws Exception {
        return postFiles(url,new LinkedMultiValueMap<>(),files);
    }

    public String postFiles(String url,MultiValueMap<String, String> multiValueMap,File[] files) throws Exception {
        MockMultipartHttpServletRequestBuilder multipart = MockMvcRequestBuilders.multipart(url);
        for(File file:files){
            String contentType = new MimetypesFileTypeMap().getContentType(file);
            //System.out.println(contentType);
            multipart.file(new MockMultipartFile("files",file.getName(),contentType,new FileInputStream(file)));
        }
        MockHttpServletRequestBuilder requestBuilder = multipart
                .params(multiValueMap);

        return exec(requestBuilder);
    }
}
