package org.vt_app;

import org.example.HttpClientUtil;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * <p></p>
 * Created by hanqf on 2020/5/26 15:21.
 */




public class VTAppTest {

    private static String VT_APP_URI = "https://vault.nqintelligence.com/BOSS_CS_VT/VTServlet_2-0";
    //private static String VT_APP_URI = "http://localhost:8080/BOSS_CS_VT/VTServlet_2-0";
    //private static String VT_APP_URI = "http://54.151.111.174:8010/BOSS_CS_VT/VTServlet_2-0";  //10.1.2.68  新的195
    //private static String VT_APP_URI = "http://3.101.86.80:8010/BOSS_CS_VT/VTServlet_2-0";  //10.1.0.201
    //private static String VT_APP_URI = "https://vt.easyxapp.com/BOSS_CS_VT_WRAP/forwardHttp";
    //private static String VT_APP_URI = "http://pl-hk-usacn.nq.com:8021/BOSS_CS_VT/VTServlet_2-0";

    /** 加密、解密使用的密钥 */
    public static final byte CRYPTKEY = 0x6E;

    private static void decrypt(byte[] bytes) {
        int len = bytes.length;
        for (int i = 0; i < len; i++) {
            bytes[i] = (byte) (bytes[i] ^ 0x6E);
        }
    }

    /**
     * <p>方法执行入口</p>
     *
     * @param requestBody
     * @author hanqf
     * 2020/5/26 15:28
     */
    private void exec(String requestBody) {

        byte[] bytes = requestBody.getBytes(StandardCharsets.UTF_8);

        decrypt(bytes);

        byte[] postBytes = HttpClientUtil.postBytes(VT_APP_URI, bytes);

        decrypt(postBytes);

        System.out.println(new String(postBytes, StandardCharsets.UTF_8));
    }

    @Test
    /**
     * <p>处理查询用户信息请求</p>
     * @author hanqf
     * 2020/5/26 15:23
     */
    public void testGetUserInfo() throws InterruptedException {

        //for (int i = 0; i < 200; i++) {
        //    new Thread() {
        //        @Override
        //        public void run() {
        //            for (int j = 0; j < 20; j++) {
        //                System.out.println("======" + j + "========");
        //                String requestBody = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>    <Request><Protocol>3.4.5</Protocol><Command>10</Command><Mandatory>0</Mandatory><ClientInfo><Model>U00tRzk1MDA=</Model><Language>1</Language><Country>86</Country><SoftLanguage>1</SoftLanguage><Business>130</Business><IMEI>354763080697654</IMEI><IMSI></IMSI><SC></SC><CI>2476</CI><APN>WIFI</APN><InstallReferrer><![CDATA[]]></InstallReferrer></ClientInfo><UserInfo><UID>1488668289</UID><Level>32</Level></UserInfo><AppInfo><OS>351</OS><ID>130</ID><Version>220282</Version><Partner>205863</Partner></AppInfo><SeqId></SeqId><PaymentResult>-1</PaymentResult><GoogleInApp><SignedData></SignedData><Signature></Signature><TransactionRef></TransactionRef></GoogleInApp></Request>\n";
        //                exec(requestBody);
        //            }
        //        }
        //    }.start();
        //}
        //
        //
        //Thread.sleep(300000);

        String requestBody = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>    <Request><Protocol>3.4.5</Protocol><Command>10</Command><Mandatory>0</Mandatory><ClientInfo><Model>U00tRzk1MDA=</Model><Language>1</Language><Country>86</Country><SoftLanguage>1</SoftLanguage><Business>130</Business><IMEI>354763080697654</IMEI><IMSI></IMSI><SC></SC><CI>2476</CI><APN>WIFI</APN><InstallReferrer><![CDATA[]]></InstallReferrer></ClientInfo><UserInfo><UID>1488668289</UID><Level>32</Level></UserInfo><AppInfo><OS>351</OS><ID>130</ID><Version>220282</Version><Partner>205863</Partner></AppInfo><SeqId></SeqId><PaymentResult>-1</PaymentResult><GoogleInApp><SignedData></SignedData><Signature></Signature><TransactionRef></TransactionRef></GoogleInApp></Request>\n";

        for (int j = 0; j < 10; j++) {
            System.out.println("======" + j + "========");
            exec(requestBody);
        }

        //String requestBody = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>    <Request><Protocol>3.4.5</Protocol><Command>10</Command><Mandatory>0</Mandatory><ClientInfo><Model>U00tRzk1MDA=</Model><Language>1</Language><Country>86</Country><SoftLanguage>1</SoftLanguage><Business>130</Business>" +
        //        //"<IMEI>0000000000000</IMEI>" + //imei不能没有值，否则会报连接失败的错误，无论几个0都会返回同一个uid,1494806148
        //        "<IMEI>1234567890112-1234567890112-abc-1234567890112</IMEI>" + //1494988015 imei不能没有值，否则会报连接失败的错误，无论几个0都会返回同一个uid,1494806148
        //        //"<IMEI>354763080697654</IMEI>" + //uid取决于imei的值
        //        "<IMSI></IMSI>" +
        //        "<SC></SC><CI>2476</CI><APN>WIFI</APN><InstallReferrer><![CDATA[]]></InstallReferrer></ClientInfo>" +
        //        //"<UserInfo>" +
        //        //"<UID>1488668289</UID>" + //即便传递了uid也会根据imei重新获取对应的uid
        //        //"<Level>32</Level>" +
        //        //"</UserInfo>" +
        //        "<AppInfo><OS>351</OS><ID>130</ID><Version>220282</Version><Partner>205863</Partner></AppInfo><SeqId></SeqId><PaymentResult>-1</PaymentResult><GoogleInApp><SignedData></SignedData><Signature></Signature><TransactionRef></TransactionRef></GoogleInApp></Request>\n";
        //exec(requestBody);

    }


