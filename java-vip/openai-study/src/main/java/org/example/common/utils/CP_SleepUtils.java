package org.example.common.utils;

import java.util.concurrent.TimeUnit;

/**
 * <h1>线程休眠工具类</h1>
 * Created by hanqf on 2023/4/27 10:05.
 */


public class CP_SleepUtils {
    /**
     * 休眠指定的时间
     * // 休眠 6 秒钟
     * CP_SleepUtils.sleep(6, TimeUnit.SECONDS);
     *
     * // 休眠 5 分钟
     * CP_SleepUtils.sleep(5, TimeUnit.MINUTES);
     *
     * // 休眠 2 小时
     * CP_SleepUtils.sleep(2, TimeUnit.HOURS);
     *
     * @param duration 休眠的时长
     * @param unit     休眠的时间单位
     */
    public static void sleep(long duration, TimeUnit unit) {
        try {
            Thread.sleep(unit.toMillis(duration));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
