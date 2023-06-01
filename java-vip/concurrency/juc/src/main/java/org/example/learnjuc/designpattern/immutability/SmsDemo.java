package org.example.learnjuc.designpattern.immutability;


public class SmsDemo {

    public static void main(String[] args) {
        //创建短信服务商的信息
        SmsInfo smsInfo = new SmsInfo("https://www.aliyun.com", 180);

        new Thread(new Runnable() {
            @Override
            public void run() {
//                smsInfo.setUrl("https://cloud.tencent.com");
//                try {
//                    Thread.sleep(100);
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
//                smsInfo.setMaxSizeInBytes(200);
                smsInfo.update("https://cloud.tencent.com",200);
            }
        },"线程1").start();

        //线程2读取短信服务商信息
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("线程2获取短信服务商信息："+smsInfo);
            }
        },"线程2").start();

    }
}
