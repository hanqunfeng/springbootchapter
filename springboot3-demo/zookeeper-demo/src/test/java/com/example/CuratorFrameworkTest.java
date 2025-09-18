package com.example;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.ACLProvider;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.AddWatchMode;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.server.auth.DigestAuthenticationProvider;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Curator Framework 测试
 * Created by hanqf on 2025/9/17 14:32.
 */


public class CuratorFrameworkTest {

    // Zookeeper连接字符串，多个服务器用逗号分隔
    private static final String CONNECT_STRING = "127.0.0.1:2181";
    private static CuratorFramework client;

    private static final String USER = "testuser"; // 用户名
    private static final String PASS = "123456";   // 密码

    @BeforeAll
    static void setup() {
        client = CuratorFrameworkFactory.builder()
                .connectString(CONNECT_STRING) // 连接字符串
                .sessionTimeoutMs(10000)  // 会话超时
                .connectionTimeoutMs(5000) // 连接超时
                .retryPolicy(new ExponentialBackoffRetry(1000, 3)) // 重试策略
                .build();
        client.start(); // 启动客户端
        System.out.println("Zookeeper连接成功！");
    }

    @AfterAll
    static void tearDown() {
        if (client != null) {
            client.close(); // 关闭客户端
            System.out.println("Zookeeper连接已关闭！");
        }
    }

