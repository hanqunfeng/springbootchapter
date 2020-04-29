package org.example;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.*;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.params.HttpMethodParams;

import java.io.*;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPOutputStream;


/**
 * <p></p>
 * Created by hanqf on 2020/4/18 14:54.
 */

@Slf4j
public class HttpUtil {
    /**
     * 创建HttpClient客户端
     */
    private static HttpClient client = new HttpClient();

    /**
     * 初始化客户端
     */
    static {
        //设置连接超时
        client.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
        //设置读数据超时
        client.getHttpConnectionManager().getParams().setSoTimeout(5000);
    }

    /**
     * <p>请求的执行方法，需要提前封装好httpMethodBase对象，如请求url和请求参数</p>
     * 返回字符串
     *
     * @param httpMethodBase
     * @return java.lang.String
     * @author hanqf
     * 2020/4/29 17:04
     */
    public static String execute(HttpMethodBase httpMethodBase) {
        StringBuffer stringBuffer = new StringBuffer();
        try {
            log.info(String.format("请求地址: [%s]", httpMethodBase.getURI().toString()));

            for (Header header : httpMethodBase.getRequestHeaders()) {
                stringBuffer.append(header.getName()).append(":").append(header.getValue()).append(",");
            }
            log.info(String.format("请求头信息: [%s]", stringBuffer.toString()));
            log.info(String.format("请求参数: [%s]", httpMethodBase.getURI().getQuery()));

        } catch (Exception e) {

        }
        String responseResult = null;
        try {
            //允许客户端或服务器中任何一方关闭底层的连接双方都会要求在处理请求后关闭它们的TCP连接
            httpMethodBase.setRequestHeader("Connection", "close");
            //重试机制，默认3次
            httpMethodBase.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
            long t1 = System.nanoTime();//请求发起的时间
            client.executeMethod(httpMethodBase);
            log.debug("响应状态为:" + httpMethodBase.getStatusLine());
            long t2 = System.nanoTime();//收到响应的时间
            responseResult = httpMethodBase.getResponseBodyAsString();
            log.debug("响应内容为:" + responseResult);

            stringBuffer = new StringBuffer();
            for (Header header : httpMethodBase.getResponseHeaders()) {
                stringBuffer.append(header.getName()).append(":").append(header.getValue()).append(",");
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

    /**
     * <p>返回字节数组</p>
     *
     * @param httpMethodBase
     * @return byte[]
     * @author hanqf
     * 2020/4/29 17:04
     */
    public static byte[] executeBytes(HttpMethodBase httpMethodBase) {
        StringBuffer stringBuffer = new StringBuffer();
        try {
            log.info(String.format("请求地址: [%s]", httpMethodBase.getURI().toString()));

            for (Header header : httpMethodBase.getRequestHeaders()) {
                stringBuffer.append(header.getName()).append(":").append(header.getValue()).append(",");
            }
            log.info(String.format("请求头信息: [%s]", stringBuffer.toString()));
            log.info(String.format("请求参数: [%s]", httpMethodBase.getURI().getQuery()));

        } catch (Exception e) {

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
                stringBuffer.append(header.getName()).append(":").append(header.getValue()).append(",");
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
        return get(url, new HashMap<>());
    }

    /**
     * <p>get请求</p>
     *
     * @param url
     * @param params
     * @return java.lang.String
     * @author hanqf
     * 2020/4/29 17:05
     */
    public static String get(String url, Map<String, Object> params) {
        GetMethod method = new GetMethod(url);
        NameValuePair[] nameValuePairs = new NameValuePair[params.size()];
        int i = 0;
        for (String key : params.keySet()) {
            NameValuePair nameValuePair = new NameValuePair(key, params.get(key).toString());
            nameValuePairs[i] = nameValuePair;
            i++;
        }
        method.setQueryString(nameValuePairs);
        return execute(method);
    }

    public static byte[] getBytes(String url, Map<String, Object> params) {
        GetMethod method = new GetMethod(url);
        NameValuePair[] nameValuePairs = new NameValuePair[params.size()];
        int i = 0;
        for (String key : params.keySet()) {
            NameValuePair nameValuePair = new NameValuePair(key, params.get(key).toString());
            nameValuePairs[i] = nameValuePair;
            i++;
        }
        method.setQueryString(nameValuePairs);
        return executeBytes(method);
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
     * 2020/4/29 17:05
     */
    public static String post(String url, Map<String, Object> params) {
        PostMethod method = new PostMethod(url);
        method.addRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf8");
        NameValuePair[] nameValuePairs = new NameValuePair[params.size()];
        int i = 0;
        for (String key : params.keySet()) {
            NameValuePair nameValuePair = new NameValuePair(key, params.get(key).toString());
            nameValuePairs[i] = nameValuePair;
            i++;
        }
        method.setRequestBody(nameValuePairs);
        return execute(method);
    }

    public static byte[] postBytes(String url, Map<String, Object> params) {
        PostMethod method = new PostMethod(url);
        method.addRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf8");
        NameValuePair[] nameValuePairs = new NameValuePair[params.size()];
        int i = 0;
        for (String key : params.keySet()) {
            NameValuePair nameValuePair = new NameValuePair(key, params.get(key).toString());
            nameValuePairs[i] = nameValuePair;
            i++;
        }
        method.setRequestBody(nameValuePairs);
        return executeBytes(method);
    }

    public static String post(String url, String json) {
        return post(url, json, false);
    }

    /**
     * <p>post请求，json</p>
     *
     * @param url
     * @param json
     * @return java.lang.String
     * @author hanqf
     * 2020/4/29 17:05
     */
    public static String post(String url, String json, boolean gzip) {
        PostMethod method = new PostMethod(url);
        method.addRequestHeader("Content-Type", "application/json;charset=utf8");
        RequestEntity entity = null;
        try {
            if (gzip) {
                method.addRequestHeader("Content-Encoding", "gzip");
                ByteArrayOutputStream originalContent = new ByteArrayOutputStream();
                originalContent.write(json.getBytes("utf-8"));
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                GZIPOutputStream gzipOut = new GZIPOutputStream(baos);
                originalContent.writeTo(gzipOut);
                gzipOut.finish();
                entity = new ByteArrayRequestEntity(baos.toByteArray(), "text/plain;chaset=utf-8");
            } else {
                entity = new StringRequestEntity(json, "application/json", "UTF-8");
            }

        } catch (Exception e) {
            log.info("Get responseResult：", e);
            e.printStackTrace();
        }
        method.setRequestEntity(entity);
        return execute(method);
    }

    public static String postFile(String url, File[] files) {
        return postFile(url, new HashMap<>(), files);
    }

    /**
     * <p>post请求，上传文件</p>
     *
     * @param url
     * @param params
     * @param files
     * @return java.lang.String
     * @author hanqf
     * 2020/4/29 19:11
     */
    public static String postFile(String url, Map<String, Object> params, File[] files) {
        PostMethod method = new PostMethod(url);
        Part[] parts = new Part[files.length + params.size()];
        int i = 0;
        for (; i < files.length; i++) {
            try {
                //解决文件名中文乱码的问题，服务器端要用URLEncoder.decode方法进行解码
                parts[i] = new FilePart("files", URLEncoder.encode(files[i].getName(), "utf-8"), files[i]);
            } catch (FileNotFoundException | UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        for (String key : params.keySet()) {
            parts[i] = new StringPart(key, params.get(key).toString(), "utf-8");
            i++;
        }

        MultipartRequestEntity multipartRequestEntity = new MultipartRequestEntity(parts, method.getParams());

        method.setRequestEntity(multipartRequestEntity);
        return execute(method);
    }

    public static String postBytes(String url, byte[] bytes) {
        return postBytes(url, bytes, false);
    }

    /**
     * <p>post请求，byte数组</p>
     *
     * @param url
     * @param bytes
     * @param gzip
     * @return java.lang.String
     * @author hanqf
     * 2020/4/29 19:22
     */
    public static String postBytes(String url, byte[] bytes, boolean gzip) {
        PostMethod method = new PostMethod(url);
        RequestEntity entity = null;
        try {
            if (gzip) {
                method.addRequestHeader("Content-Encoding", "gzip");
                ByteArrayOutputStream originalContent = new ByteArrayOutputStream();
                originalContent.write(bytes);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                GZIPOutputStream gzipOut = new GZIPOutputStream(baos);
                originalContent.writeTo(gzipOut);
                gzipOut.finish();
                entity = new ByteArrayRequestEntity(baos.toByteArray(), "text/plain;chaset=utf-8");
            } else {
                entity = new ByteArrayRequestEntity(bytes, "text/plain;chaset=utf-8");
            }

        } catch (Exception e) {
            log.info("Get responseResult：", e);
            e.printStackTrace();
        }

        method.setRequestEntity(entity);
        return execute(method);
    }

    public static String postInputStream(String url, InputStream is) {
        return postInputStream(url, is, false);
    }

    /**
     * <p>post请求，流</p>
     *
     * @param url
     * @param is
     * @param gzip
     * @return java.lang.String
     * @author hanqf
     * 2020/4/29 19:36
     */
    public static String postInputStream(String url, InputStream is, boolean gzip) {
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


}
