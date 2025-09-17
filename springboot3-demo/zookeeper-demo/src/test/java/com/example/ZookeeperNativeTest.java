package com.example;


import org.apache.zookeeper.*;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.server.auth.DigestAuthenticationProvider;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.apache.zookeeper.ZooDefs.Perms.ALL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * ZookeeperNative 测试
 * Created by hanqf on 2025/9/17 14:32.
 */

public class ZookeeperNativeTest {

    private static final String CONNECT_STRING = "127.0.0.1:2181"; // zookeeper地址，多个地址用逗号分隔
    private static final int SESSION_TIMEOUT = 5000; // 会话超时时间
    private static ZooKeeper zk;
    private static CountDownLatch connectedSignal = new CountDownLatch(1);

    private static final String USER = "testuser"; // 用户名
    private static final String PASS = "123456";   // 密码

    @BeforeAll
    static void setup() throws IOException, InterruptedException {
        zk = new ZooKeeper(CONNECT_STRING, SESSION_TIMEOUT, event -> {
            if (event.getState() == Watcher.Event.KeeperState.SyncConnected) {
                connectedSignal.countDown();
                System.out.println("连接建立");
            }
        });
        System.out.println("等待连接建立...");
        connectedSignal.await();
    }

    @AfterAll
    static void tearDown() throws InterruptedException {
        if (zk != null) {
            zk.close();
            System.out.println("连接关闭");
        }
    }

    @Test
    public void testCreateAndGetData() throws Exception {
        String path = "/test-native";
        // 创建节点
        zk.create(path, "hello".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        System.out.println("节点已创建");
        // 读取数据
        byte[] data = zk.getData(path, false, null);
        System.out.println("数据：" + new String(data));
        assertEquals("hello", new String(data));
        // 删除节点
        zk.delete(path, -1);
        System.out.println("节点已删除");
    }

    @Test
    public void testWatcher() throws Exception {
        String path = "/test-watcher";
        zk.create(path, "init".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);

        CountDownLatch latch = new CountDownLatch(1);
        // 创建一个watcher
        zk.getData(path, event -> {
            if (event.getType() == Watcher.Event.EventType.NodeDataChanged) {
                latch.countDown();
                System.out.println("数据已改变");
            }
        }, null);

        zk.setData(path, "new-data".getBytes(), -1);
        latch.await(); // 等待watch触发

        zk.delete(path, -1);
    }

    @Test
    public void testACLWithDigest() throws Exception {
        String path = "/acl-native";

        // 创建 Digest ACL
        List<ACL> acls = new ArrayList<>();
        Id digestId = new Id("digest", DigestAuthenticationProvider.generateDigest(USER + ":" + PASS));
        acls.add(new ACL(ALL, digestId));

        // 创建节点并设置ACL
        zk.create(path, "secure-data".getBytes(), acls, CreateMode.PERSISTENT);
        System.out.println("节点已创建");

        // 添加认证信息
        zk.addAuthInfo("digest", (USER + ":" + PASS).getBytes());
        System.out.println("已添加认证信息");
        // 正确认证下读取数据
        byte[] data = zk.getData(path, false, null);
        System.out.println("数据：" + new String(data));
        assertEquals("secure-data", new String(data));

        // 使用没有认证的 ZooKeeper 客户端尝试读取
        ZooKeeper noAuthZk = new ZooKeeper(CONNECT_STRING, SESSION_TIMEOUT, null);
        System.out.println("使用没有认证的 ZooKeeper 尝试读取数据...");
        assertThrows(KeeperException.NoAuthException.class, () -> noAuthZk.getData(path, false, null));
        System.out.println("没有认证的 ZooKeeper 读取失败，已关闭");
        noAuthZk.close();

        zk.delete(path, -1);
    }

    @Test
    public void testPersistentWatch() throws Exception {
        String WATCH_PATH = "/watch_test";
        // 确保节点存在
        if (zk.exists(WATCH_PATH, false) == null) {
            try {
                zk.create(WATCH_PATH, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            } catch (KeeperException.NodeExistsException ignore) {
                // 可能被其他客户端并发创建，忽略
            }
        }

        // 添加永久监听器, `ZooKeeper 3.6+`，以前的版本不支持
        zk.addWatch(WATCH_PATH, event -> {
            System.out.println("持久化监听触发：类型 = " + event.getType() + ", 路径 = " + event.getPath());
        }, AddWatchMode.PERSISTENT);

        // 模拟数据变化
        zk.setData(WATCH_PATH, "data1".getBytes(), -1);
        Thread.sleep(1000);
        zk.setData(WATCH_PATH, "data2".getBytes(), -1);
        Thread.sleep(2000);
    }
}