    private ACLProvider getACLProvider() {
        // 创建ACL Provider
        ACLProvider aclProvider = new ACLProvider() {
            private final List<ACL> acls;

            {
                try {
                    Id digestId = new Id("digest", DigestAuthenticationProvider.generateDigest(USER + ":" + PASS));
                    acls = new ArrayList<>();
                    acls.add(new ACL(ZooDefs.Perms.ALL, digestId));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public List<ACL> getDefaultAcl() {
                return acls;
            }

            @Override
            public List<ACL> getAclForPath(String path) {
                return acls;
            }
        };

        return aclProvider;
    }

    /**
     * 创建一个具有ACL的客户端
     *
     * @return
     */
    private CuratorFramework getAclClient() {


        CuratorFramework aclClient = CuratorFrameworkFactory.builder()
                .connectString(CONNECT_STRING) // 连接字符串
                .sessionTimeoutMs(10000)  // 会话超时
                .connectionTimeoutMs(5000) // 连接超时
                .retryPolicy(new ExponentialBackoffRetry(1000, 3)) // 重试策略
                .authorization("digest", (USER + ":" + PASS).getBytes())
                .aclProvider(getACLProvider()) // 设置当前 client 的默认ACL
                .build();
        aclClient.start();
        return aclClient;
    }

    @Test
    public void testCreateAndGetData() throws Exception {
        String path = "/test-curator";
        client.create().forPath(path, "hello".getBytes());
        System.out.println("创建节点成功，节点路径：" + path);
        byte[] data = client.getData().forPath(path);
        System.out.println("获取节点数据成功，节点数据：" + new String(data));
        assertEquals("hello", new String(data));
        System.out.println("删除节点成功！");
        client.delete().forPath(path);
    }

    @Test
    public void testCreateAndGetDataWithStat() throws Exception {
        String path = "/test-curator";
        Stat stat = new Stat();
        client.create().forPath(path, "hello".getBytes());
        System.out.println("创建节点成功，节点路径：" + path);
        byte[] data = client.getData().storingStatIn(stat).forPath(path);
        System.out.println("获取节点数据成功，节点数据：" + new String(data) + "|| stat version:" + stat.getVersion() );
        assertEquals(0, stat.getVersion());
        assertEquals("hello", new String(data));
        // 修改节点
        client.setData().withVersion(stat.getVersion()).forPath(path, "new-data".getBytes());
        System.out.println("修改节点数据成功！");
        data = client.getData().storingStatIn(stat).forPath(path);
        System.out.println("获取节点数据成功，节点数据：" + new String(data) + "|| stat version:" + stat.getVersion());
        assertEquals(1, stat.getVersion());

        stat = client.checkExists().forPath(path);
        System.out.println("删除节点成功！");
        client.delete().withVersion(stat.getVersion()).forPath(path);
    }

    @Test
    public void testWatcher() throws Exception {
        String path = "/test-curator-watcher";
        client.create().forPath(path, "init".getBytes());
        System.out.println("创建节点成功，节点路径：" + path);

        client.getData().usingWatcher((Watcher) event -> {
            System.out.println("Watcher event: " + event);
        }).forPath(path);

        client.setData().forPath(path, "new-data".getBytes());
        System.out.println("设置节点数据成功！");
        Thread.sleep(500); // 等待watcher触发
        client.delete().forPath(path);
        System.out.println("删除节点成功！");
    }

    @Test
    public void testACLWithDigest() throws Exception {
        String path = "/acl-curator";

        final CuratorFramework aclClient = getAclClient();
        // 创建节点时自动带 ACL，因为 aclClient 初始化时已设置了默认的 ACL
//        aclClient.create().creatingParentsIfNeeded().forPath(path, "secure-data".getBytes());

        // 也可以在创建节点是指定  ACL
        // 为当前节点自定义 ACL
        List<ACL> acls = new ArrayList<>();
        Id digestId = new Id("digest", DigestAuthenticationProvider.generateDigest(USER + ":" + PASS));
        // 只给 READ + DELETE + WRITE 权限
        int perms = ZooDefs.Perms.READ | ZooDefs.Perms.DELETE | ZooDefs.Perms.WRITE;
        acls.add(new ACL(perms, digestId));

//        final List<ACL> defaultAcl = getACLProvider().getDefaultAcl();

        // 创建节点时临时指定 ACL
        aclClient.create()
                .withACL(acls)
                .forPath(path, "secure-data".getBytes());

        // 正确认证下读取数据
        byte[] data = aclClient.getData().forPath(path);
        assertEquals("secure-data", new String(data));

        // 未认证客户端尝试访问
        assertThrows(Exception.class, () -> client.getData().forPath(path));

        // 为客户端添加认证
        client.getZookeeperClient().getZooKeeper().addAuthInfo("digest", (USER + ":" + PASS).getBytes());
        final byte[] bytes = client.getData().forPath(path);
        System.out.println("获取节点数据成功，节点数据：" + new String(bytes));

        aclClient.delete().forPath(path);
        aclClient.close();
    }


    @Test
    public void testDistributedLock() throws Exception {
        String lockPath = "/lock";
        // 可重入互斥锁
        InterProcessMutex lock = new InterProcessMutex(client, lockPath);

        // 尝试获取锁, 最多等待3秒，如果获取锁成功，则执行业务逻辑，并释放锁，否则放弃，如果要一直等待，则使用 lock.acquire()
        if (lock.acquire(3, TimeUnit.SECONDS)) {
            try {
                System.out.println("获得分布式锁，执行业务逻辑");
            } finally {
                // 释放锁
                lock.release();
                System.out.println("释放分布式锁");
            }
        }
    }

    @Test
    public void testPermanentWatcher() throws Exception {

        String WATCH_PATH = "/watch_test";
        // 确保节点存在
        if (client.checkExists().forPath(WATCH_PATH) == null) {
            client.create().creatingParentsIfNeeded().forPath(WATCH_PATH, "init".getBytes());
        }
        // 添加永久监听器, `ZooKeeper 3.6+` + `Curator 5.x` 的创建方法
        client.watchers()
                .add()
                .withMode(AddWatchMode.PERSISTENT)
                .usingWatcher((CuratorWatcher) event -> {
                    System.out.println("Persistent Watcher event: " + event);
                })
                .forPath(WATCH_PATH);

        // 模拟数据变化
        client.setData().forPath(WATCH_PATH, "data1".getBytes());
        Thread.sleep(1000);
        client.setData().forPath(WATCH_PATH, "data2".getBytes());
        Thread.sleep(2000);
    }
}

