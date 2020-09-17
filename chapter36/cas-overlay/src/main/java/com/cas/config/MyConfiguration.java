package com.cas.config;

import com.cas.controller.CaptchaController;
import com.cas.controller.ServiceController;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.*;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

/**
 * <p>本地配置类</p>
 * Created by hanqf on 2020/9/11 14:25.
 * <p>
 * 参考：https://blog.csdn.net/yelllowcong/article/details/79250841
 */

@Configuration("myConfiguration")
//此处把CasConfigurationProperties注入到当前容器中，方便其它类使用，该属性类中为application.properties中配置的以cas开头的所有属性
@EnableConfigurationProperties(CasConfigurationProperties.class)
public class MyConfiguration {
    /**
     * 如果客户端地址为http，而非https，则需要在启动项目时执行该方法
     * 参考：https://blog.csdn.net/it_dx/article/details/78866711
     * CertificateException: No subject alternative DNS name matching XXX found
     *
     * 本来只需要客户端添加即可，不过这里通过如下地址登录后，也会报错，所以也就加上了
     * https://cas.example.org:8443/cas/status/dashboard
     */
    static {
        try {
            // Create a trust manager that does not validate certificate chains
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                @Override
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }
            };

            // Install the all-trusting trust manager
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            // Create all-trusting host name verifier
            HostnameVerifier allHostsValid = new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };

            // Install the all-trusting host verifier
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
    }

    // 注册bean到spring容器
    //注册码
    @Bean
    @ConditionalOnMissingBean(name = "captchaController")
    public CaptchaController captchaController() {
        return new CaptchaController();
    }

    //动态service
    @Bean
    @ConditionalOnMissingBean(name = "serviceController")
    public ServiceController serviceController() {
        return new ServiceController();
    }


    //session共享，参考：https://www.it1352.com/995116.html
    //@Value("${tomcat.jvmroute}")
    //private String jvmRoute;
    //
    //@PostConstruct
    //public void setJvmRoute() {
    //    // embedded tomcat uses this property to set the jvmRoute
    //    System.setProperty("jvmRoute", jvmRoute);
    //}

}
