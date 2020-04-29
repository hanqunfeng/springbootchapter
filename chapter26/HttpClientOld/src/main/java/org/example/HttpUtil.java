package org.example;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;


/**
 * <p></p>
 * Created by hanqf on 2020/4/18 14:54.
 */

@Slf4j
public class HttpUtil {
    private static HttpClient client = new HttpClient();

    static {
        //设置连接超时
        client.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
        //设置读数据超时
        client.getHttpConnectionManager().getParams().setSoTimeout(5000);
    }

    public static String execute(HttpMethodBase httpMethodBase) {
        StringBuffer stringBuffer = new StringBuffer();
        try {
            log.info(String.format("请求地址: [%s]", httpMethodBase.getURI().toString()));

            for (Header header : httpMethodBase.getRequestHeaders()) {
                stringBuffer.append(header.toString()).append(",");
            }
            log.info(String.format("请求头信息: [%s]", stringBuffer.toString()));
            log.info(String.format("请求参数: [%s]", httpMethodBase.getURI().getQuery()));

        }catch (Exception e){

        }
        String responseResult = null;
        try {
            //允许客户端或服务器中任何一方关闭底层的连接双方都会要求在处理请求后关闭它们的TCP连接
            httpMethodBase.setRequestHeader("Connection", "close");
            long t1 = System.nanoTime();//请求发起的时间
            client.executeMethod(httpMethodBase);
            log.debug("响应状态为:" + httpMethodBase.getStatusLine());
            long t2 = System.nanoTime();//收到响应的时间
            responseResult = httpMethodBase.getResponseBodyAsString();
            log.debug("响应内容为:" + responseResult);

            stringBuffer = new StringBuffer();
            for (Header header : httpMethodBase.getResponseHeaders()) {
                stringBuffer.append(header.toString()).append(",");
            }
            log.info(String.format("响应头信息: [%s]", stringBuffer.toString()));

            log.info(String.format("执行时间: [%.1fms]", (t2 - t1) / 1e6d));
        } catch (IOException e) {
            log.info("Get responseResult：", e);
            e.printStackTrace();
        } finally {
            httpMethodBase.releaseConnection();
        }

        return responseResult;
    }

    public static byte[] executeBytes(HttpMethodBase httpMethodBase) {
        StringBuffer stringBuffer = new StringBuffer();
        try {
            log.info(String.format("请求地址: [%s]", httpMethodBase.getURI().toString()));

            for (Header header : httpMethodBase.getRequestHeaders()) {
                stringBuffer.append(header.toString()).append(",");
            }
            log.info(String.format("请求头信息: [%s]", stringBuffer.toString()));
            log.info(String.format("请求参数: [%s]", httpMethodBase.getURI().getQuery()));

        }catch (Exception e){

        }
        byte[] bytes = null;
        try {
            //允许客户端或服务器中任何一方关闭底层的连接双方都会要求在处理请求后关闭它们的TCP连接
            httpMethodBase.setRequestHeader("Connection", "close");
            long t1 = System.nanoTime();//请求发起的时间
            client.executeMethod(httpMethodBase);
            log.debug("响应状态为:" + httpMethodBase.getStatusLine());
            long t2 = System.nanoTime();//收到响应的时间
            bytes = httpMethodBase.getResponseBody();
            log.debug("响应内容字节数组长度:" + bytes.length);

            stringBuffer = new StringBuffer();
            for (Header header : httpMethodBase.getResponseHeaders()) {
                stringBuffer.append(header.toString()).append(",");
            }
            log.info(String.format("响应头信息: [%s]", stringBuffer.toString()));
            log.info(String.format("执行时间: [%.1fms]", (t2 - t1) / 1e6d));

        } catch (IOException e) {
            log.info("Get responseResult：", e);
            e.printStackTrace();
        } finally {
            httpMethodBase.releaseConnection();
        }

        return bytes;
    }

    public static String get(String url) {
        return get(url,new HashMap<>());
    }

    public static String get(String url, Map<String, String> params) {
        GetMethod method = new GetMethod(url);
        NameValuePair[] nameValuePairs = new NameValuePair[params.size()];
        int i = 0;
        for (String key : params.keySet()) {
            NameValuePair nameValuePair = new NameValuePair(key, params.get(key));
            nameValuePairs[i] = nameValuePair;
            i++;
        }
        method.setQueryString(nameValuePairs);
        return execute(method);
    }

    public static String post(String url) {
        return post(url,new HashMap<>());
    }
    public static String post(String url, Map<String, String> params) {
        PostMethod method = new PostMethod(url);
        NameValuePair[] nameValuePairs = new NameValuePair[params.size()];
        int i = 0;
        for (String key : params.keySet()) {
            NameValuePair nameValuePair = new NameValuePair(key, params.get(key));
            nameValuePairs[i] = nameValuePair;
            i++;
        }
        method.setRequestBody(nameValuePairs);
        return execute(method);
    }

    public static String post(String url, String json) {
        PostMethod method = new PostMethod(url);
        method.addRequestHeader("Content-Type", "application/json;charset=utf8");
        RequestEntity entity = null;
        try {
            entity = new StringRequestEntity(json, "application/json", "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        method.setRequestEntity(entity);
        return execute(method);
    }
}
