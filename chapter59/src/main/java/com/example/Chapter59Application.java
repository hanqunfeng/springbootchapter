package com.example;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;

@Slf4j
@SpringBootApplication
public class Chapter59Application {

    @Primary
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate(generateHttpsRequestFactory());
    }

    public HttpComponentsClientHttpRequestFactory generateHttpsRequestFactory() {
        try {
            //解决https不能访问的问题
            TrustStrategy acceptingTrustStrategy = (x509Certificates, authType) -> true;
            SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build();
            SSLConnectionSocketFactory connectionSocketFactory =
                    new SSLConnectionSocketFactory(sslContext, new NoopHostnameVerifier());

            HttpClientBuilder httpClientBuilder = HttpClients.custom()
                    //修改默认的 cookie 策略，改为 STANDARD_STRICT 或者 STANDARD
                    //https://www.cnblogs.com/lionsblog/p/10365529.html
                    //解决：HttpClient 报错 Invalid cookie header， Invalid 'expires' attribute: Thu, 01 Jan 1970 00:00:00 GMTHttpClient 报错 Invalid cookie header， Invalid 'expires' attribute: Thu, 01 Jan 1970 00:00:00 GMT
                    .setDefaultRequestConfig(RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build());
            httpClientBuilder.setSSLSocketFactory(connectionSocketFactory);
            CloseableHttpClient httpClient = httpClientBuilder.build();
            HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
            factory.setHttpClient(httpClient);
            factory.setConnectTimeout(10 * 1000);
            factory.setReadTimeout(30 * 1000);
            return factory;
        } catch (Exception e) {
            log.error("创建HttpsRestTemplate失败", e);
            throw new RuntimeException("创建HttpsRestTemplate失败", e);
        }

    }


    public static void main(String[] args) {
        SpringApplication.run(Chapter59Application.class, args);
    }

}
