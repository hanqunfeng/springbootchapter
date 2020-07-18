package com.example.zookeeperlock;

import com.example.zookeeperlock.common.BaseLockHandler;
import com.example.zookeeperlock.common.ShardReentrantLockComponent;
import org.apache.curator.test.TestingServer;
import org.apache.curator.utils.CloseableUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest
class ZookeeperLockApplicationTests {

    /**
     * 表示开启多个线程并行执行
     */
    private static final int THREAD_COUNT = 100;

    @Autowired
    private ShardReentrantLockComponent lockComponent;

    /**
     * 用来模拟 zookeeper 服务
     */
    private TestingServer server;

    /**
     * 用来实现具体逻辑，对该计数器加1
     */
    private int count;

    @BeforeEach
    public void before() throws Exception {
        //模拟一个zookeeper节点，端口号为 2181
        server = new TestingServer(2181);
    }

    @AfterEach
    public void after() {
        if(server != null) {
            //关闭资源
            CloseableUtils.closeQuietly(server);
        }
    }

    /**
     * 不加锁实现多个线程同时对 count 执行 ++ 操作
     * 会出现数据不一致现象
     * @throws Exception
     */
    @Test
    public void noAcquireLockTest() throws Exception {
        //初始化一个拥有 100 个线程的线程池
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        //使用 CountDownLatch 实现线程的协调
        CountDownLatch countDownLatch = new CountDownLatch(THREAD_COUNT);
        for(int i = 0; i < THREAD_COUNT; i++) {
            final int index = i;
            //提交线程
            executorService.submit(() -> {
                //name 表示该线程的名称
                String name = "client" + (index + 1);
                //执行 count++
                count++;
                try {
                    //睡眠 50ms ,使测试结果更明显
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //打印各个线程执行结果
                System.out.println(name + "    执行业务方法，对 count 执行 ++ 操作后 count 的值为 : " + count);
                //调用countDown方法，表示该线程执行完毕
                countDownLatch.countDown();
            });
        }
        //使该方法阻塞住，不然看不到效果
        countDownLatch.await();
    }

    /**
     * 使用 zookeeper 加锁实现多个线程同时对 count 执行 ++ 操作
     * @throws Exception
     */
    @Test
    public void acquireLockTest() throws Exception {
        //要加锁节点的路径
        String path = "/path/test";
        //初始化一个拥有 100 个线程的线程池
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        //使用 CountDownLatch 实现线程的协调
        CountDownLatch countDownLatch = new CountDownLatch(THREAD_COUNT);

        for(int i = 0; i < THREAD_COUNT; i++) {
            final int index = i;
            //提交线程
            executorService.submit(() -> {
                //name 表示该线程的名称
                String name = "client" + (index + 1);
                //result 获取执行完业务逻辑后返回值
                String result = null;
                while (result == null) {
                    //result 为 null 表示没有的锁，会继续执行while循环去竞争获取锁
                    result = lockComponent.acquireLock(new BaseLockHandler<String>(path) {

                        //执行具体的业务逻辑
                        @Override
                        public String handler() {
                            //执行 count++
                            count++;
                            try {
                                //睡眠 50ms ,使测试结果更明显
                                Thread.sleep(50);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            //打印各个线程执行结果
                            System.out.println(name + "    执行业务方法，对count++ : " + count);

                            //执行成功后不要返回null，如果返回null，会继续执行while去竞争获取锁
                            return this.getPath();
                        }
                    });
                }
                //调用countDown方法，表示该线程执行完毕
                countDownLatch.countDown();
            });
        }
        //使该方法阻塞住，不然看不到效果
        countDownLatch.await();
    }

}