package com.example.job;

import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * <p></p>
 * Created by hanqf on 2020/7/4 15:47.
 */
@Slf4j
public class DemoJob implements Job {

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.info("任务开始。。。。。。。" + jobExecutionContext.toString());
        JobDataMap jobDataMap = jobExecutionContext.getJobDetail().getJobDataMap();
        for(String key:jobDataMap.getKeys()){
            log.info("key="+key+",value="+jobDataMap.get(key));
        }
    }
}
