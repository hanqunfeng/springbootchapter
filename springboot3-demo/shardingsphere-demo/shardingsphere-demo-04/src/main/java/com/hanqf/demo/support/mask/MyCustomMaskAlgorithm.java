package com.hanqf.demo.support.mask;

import org.apache.shardingsphere.mask.spi.MaskAlgorithm;

import java.util.Properties;

/**
 * 自定义脱敏算法
 * Created by hanqf on 2025/9/1 15:29.
 */


public final class MyCustomMaskAlgorithm implements MaskAlgorithm<String, String> {

    private String replaceChar = "*";

    @Override
    public void init(Properties props) {
        if (props.containsKey("replace-char")) {
            this.replaceChar = props.getProperty("replace-char");
        }
    }

    @Override
    public String mask(String plainValue) {
        if (plainValue == null) {
            return null;
        }
        // 自定义脱敏逻辑：例如全部替换成指定字符
        return plainValue.replaceAll(".", replaceChar);
    }

    @Override
    public String getType() {
        // 配置文件中引用该算法的 type 就是这里的返回值
        return "MY_CUSTOM_MASK";
    }
}
