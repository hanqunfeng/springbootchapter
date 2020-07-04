package com.example.runner;

import com.example.job.DemoJob;
import com.example.utils.JobUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * <p></p>
 * Created by hanqf on 2020/7/4 15:50.
 */

@Component
public class DemoRunner implements CommandLineRunner {

    @Autowired
    private JobUtils jobUtils;

    @Override
    public void run(String... args) throws Exception {
        Map<String,Object> map = new HashMap<>();
        map.put("jobName","demo");
        map.put("jobGroup","demo-group");
        jobUtils.createJobByCron("demo","demo-group","0/5 * * * * ?",DemoJob.class,map,true);
        Thread.sleep(10000);
        jobUtils.removeJob("demo","demo-group");

        Thread.sleep(5000);
        jobUtils.removeJob("demo","demo-group");

        //不执行shutdown，后台进程会一直运行
        jobUtils.shutdown();
    }
}
