package com.example.dblock;

import com.example.dblock.service.LockService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DbLockApplicationTests {

    @Autowired
    private LockService lockService;

    @Test
    void testLock() {

        for (int i1 = 0; i1 < 4; i1++) {
            boolean lock = lockService.tryLock("myLock", 30);
            if (lock) {
                try {
                    System.out.println("get lock ==" + i1);
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    lockService.unlock("myLock");
                }
            } else {
                try {
                    System.out.println("can't get lock ==" + i1);
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}
