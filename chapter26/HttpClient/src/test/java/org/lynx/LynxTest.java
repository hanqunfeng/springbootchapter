package org.lynx;

import org.example.HttpClientUtil;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.zip.ZipException;

/**
 * <p></p>
 * Created by hanqf on 2020/5/26 15:18.
 */


public class LynxTest {

    //private static String LYNX_CLOUD_URL = "https://lynx.picooapp.com/lynx/cloud";
    //private static String LYNX_CLOUD_URL = "http://localhost:8080/lynx/cloud";
    private static String LYNX_CLOUD_URL = "https://lynx-di1q2xdk.tunnelto.dev/lynx/cloud";

    /**
     * <p>方法执行入口</p>
     * @author hanqf
     * 2020/5/26 15:29
     * @param requestBody
     */
    private void exec(String requestBody) throws ZipException {

        ZipUtil zipUtil = new ZipUtil();
        byte[] bytes = requestBody.getBytes(StandardCharsets.UTF_8);

        bytes = zipUtil.zipData(bytes);

        byte[] postBytes = HttpClientUtil.postBytes(LYNX_CLOUD_URL, bytes);

        bytes = zipUtil.unzipData(postBytes);

        System.out.println(new String(bytes, StandardCharsets.UTF_8));
    }

    @Test
    /**
     * <p>用户订购列表，基于此判断用户状态是否为会员</p>
     * @author hanqf
     * 2020/5/26 15:28
     */
    public void testLynx() throws ZipException {
        String requestBody = "{\"command\":\"purchaselist\",\"userInfo\":{\"userName\":\"maguangkun2016@gmail.com\",\"accessToken\":\"a417ad0dd9174a45b1415beea36015de#974512#maguangkun2016@gmail.com\"},\"softwareInfo\":{\"version\":\"1\",\"os\":\"351\",\"partner\":\"1\",\"language\":\"en_US\",\"protocol\":\"2.0\",\"imsi\":\"\"}}";
        exec(requestBody);
    }

    @Test
    public void testLogin() throws ZipException {
        String requestBody = "{\"command\":\"nqlogin\",\"userInfo\":{\"userName\":\"maguangkun2016@gmail.com\"},\"password\":\"694e17fcad0138f34597e064cd1d5a1d3e519685\",\"softwareInfo\":{\"version\":\"1\",\"os\":\"351\",\"partner\":\"1\",\"language\":\"en_US\",\"protocol\":\"2.0\",\"imsi\":\"\"}}";
        exec(requestBody);

    }

    @Test
    public void testRegister() throws ZipException {
        String requestBody = "{\"command\":\"register\",\"userInfo\":{\"userName\":\"maguangkun2017@gmail.com\"},\"password\":\"694e17fcad0138f34597e064cd1d5a1d3e519685\",\"softwareInfo\":{\"version\":\"1\",\"os\":\"351\",\"partner\":\"1\",\"language\":\"en_US\",\"protocol\":\"2.0\",\"imsi\":\"\"}}";
        exec(requestBody);

    }
}
