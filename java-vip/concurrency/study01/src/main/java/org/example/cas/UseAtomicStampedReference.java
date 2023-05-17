package org.example.cas;

import java.util.concurrent.atomic.AtomicStampedReference;

/**
 *类说明：演示带版本戳的原子操作类
 */
public class UseAtomicStampedReference {
    static AtomicStampedReference<String> asr
            = new AtomicStampedReference("mark",0);

    public static void main(String[] args) throws InterruptedException {
        //拿到当前的版本号(旧)
        final int oldStamp = asr.getStamp();
        final String oldReference = asr.getReference();
        System.out.println(oldReference+"============"+oldStamp);

        Thread rightStampThread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName()+":当前变量值："
                        +oldReference + "-当前版本戳：" + oldStamp + "-"
                  + asr.compareAndSet(oldReference,
                        oldReference + "+Java", oldStamp,
                        oldStamp + 1));
            }
        });

        Thread errorStampThread = new Thread(new Runnable() {
            @Override
            public void run() {
                String reference = asr.getReference();
                System.out.println(Thread.currentThread().getName()
                        +":当前变量值："
                        +reference + "-当前版本戳：" + asr.getStamp() + "-"
                        + asr.compareAndSet(reference,
                        reference + "+C", oldStamp,
                        oldStamp + 1));
            }
        });
        rightStampThread.start();
        rightStampThread.join();
        errorStampThread.start();
        errorStampThread.join();

        System.out.println(asr.getReference()+"============"+asr.getStamp());
    }
}
