package com.example.csv;

import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * <h1></h1>
 * Created by hanqf on 2021/10/14 11:07.
 */


public abstract class AbstractConvertCsvBase {
    private static final String SPLIT = "'";

    /**
     * 转换
     * @param params 参数中必须有key为"value"
     * @return
     */
    public Object startConvert(Map<String,String> params) throws Exception {
        String value = params.get("value");
        if (StringUtils.isNotBlank(value) && SPLIT.equals(value.substring(0,1))){
            value = value.substring(1);
        }
        if (StringUtils.isBlank(value)){
            return null;
        }
        params.put("value",value);
        return convert(params);
    }

    /**
     * 转换方法
     * @param params
     * @return
     */
    public abstract Object convert(Map<String,String> params) throws Exception;

    public static class Converter extends AbstractConvertCsvBase{
        public static Converter newInstance() {
            return new Converter();
        }
        @Override
        public Object convert(Map<String,String> params) {
            return params.get("value");
        }
    }
}
