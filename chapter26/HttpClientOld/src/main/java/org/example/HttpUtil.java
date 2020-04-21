package org.example;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;

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

    public static String post(String url, Map<String,String> params){
        PostMethod method = new PostMethod(url);
        NameValuePair[] nameValuePairs = new NameValuePair[params.size()];
        int i = 0;
        for(String key : params.keySet()){
            NameValuePair nameValuePair = new NameValuePair(key, params.get(key));
            nameValuePairs[i] = nameValuePair;
            i++;
        }

        try {
            //允许客户端或服务器中任何一方关闭底层的连接双方都会要求在处理请求后关闭它们的TCP连接
            method.setRequestHeader("Connection", "close");
            method.setRequestBody(nameValuePairs);
            client.executeMethod(method);
            String responseResult = method.getResponseBodyAsString();
            log.info("Get responseResult：" + responseResult);
            return responseResult;
        } catch (Exception e) {
            log.info("Get responseResult：", e);
            e.printStackTrace();
        } finally {
            method.releaseConnection();
        }
        return null;
    }
}
