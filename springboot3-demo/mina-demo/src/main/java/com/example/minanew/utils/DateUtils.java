package com.example.minanew.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 日期Util类
 *
 * @author zhoushuhua
 */
public class DateUtils {

    private static Logger logger = LoggerFactory.getLogger(DateUtils.class);

    public static final String defaultDatePattern = "yyyy-MM-dd";

    public static final String FullDatePattern = "yyyy-MM-dd HH:mm:ss";

    public static final String FullHourDatePattern = "yyyy-MM-dd HH:00:00";

    public static final String HFDatePattern = "yyyy-MM-dd HH:mm";

    public static final String HDatePattern = "yyyy-MM-dd HH:00:00";

    public static final String DATE_FORMAT_PATTERN = "MM/dd/yyyy";

    static {
        // 尝试试从messages_zh_Cn.properties中获取defaultDatePattner.
        try {
            // Locale locale = LocaleContextHolder.getLocale();
            // defaultDatePattern =
            // ResourceBundle.getBundle(Constants.MESSAGE_BUNDLE_KEY,
            // locale).getString("date.default_format");
        } catch (MissingResourceException mse) {
            // do nothing
        }
    }

    /**
     * 获取指定时间对应的毫秒数
     *
     * @param time "HH:mm:ss"
     * @return
     */

    public static long getTimeMillis(String time) {

        try {

            DateFormat dateFormat = new SimpleDateFormat("yy-MM-dd HH:mm:ss");

            DateFormat dayFormat = new SimpleDateFormat("yy-MM-dd");

            Date curDate = dateFormat.parse(dayFormat.format(new Date()) + " " + time);

            return curDate.getTime();

        } catch (ParseException e) {

            e.printStackTrace();

        }

        return 0;

    }

    public static Long obtTimeMillis(String time) {
        try {
            DateFormat dateFormat = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
            Date curDate = dateFormat.parse(time);
            return curDate.getTime();
        } catch (ParseException e) {
            logger.error("获取绝对秒出错", e);
        }
        return 0L;
    }

    /**
     * 获取  FullDatePattern = "yyyy-MM-dd HH:mm:ss";
     *
     * @return String
     */
    public static String getStringDate() {
        return new SimpleDateFormat(FullDatePattern).format(new Date());
    }

    /**
     * 获得默认的 date pattern
     *
     * @return String
     */
    public static String getDatePattern() {
        return defaultDatePattern;
    }

    /**
     * 返回预设Format的当前日期字符串
     *
     * @return String
     */
    public static String getToday() {
        return format(now());
    }

    /**
     * 返回当前时间
     *
     * @return Date实例
     */
    public static Date now() {
        return nowCal().getTime();
    }

    /**
     * 当前时间
     *
     * @return Calendar实例
     */
    public static Calendar nowCal() {
        return Calendar.getInstance();
    }

    /**
     * Date型转化到Calendar型
     *
     * @param date
     * @return Calendar
     */
    public static Calendar date2Cal(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c;
    }

    /**
     * 当前时间的下一天
     *
     * @return Calendar
     */
    public static Calendar nextDay() {
        return nextDay(nowCal());
    }

    /**
     * 当前时间的下一月
     *
     * @return Calendar
     */
    public static Calendar nextMonth() {
        return nextMonth(nowCal());
    }

    /**
     * 当前时间的下一年
     *
     * @return Calendar
     */
    public static Calendar nextYear() {
        return nextMonth(nowCal());
    }

    /**
     * 下一天
     *
     * @param cal
     * @return Calendar
     */
    public static Calendar nextDay(Calendar cal) {
        if (cal == null) {
            return null;
        }
        return afterDays(cal, 1);
    }

    /**
     * 下一月
     *
     * @param cal
     * @return Calendar
     */
    public static Calendar nextMonth(Calendar cal) {
        if (cal == null) {
            return null;
        }
        return afterMonths(cal, 1);
    }

    /**
     * 下一年
     *
     * @param cal
     * @return Calendar
     */
    public static Calendar nextYear(Calendar cal) {
        if (cal == null) {
            return null;
        }
        return afterYesrs(cal, 1);
    }

    /**
     * 后n天
     *
     * @param cal
     * @param n
     * @return Calendar
     */
    public static Calendar afterDays(Calendar cal, int n) {
        if (cal == null) {
            return null;
        }
        Calendar c = (Calendar) cal.clone();
        c.set(Calendar.DAY_OF_YEAR, cal.get(Calendar.DAY_OF_YEAR) + n);
        return c;
    }

