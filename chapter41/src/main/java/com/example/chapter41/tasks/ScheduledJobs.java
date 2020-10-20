package com.example.chapter41.tasks;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

/**
 * <h1>定时任务</h1>
 * Created by hanqf on 2020/10/20 10:52.
 */

@Component
@Slf4j
public class ScheduledJobs {

    //格式化
    private static final DateTimeFormatter fmTime = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * <h2>fixedDelay:任务执行完成后间隔多少时间执行下一次任务</h2>
     * Created by hanqf on 2020/10/20 11:00. <br>
     * initialDelay = 2000 : 表示第一次执行前延迟2秒，默认不延迟，服务器启动直接运行
     * @author hanqf
     */
    @Scheduled(fixedDelay = 5000, initialDelay = 2000)
    public void fixedDelayJob() throws InterruptedException {
        log.info("fixedDelayJob 开始：" + LocalDateTime.now().format(fmTime));
        TimeUnit.SECONDS.sleep(10);
        log.info("fixedDelayJob 结束：" + LocalDateTime.now().format(fmTime));
    }

    /**
     * <h2>fixedRate:任务执行开始后间隔多少时间执行下一次任务，如果间隔时间小于任务执行时间，则等待任务执行完成后立即执行下一次任务</h2>
     * Created by hanqf on 2020/10/20 11:01. <br>
     *
     * @author hanqf
     */
    @Scheduled(fixedRate = 3000)
    public void fixedRateJob() throws InterruptedException {
        log.info("fixedRateJob 开始：" + LocalDateTime.now().format(fmTime));
        TimeUnit.SECONDS.sleep(5);
        log.info("fixedRateJob 结束：" + LocalDateTime.now().format(fmTime));
    }


    @Scheduled(cron = "0/10 * * * * ?")
    public void cronJob() {
        log.info("cronJob 执行：" + LocalDateTime.now().format(fmTime));
    }
}
