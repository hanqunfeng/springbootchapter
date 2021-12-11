package com.example;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * <h1></h1>
 * Created by hanqf on 2021/12/11 17:28.
 */


public class DateUtilTest {

    @Test
    public void DateTime(){

        //获取当前时间
        DateTime dateTime = DateUtil.date();
        System.out.println(dateTime);
        System.out.println(dateTime.toString("yyyy/MM/dd HH:mm:ss"));
        System.out.println(dateTime.getTimeZone());
        Date date = dateTime.toJdkDate();
        java.sql.Date sqlDate = dateTime.toSqlDate();
        LocalDateTime localDateTime = dateTime.toLocalDateTime();

        final long time = dateTime.getTime();

        System.out.println(dateTime.year());
        //月份从0开始计算
        System.out.println(dateTime.month() + 1);
        System.out.println(dateTime.dayOfMonth());

        //是否闰年
        System.out.println(dateTime.isLeapYear());

        //设置时间
        dateTime.setTime(System.currentTimeMillis());

        //设置日期为当前日期加2天
        System.out.println(dateTime.setField(DateField.DAY_OF_MONTH, dateTime.dayOfMonth()+2));


        //格式化
        System.out.println(DateUtil.format(new Date(), "yyyy/MM/dd"));

        final DateTime now = DateTime.now();

        final DateTime of = DateTime.of(System.currentTimeMillis());

    }
}
