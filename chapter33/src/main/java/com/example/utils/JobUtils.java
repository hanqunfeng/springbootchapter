package com.example.utils;

import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

/**
 * <p></p>
 * Created by hanqf on 2020/7/4 15:37.
 */

@Component
@Slf4j
public class JobUtils {

    @Autowired
    Scheduler scheduler;


    public void createJobByStartAt(long startAtTime, String name, String group, Class<? extends Job> jobBean) {
        createJobByStartAt(startAtTime, name, group, jobBean, null);

    }

    public void createJobByStartAt(long startAtTime, String name, String group, Class<? extends Job> jobBean, Map<String, Object> dataMap) {
        //创建任务触发器
        Trigger trigger = TriggerBuilder.newTrigger().withIdentity(name, group).startAt(new Date(startAtTime)).build();
        createJob(name, group, trigger, jobBean, dataMap, false);

    }

    public void createJobByCron(JobKey jobKey, String cron, Class<? extends Job> jobBean, Map<String, Object> dataMap, boolean isNow) {
        createJobByCron(jobKey.getName(), jobKey.getGroup(), cron, jobBean, dataMap, isNow);
    }

    public void createJobByCron(JobKey jobKey, String cron, Class<? extends Job> jobBean, Map<String, Object> dataMap) {
        createJobByCron(jobKey.getName(), jobKey.getGroup(), cron, jobBean, dataMap, false);
    }

    public void createJobByCron(JobKey jobKey, String cron, Class jobBean) {
        createJobByCron(jobKey, cron, jobBean, null);
    }

    public void createJobByCron(String name, String group, String cron, Class<? extends Job> jobBean) {
        createJobByCron(name, group, cron, jobBean, null);
    }

    public void createJobByCron(String name, String group, String cron, Class<? extends Job> jobBean, Map<String, Object> dataMap) {
        createJobByCron(name, group, cron, jobBean, dataMap, false);
    }

    public void createJobByCron(String name, String group, String cron, Class<? extends Job> jobBean, Map<String, Object> dataMap, boolean isNow) {
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cron);
        //创建任务触发器
        Trigger trigger = TriggerBuilder.newTrigger().withIdentity(name, group).withSchedule(scheduleBuilder).build();
        createJob(name, group, trigger, jobBean, dataMap, isNow);
    }


    /**
     * <p>创建定时任务</p>
     *
     * @param name    任务名称
     * @param group   任务分组
     * @param trigger 触发器
     * @param jobBean 任务类
     * @param dataMap 传递数据
     * @param isNow   是否立刻执行
     * @author hanqf
     * 2020/7/4 16:51
     */
    private void createJob(String name, String group, Trigger trigger, Class<? extends Job> jobBean, Map<String, Object> dataMap, boolean isNow) {

        try {
            if (!scheduler.checkExists(JobKey.jobKey(name, group))) {
                //创建任务
                JobDetail jobDetail = JobBuilder.newJob(jobBean).withIdentity(name, group).build();
                if (dataMap != null) {
                    for (String key : dataMap.keySet()) {
                        jobDetail.getJobDataMap().put(key, dataMap.get(key));
                    }
                }

                //将触发器与任务绑定到调度器内
                scheduler.scheduleJob(jobDetail, trigger);
                if (isNow) {
                    scheduler.triggerJob(jobDetail.getKey(), jobDetail.getJobDataMap());
                }

                log.info("任务创建成功: " + jobDetail);
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    /**
     * <p>删除定时任务</p>
     *
     * @param name  任务名称
     * @param group 任务分组
     * @author hanqf
     * 2020/7/4 17:05
     */
    public void removeJob(String name, String group) throws SchedulerException {
        TriggerKey triggerKey = TriggerKey.triggerKey(name, group);
        if (scheduler.checkExists(triggerKey)) {
            // 停止触发器
            scheduler.pauseTrigger(triggerKey);
            // 移除触发器
            scheduler.unscheduleJob(triggerKey);
            // 删除任务
            scheduler.deleteJob(JobKey.jobKey(name, group));
            log.info("任务删除成功: " + triggerKey);
        } else {
            log.info("要删除的任务不存在！" + triggerKey);
        }
    }

    public void shutdown() throws SchedulerException {
        scheduler.shutdown();
    }

}
