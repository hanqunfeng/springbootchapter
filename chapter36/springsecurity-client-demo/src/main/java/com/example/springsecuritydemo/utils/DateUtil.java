package com.example.springsecuritydemo.utils;

import org.apache.commons.lang3.ArrayUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * <p></p>
 * Created by hanqf on 2020/9/28 12:50.
 */


public class DateUtil {
    public static String DEFAULR_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static String DEFAULR_DATE_FORMAT = "yyyy-MM-dd";

    public static String getDateStrByTimeZoneId(String timeZoneId, String format) {
        TimeZone timeZone;
        String[] availableIDs = TimeZone.getAvailableIDs();

        if (ArrayUtils.contains(availableIDs, timeZoneId)) {
            timeZone = TimeZone.getTimeZone(timeZoneId);
        } else {
            timeZone = TimeZone.getDefault();
        }

        //System.out.println("timeZoneId == " + timeZone.getID());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        simpleDateFormat.setTimeZone(timeZone);
        return simpleDateFormat.format(new Date());
    }

    public static String getDateStrByDefault(String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(new Date());
    }

    public static void main(String[] args) {

        String timeZoneId = "Asia/Shanghai";
        System.out.println(getDateStrByTimeZoneId(timeZoneId, DateUtil.DEFAULR_DATE_FORMAT));

        //打印所有可用时区ID
        //Arrays.stream(TimeZone.getAvailableIDs()).sorted().forEach(System.out::println);

    }
}