    @Test
    public void test25_1(){
        String requestBody = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>   <Request>\n" +
                "  <Protocol>3.4.5</Protocol>\n" +
                "  <Command>25</Command>\n" +
                "  <Mandatory>0</Mandatory>\n" +
                "  <ClientInfo>\n" +
                "    <Model>U00tTjk2MDA=</Model>\n" +
                "    <Language>1</Language>\n" +
                "    <Country>86</Country>\n" +
                "    <SoftLanguage>1</SoftLanguage>\n" +
                "    <Business>130</Business>\n" +
                "    <IMEI>354635033189007</IMEI>\n" +
                "    <IMSI>26201367031272</IMSI>\n" +
                "    <SC>+8613800100500</SC>\n" +
                "    <CI>2476</CI>\n" +
                "    <APN>WIFI</APN>\n" +
                "    <InstallReferrer>\n" +
                "      <![CDATA[]]>\n" +
                "    </InstallReferrer>\n" +
                "  </ClientInfo>\n" +
                "  <UserInfo>\n" +
                "    <UID>733401164</UID>\n" +
                "  </UserInfo>\n" +
                "  <AppInfo>\n" +
                "    <OS>351</OS>\n" +
                "    <ID>130</ID>\n" +
                "    <Version>220281</Version>\n" +
                "    <Partner>205863</Partner>\n" +
                "  </AppInfo>\n" +
                "  <SubscribeScene>15</SubscribeScene>\n" +
                "  <SubscribeInfo></SubscribeInfo>\n" +
                "  <SupportGoogleInApp>3</SupportGoogleInApp>\n" +
                "  <GoogleInApp>\n" +
                "    <SignedData></SignedData>\n" +
                "    <Signature></Signature>\n" +
                "    <TransactionRef></TransactionRef>\n" +
                "  </GoogleInApp>\n" +
                "</Request>";
        for (int j = 0; j < 10; j++) {
            System.out.println("======" + j + "========");
            exec(requestBody);
        }
    }


    @Test
    public void test25_2(){
        String requestBody = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>   <Request>\n" +
                "  <Protocol>3.4.5</Protocol>\n" +
                "  <Command>25</Command>\n" +
                "  <Mandatory>0</Mandatory>\n" +
                "  <ClientInfo>\n" +
                "    <Model>U00tTjk2MDA=</Model>\n" +
                "    <Language>1</Language>\n" +
                "    <Country>86</Country>\n" +
                "    <SoftLanguage>1</SoftLanguage>\n" +
                "    <Business>130</Business>\n" +
                "    <IMEI>354635033189007</IMEI>\n" +
                "    <IMSI>26201367031272</IMSI>\n" +
                "    <SC>+8613800100500</SC>\n" +
                "    <CI>2476</CI>\n" +
                "    <APN>WIFI</APN>\n" +
                "    <InstallReferrer>\n" +
                "      <![CDATA[]]>\n" +
                "    </InstallReferrer>\n" +
                "  </ClientInfo>\n" +
                "  <UserInfo>\n" +
                "    <UID>733401164</UID>\n" +
                "  </UserInfo>\n" +
                "  <AppInfo>\n" +
                "    <OS>351</OS>\n" +
                "    <ID>130</ID>\n" +
                "    <Version>220281</Version>\n" +
                "    <Partner>205863</Partner>\n" +
                "  </AppInfo>\n" +
                "  <SubscribeScene>42</SubscribeScene>\n" +
                "  <SubscribeInfo>\n" +
                "    <Option>335_9290</Option>\n" +
                "  </SubscribeInfo>\n" +
                "  <SupportGoogleInApp>3</SupportGoogleInApp>\n" +
                "  <GoogleInApp>\n" +
                "    <SignedData></SignedData>\n" +
                "    <Signature></Signature>\n" +
                "    <TransactionRef></TransactionRef>\n" +
                "  </GoogleInApp>\n" +
                "</Request>";
        for (int j = 0; j < 10; j++) {
            System.out.println("======" + j + "========");
            exec(requestBody);
        }
    }


    public  String generalConnect(String sURL) throws IOException {
        StringBuffer sResult = new StringBuffer();
        BufferedReader reader = null;
        HttpURLConnection uc = null;
        try {
            URL url = new URL(sURL);
            uc = (HttpURLConnection) url.openConnection();
            uc.setConnectTimeout(2700);
            uc.setReadTimeout(2700);
            // 接收应答信息
            reader = new BufferedReader(new InputStreamReader(uc.getInputStream()));
            String curLine = "";
            while((curLine = reader.readLine())!=null){
                String reads = new String(curLine.getBytes(), 0, curLine.getBytes().length, "UTF-8");
                sResult.append(reads).append("\r\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            if(reader!=null){
                reader.close();
                reader = null;
            }
            if(uc!=null){
                uc.disconnect();
                uc = null;
            }
        }
        return sResult.toString().trim();
    }

    @Test
    public void testJiFei() throws IOException {
        String url = "http://blyt.netqin.com:8297/BOSS_AS/internal.htm?cmd=payCenterServiceAdapter&methodName=gatePayEx&paymid=1379467102&langid=1&paymethod=50&currency=USD&price=3.99&discountPrice=3.99&country=DE&symbol=USD&busid=130&platid=351&coopid=205863";
        String response = generalConnect(url);
        System.out.println(response);

        Map<String,String> map = new HashMap<String,String>();
        if(response!=null && !"".equals(response)){
            String results [] = response.split("\r\n");
            for(String property : results){
                String key =property.substring(0,property.indexOf("="));
                String value =property.substring(property.indexOf("=")+1);
                map.put(key,value);
            }
        }
        System.out.println(map);
    }
}
