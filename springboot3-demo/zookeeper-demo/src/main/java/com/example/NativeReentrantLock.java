package com.example;

/**
 * zookeeper原生客户端实现可充入互斥锁
 * Created by hanqf on 2025/9/17 16:36.
 */


import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 原生 ZooKeeper 实现的可重入分布式互斥锁（简化版）。
 * <p>
 * 说明：
 * - 每次第一次获取锁时，client 在 lockBasePath 下创建一个 ephemeral-sequential 节点，
 * 节点名形如: lockBasePath/lock-0000000003
 * - 节点数据包含 ownerId (uuid) 仅用于诊断/调试（非必须）。
 * - 本地维护重入计数（reentrantCount）。如果同一个客户端再次调用 acquire，
 * 直接增加计数并立即返回（不重复创建节点）。
 * - 释放时计数归零并删除自身创建的节点。
 */
public class NativeReentrantLock {
    private final ZooKeeper zk;
    private final String lockBasePath; // e.g. /locks/mylock
    private final String ownerId = UUID.randomUUID().toString(); // 标识当前 client 实例
    private final Object mutex = new Object();

    // 本地状态
    private String ourLockPath = null;      // /locks/mylock/lock-0000000003
    private int reentrantCount = 0;

    public NativeReentrantLock(ZooKeeper zk, String lockBasePath) throws KeeperException, InterruptedException {
        this.zk = zk;
        if (!lockBasePath.startsWith("/")) throw new IllegalArgumentException("path must start with /");
        this.lockBasePath = lockBasePath;
        ensurePathExists(lockBasePath);
    }

    private void ensurePathExists(String path) throws KeeperException, InterruptedException {
        String[] parts = path.split("/");
        StringBuilder current = new StringBuilder();
        for (String part : parts) {
            if (part.isEmpty()) continue; // 跳过开头的空字符串
            current.append("/").append(part);
            String p = current.toString();
            if (zk.exists(p, false) == null) {
                try {
                    zk.create(p, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                } catch (KeeperException.NodeExistsException ignore) {
                    // 可能被其他客户端并发创建，忽略
                }
            }
        }
    }


    /**
     * 获取锁（可重入）。无超时时间，直到获得为止。
     */
    public void acquire() throws KeeperException, InterruptedException {
        acquire(0, null);
    }

    /**
     * 获取锁（带超时）。如果 timeout==0 且 unit==null 则表示无限等待。
     * 返回 true 表示获取到锁，false 表示超时未获取到。
     */
    public boolean acquire(long timeout, TimeUnit unit) throws KeeperException, InterruptedException {
        long deadline = (unit == null || timeout <= 0) ? 0L : (System.nanoTime() + unit.toNanos(timeout));

        synchronized (mutex) {
            // 如果当前客户端已持有锁，支持可重入：直接计数+1 返回
            if (ourLockPath != null) {
                reentrantCount++;
                return true;
            }

            // 1) create ephemeral-sequential node
            String sequentialNode = zk.create(lockBasePath + "/lock-", ownerId.getBytes(StandardCharsets.UTF_8),
                    ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);

            ourLockPath = sequentialNode;

            try {
                // 2) 尝试获取锁：检查自己是否为最小序号节点
                while (true) {
                    List<String> children = zk.getChildren(lockBasePath, false);
                    if (children == null || children.isEmpty()) {
                        // unlikely
                        break;
                    }

                    // sort children by sequence number extracted from name
                    children.sort(Comparator.comparingInt(NativeReentrantLock::sequenceFromName));

                    String smallest = children.get(0);
                    String ourNodeName = seqNodeName(ourLockPath);

                    if (smallest.equals(ourNodeName)) {
                        // 获得锁
                        reentrantCount = 1;
                        return true;
                    } else {
                        // 找到比我们小的那个节点中的前驱节点（即在排序中位于我们之前的最后一个），在其上注册删除watch
                        String predecessor = null;
                        for (int i = 0; i < children.size(); i++) {
                            if (children.get(i).equals(ourNodeName)) {
                                predecessor = children.get(i - 1);
                                break;
                            }
                        }

                        if (predecessor == null) {
                            // 说明我们的节点不在孩子列表里（可能会话丢失），抛异常
                            throw new KeeperException.NoNodeException("Our node disappeared: " + ourLockPath);
                        }

                        String predecessorFullPath = lockBasePath + "/" + predecessor;
                        CountDownLatch latch = new CountDownLatch(1);

                        // 注册一次性 watcher：等待 predecessor 被删除
                        Watcher watcher = event -> {
                            if (event.getType() == Watcher.Event.EventType.NodeDeleted) {
                                latch.countDown();
                            }
                        };

                        Stat stat = zk.exists(predecessorFullPath, watcher);
                        if (stat == null) {
                            // 前驱已不存在，继续循环判断
                            continue;
                        }

                        // 等待前驱删除或者超时到达
                        if (deadline == 0L) {
                            latch.await();
                        } else {
                            long waitNanos = deadline - System.nanoTime();
                            if (waitNanos <= 0) {
                                // 超时：清理自己的节点并返回 false
                                try {
                                    zk.delete(ourLockPath, -1);
                                } catch (Exception ignore) {
                                }
                                ourLockPath = null;
                                return false;
                            }
                            long waitMillis = TimeUnit.NANOSECONDS.toMillis(waitNanos);
                            boolean countedDown = latch.await(waitMillis, TimeUnit.MILLISECONDS);
                            if (!countedDown) {
                                // 超时：清理自己的节点并返回 false
                                try {
                                    zk.delete(ourLockPath, -1);
                                } catch (Exception ignore) {
                                }
                                ourLockPath = null;
                                return false;
                            }
                        }
                        // 前驱删除后，循环重新判断是否成为最小
                    }
                }
            } catch (KeeperException | InterruptedException e) {
                // 出错清理
                try {
                    if (ourLockPath != null) {
                        zk.delete(ourLockPath, -1);
                    }
                } catch (Exception ignore) {
                }
                ourLockPath = null;
                throw e;
            }
            return false;
        }
    }

    /**
     * 释放锁。对重入计数进行递减。只有计数为0时才删除节点并真正释放锁。
     */
    public void release() throws KeeperException, InterruptedException {
        synchronized (mutex) {
            if (ourLockPath == null) {
                throw new IllegalStateException("Lock not held by this client");
            }
            reentrantCount--;
            if (reentrantCount > 0) {
                return;
            }

            // 删除我们的 lock 节点
            try {
                zk.delete(ourLockPath, -1);
            } catch (KeeperException.NoNodeException ignore) {
                // 节点不存在可能是会话失效导致，忽略
            }
            ourLockPath = null;
            reentrantCount = 0;
        }
    }

    public boolean isHeldByCurrentClient() {
        synchronized (mutex) {
            return ourLockPath != null && reentrantCount > 0;
        }
    }

    // helper: extract node name from full path
    private static String seqNodeName(String fullPath) {
        int idx = fullPath.lastIndexOf('/');
        return fullPath.substring(idx + 1);
    }

    // helper: extract sequence suffix numeric part for sorting
    private static int sequenceFromName(String name) {
        // name like lock-0000000003
        int dash = name.lastIndexOf('-');
        if (dash < 0) return 0;
        String seq = name.substring(dash + 1);
        try {
            return Integer.parseInt(seq);
        } catch (NumberFormatException e) {
            // fallback lexicographic
            return name.hashCode();
        }
    }
}