    /**
     * 下N秒
     *
     * @param cal
     * @param n
     * @return
     */
    public static Calendar afterSecond(Calendar cal, int n) {
        if (cal == null) {
            return null;
        }
        Calendar c = (Calendar) cal.clone();
        c.set(Calendar.SECOND, cal.get(Calendar.SECOND) + n);
        return c;
    }

    /**
     * 下N分钟或者N分钟以前
     *
     * @param cal
     * @param minute
     * @return
     */
    public static Calendar afterMinute(Calendar cal, int minute) {
        if (cal == null) {
            return null;
        }
        Calendar c = (Calendar) cal.clone();

        c.set(Calendar.MINUTE, cal.get(Calendar.MINUTE) + minute);

        return c;


    }

    /**
     * 下N小时或者前N小时
     *
     * @param cal
     * @param hour
     * @return
     */
    public static Calendar afterHours(Calendar cal, int hour) {

        if (cal == null) {
            return null;
        }
        Calendar c = (Calendar) cal.clone();

        c.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY) + hour);

        return c;

    }

    /**
     * 后n月
     *
     * @param cal
     * @param n
     * @return Calendar
     */
    public static Calendar afterMonths(Calendar cal, int n) {
        if (cal == null) {
            return null;
        }
        Calendar c = (Calendar) cal.clone();
        c.set(Calendar.MONTH, cal.get(Calendar.MONTH) + n);
        return c;
    }


    /**
     * 后n年
     *
     * @param cal
     * @param n
     * @return Calendar
     */
    public static Calendar afterYesrs(Calendar cal, int n) {
        if (cal == null) {
            return null;
        }
        Calendar c = (Calendar) cal.clone();
        c.set(Calendar.YEAR, cal.get(Calendar.YEAR) + n);
        return c;
    }

    /**
     * 使用预设Format格式化Date成字符串
     *
     * @return String
     */
    public static String format(Date date) {
        return date == null ? "" : format(date, getDatePattern());
    }

    /**
     * 使用参数Format格式化Date成字符串
     *
     * @return String
     */
    public static String format(Date date, String pattern) {
        return date == null ? "" : new SimpleDateFormat(pattern).format(date);
    }

    public static String format(long time, String pattern) {
        return 0 == time ? "" : new SimpleDateFormat(pattern).format(new Date(
                time));
    }

    /**
     * 试用参数Format格式化Calendar成字符串
     *
     * @param cal
     * @param pattern
     * @return String
     */
    public static String format(Calendar cal, String pattern) {
        return cal == null ? "" : new SimpleDateFormat(pattern).format(cal
                .getTime());
    }

    /**
     * 使用预设格式将字符串转为Date
     *
     * @return Date
     */
    public static Date parse(String strDate) throws ParseException {
        return StringUtils.isBlank(strDate) ? null : parse(strDate,
                getDatePattern());
    }

    /**
     * 使用参数Format将字符串转为Date
     *
     * @return Date
     */
    public static Date parse(String strDate, String pattern)
            throws ParseException {
        return StringUtils.isBlank(strDate) ? null : new SimpleDateFormat(
                pattern).parse(strDate);
    }

    /**
     * 在日期上增加数个整月
     *
     * @return Date
     */
    public static Date addMonth(Date date, int n) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, n);
        return cal.getTime();
    }

    /**
     * get String value(MM/dd/yyyy) of time
     *
     * @param d
     * @return String
     */
    public static String dateToString(Date d) {
        if (d == null) {
            return null;
        }
        SimpleDateFormat lenientDateFormat = new SimpleDateFormat(
                defaultDatePattern);
        return lenientDateFormat.format(d);
    }


    /**
     * Date 转 String yyyyMMdd 转换自定义日期格式 默认为：yyyy-MM-dd HH:mm:ss
     *
     * @param strDate 字符串日期
     * @param pattern 转换格式 (可传 null 或 "")
     * @return 转换后结果（String）
     * @author eswyao@126.com
     */
    public String dateToString(String strDate, String pattern) {

        try {
            Date fdate = parse(strDate, "yyyyMMdd");
            if ("".equals(pattern) || null == pattern) {
                pattern = FullDatePattern;
            }

            if (null != strDate || "".equals(strDate)) {
                SimpleDateFormat sformat = new SimpleDateFormat(pattern);
                String date = sformat.format(fdate);
                return date;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return "";
    }

    /**
     * 获得该月第一天
     *
     * @param year
     * @param month
     * @return
     */
    public static String getFirstDayOfMonth(int year, int month) {
        Calendar cal = Calendar.getInstance();

        if (year > 0) {
            cal.set(Calendar.YEAR, year);
        }
        if (month > 0) {
            cal.set(Calendar.MONTH, month - 1);
        }
        // 获取某月最小天数
        int firstDay = cal.getActualMinimum(Calendar.DAY_OF_MONTH);
        // 设置日历中月份的最小天数
        cal.set(Calendar.DAY_OF_MONTH, firstDay);
        // 格式化日期
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        String firstDayOfMonth = sdf.format(cal.getTime());
        return firstDayOfMonth;
    }


    /**
     * 获得该月最后一天
     *
     * @param year
     * @param month
     * @return
     */
    public static String getLastDayOfMonth(int year, int month) {
        Calendar cal = Calendar.getInstance();
        if (year > 0) {
            cal.set(Calendar.YEAR, year);
        }
        if (month > 0) {
            cal.set(Calendar.MONTH, month - 1);
        }
        // 获取某月最大天数
        int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        // 设置日历中月份的最大天数
        cal.set(Calendar.DAY_OF_MONTH, lastDay);
        // 格式化日期
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd 23:59:59");
        String lastDayOfMonth = sdf.format(cal.getTime());
        return lastDayOfMonth;
    }

    /**
     * 获取昨天
     *
     * @return
     */
    public static String getYesterday(Date date) {
        Calendar ca = Calendar.getInstance();
        ca.setTime(date); // 设置时间为当前时间
        ca.add(Calendar.DATE, -1);// 日期减1
        Date resultDate = ca.getTime(); // 结果
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(resultDate);
    }

    /**
     * 获取上一周的开始日期和结束日期
     *
     * @param data
     * @return
     */
    public static Map<String, String> getLastWeek(Date data) {

        // 取上周一 00:00:00
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(data);
        int n1 = -1;// n为推迟的周数，-1上周，0本周，1下周，2下下周，依次类推
        cal1.add(Calendar.DATE, n1 * 7);
        cal1.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        cal1.set(Calendar.HOUR_OF_DAY, 0);
        cal1.set(Calendar.MINUTE, 0);
        cal1.set(Calendar.SECOND, 0);

        // 取本周一00:00:00
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(data);
        int n2 = 0;// n为推迟的周数，-1上周，0本周，1下周，2下下周，依次类推
        cal2.add(Calendar.DATE, n2 * 7);
        cal2.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        cal2.set(Calendar.HOUR_OF_DAY, 0);
        cal2.set(Calendar.MINUTE, 0);
        cal2.set(Calendar.SECOND, 0);
        SimpleDateFormat timeFormate = new SimpleDateFormat("yyyy-MM-dd");

        Map<String, String> map = new HashMap<String, String>();

        map.put("monday", timeFormate.format(cal1.getTime()));
        map.put("sunday", getYesterday(cal2.getTime()));

        return map;
    }

    /**
     * 获取上月
     *
     * @param date
     * @return
     */
    public static String getLastMonth(Date date) {
        // 取上周一 00:00:00
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date);
        int n1 = -1;// n为推迟的周数，-1上周，0本周，1下周，2下下周，依次类推
        cal1.add(Calendar.MONTH, n1);

        SimpleDateFormat timeFormate = new SimpleDateFormat("MM");

        return timeFormate.format(cal1.getTime());
    }

    /**
     * get seven days before
     *
     * @return
     */
    public static String getSevenDaysBefore() {

        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(new Date());
        cal1.add(Calendar.DATE, -3);

        SimpleDateFormat timeFormate = new SimpleDateFormat("yyyy-MM-dd");

        return timeFormate.format(cal1.getTime()) + " 00:00:00";
    }

    /**
     * 秒换算为时分秒
     *
     * @param time
     * @return
     */
    public static String getConvert(int time) {
        try {

            if (time < 60) {
                return time + "秒";
            } else if ((time / 60) < 60) {
                return time / 60 + "分" + time % 60 + "秒";
            } else {
                return time / 60 / 60 + "小时" + (time % 3600) / 60 + "分" + time % 60 % 60 + "秒";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {

        System.out.println(format(new Date(), "YYYY-MM-dd"));

    }

    /**
     * 获取n天后零时零点毫秒数
     *
     * @param day 第几天
     * @return
     */
    public static long timeInMillis(int day) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, day);
        // 改成这样就好了
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    /**
     * 获取n天后零时零点毫秒与当前时间秒差
     *
     * @param day 第几天
     * @return
     */
    public static long millis(int day) {
        return (timeInMillis(day) - System.currentTimeMillis());
    }

}

