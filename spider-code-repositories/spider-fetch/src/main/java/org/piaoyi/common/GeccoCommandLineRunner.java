package org.piaoyi.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * <h1>启动</h1>
 * Created by hanqf on 2022/7/6 15:49.
 */
@Slf4j
@Component("GeccoCommandLineRunner")
public class GeccoCommandLineRunner implements CommandLineRunner {

    @Value("${spider.start}")
    public String spider_start;

    @Autowired
    private ApplicationContext applicationContext;


    private static Map<String, String> beanMap = new HashMap<>();

    static {
        beanMap.put("github", "GithubSearchResultRunner");
        beanMap.put("gitee", "GiteeSearchResultRunner");
    }

    @Override
    public void run(String... args) throws Exception {
        if (spider_start != null) {
            String beanName = beanMap.get(spider_start);
            if (beanName != null) {
                exec(beanName);
            }else{
                System.out.println("！！！--spider.start 参数错误：取值范围sina，xzw，horo，astro，msn，alman！！！");
            }
        }else {
            System.out.println("！！！请指定抓取网站参数：--spider.start 取值范围sina，xzw，horo，astro，msn，alman！！！");
        }

    }


    /**
     * <p>获取BaseRunner实现类的对象，并执行start方法</p>
     *
     * @param beanName
     * @author hanqf
     * 2020/4/12 13:53
     */
    private void exec(String beanName) {
        log.info("run " + beanName);
        if (!"".equals(beanName)) {
            Object object = applicationContext.getBean(beanName);
            if (object != null && object instanceof BaseDynamicRunner) {
                BaseDynamicRunner baseDynamicRunner = ((BaseDynamicRunner) object);
                baseDynamicRunner.setInterval(2000);
                baseDynamicRunner.setThread(2);
                baseDynamicRunner.start();
            } else if (object != null && object instanceof BaseBeanRunner) {
                BaseBeanRunner baseBeanRunner = ((BaseBeanRunner) object);
                baseBeanRunner.setInterval(100);
                baseBeanRunner.setThread(1);
                baseBeanRunner.start();
            } else if (object != null && object instanceof BaseRunner) {
                ((BaseRunner) object).start();
            }
        } else {
            log.info("args error!");
        }
    }
}
