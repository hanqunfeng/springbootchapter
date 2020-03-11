package org.example.model;/**
 * Created by hanqf on 2020/3/2 15:29.
 */


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author hanqf
 * @date 2020/3/2 15:29
 */
@Component
//spring-boot类
@ConfigurationProperties("database")
public class ConfigProperties {

    private String url;

    private String username;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "ConfigProperties{" +
                "url='" + url + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
