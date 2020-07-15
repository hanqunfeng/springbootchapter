package org.cloud;

import org.example.HttpClientUtil;
import org.junit.Test;
import org.lynx.ZipUtil;

import java.nio.charset.StandardCharsets;
import java.util.zip.ZipException;

/**
 * <p></p>
 * Created by hanqf on 2020/6/6 10:15.
 */


public class CloudTest {

    private static String CLOUD_URI = "https://cloudapp.nq.com/BOSS_CS_CLOUD/cloud";




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

        byte[] postBytes = HttpClientUtil.postBytes(CLOUD_URI, bytes);

        bytes = zipUtil.unzipData(postBytes);

        System.out.println(new String(bytes, StandardCharsets.UTF_8));
    }

    @Test
    /**
     * <p>用户注册</p>
     * @author hanqf
     * 2020/5/26 15:28
     */
    public void testRegister() throws ZipException {
        for(int i=0;i<10;i++) {
            String email = "abc@163.com" + i;
            String requestBody = "{\"command\":\"register\",\"userInfo\":{\"uid\":\"1488668289\",\"userName\":\""+email+"\",\"level\":32},\"password\":\"SHA1-694e17fcad0138f34597e064cd1d5a1d3e519685\",\"softwareInfo\":{\"version\":\"220282\",\"os\":\"351\",\"partner\":\"205863\",\"language\":\"zh_CN\"}}\n";
            exec(requestBody);
        }
    }


    @Test
    public void testLogin() throws ZipException {
        for(int i=0;i<10;i++) {
            System.out.println(String.format("i==%d",i));
            String requestBody = "{\"command\":\"nqlogin\",\"userInfo\":{\"uid\":\"1492257658\",\"userName\":\"hanqf2008@163.com\",\"level\":4},\"password\":\"SHA1-7c4a8d09ca3762af61e59520943dc26494f8941b\",\"softwareInfo\":{\"version\":\"220267\",\"os\":\"351\",\"partner\":\"205863\",\"language\":\"zh_CN\"}}";
            exec(requestBody);
        }

    }



}
