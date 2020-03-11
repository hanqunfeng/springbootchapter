package com.example.util;/**
 * Created by hanqf on 2020/3/9 16:50.
 */


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author hanqf
 * @date 2020/3/9 16:50
 */

@Component
//spring-boot类
@ConfigurationProperties("spring.jpa")
public class JpaProperties {
    private boolean showSql;
    private boolean generateDdl;
    private String formatSql;

    public boolean isShowSql() {
        return showSql;
    }

    public void setShowSql(boolean showSql) {
        this.showSql = showSql;
    }

    public boolean isGenerateDdl() {
        return generateDdl;
    }

    public void setGenerateDdl(boolean generateDdl) {
        this.generateDdl = generateDdl;
    }

    public String getFormatSql() {
        return formatSql;
    }

    public void setFormatSql(String formatSql) {
        this.formatSql = formatSql;
    }
}
