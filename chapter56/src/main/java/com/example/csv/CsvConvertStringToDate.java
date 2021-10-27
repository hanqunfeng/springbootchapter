package com.example.csv;

import java.text.SimpleDateFormat;
import java.util.Map;

/**
 * <h1></h1>
 * Created by hanqf on 2021/10/14 14:58.
 */


public class CsvConvertStringToDate extends AbstractConvertCsvBase {
    @Override
    public Object convert(Map<String, String> params) throws Exception {
        String value = params.get("value");
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        return sf.parse(value);
    }
}
