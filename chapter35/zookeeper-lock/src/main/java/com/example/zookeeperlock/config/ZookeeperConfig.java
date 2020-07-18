package com.example.zookeeperlock.config;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <p>curator 配置</p>
 * Created by hanqf on 2020/7/18 15:32.
 */


@Configuration
public class ZookeeperConfig {

    /**
     * 获取 CuratorFramework
     * 使用 curator 操作 zookeeper
     * @return
     */
    @Bean
    public CuratorFramework curatorFramework(ZookeeperProperties zookeeperProperties) {
        //配置zookeeper连接的重试策略
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(zookeeperProperties.getBaseSleepTimeMs(), zookeeperProperties.getMaxRetries());

        //构建 CuratorFramework
        CuratorFramework curatorFramework =
                CuratorFrameworkFactory.builder()
                        .connectString(zookeeperProperties.getConnectString())
                        .sessionTimeoutMs(zookeeperProperties.getSessionTimeoutMs())
                        .connectionTimeoutMs(zookeeperProperties.getConnectionTimeoutMs())
                        .retryPolicy(retryPolicy)
                        .build();
        //连接 zookeeper
        curatorFramework.start();
        return curatorFramework;
    }

}