package com.example.oauth2resourceserverdemo.security.jwt;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Paths;

/**
 * Jwt工具类
*/
@Data
@ConfigurationProperties(prefix = "jwt")
@Component
public class JwtTokenProperties {

    /**
     * 密钥类型:secret，jks
    */
    private String type;

    /**
     * 密钥
     */
    private String secret;

    /**
     * 公钥文件路径
    */
    private String publicKeyFile;


    /**
     * 返回公钥字符串
    */
    public String getPublicKeyStr() {
        Resource resource;
        if (publicKeyFile.startsWith("classpath:")) {
            publicKeyFile = publicKeyFile.replace("classpath:", "");
            resource = new ClassPathResource(publicKeyFile);
        } else {
            resource = new PathResource(Paths.get(publicKeyFile));
        }
        try(InputStream inputStream = resource.getInputStream();){
            return getStreamToStr(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将一个输入流转化为字符串
     *
     * @param tInputStream
     * @return
     */
    public static String getStreamToStr(InputStream tInputStream) {
        if (tInputStream != null) {
            try {
                BufferedReader tBufferedReader = new BufferedReader(new InputStreamReader(tInputStream));
                StringBuffer tStringBuffer = new StringBuffer();
                String sTempOneLine;
                while ((sTempOneLine = tBufferedReader.readLine()) != null) {
                    tStringBuffer.append(sTempOneLine);
                }
                return tStringBuffer.toString();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

}
