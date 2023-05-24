package org.example.learnjuc.blockingqueue;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class OrderDemo {
    private static final DelayQueue<Order> delayQueue = new DelayQueue<>();

    static class Order implements Delayed {
        private String orderId;
        private ZonedDateTime expireTime;

        public Order(String orderId, ZonedDateTime expireTime) {
            this.orderId = orderId;
            this.expireTime = expireTime;
        }

        public String getOrderId() {
            return orderId;
        }

        public void setOrderId(String orderId) {
            this.orderId = orderId;
        }

        public ZonedDateTime getExpireTime() {
            return expireTime;
        }

        public void setExpireTime(ZonedDateTime expireTime) {
            this.expireTime = expireTime;
        }

        @Override
        public long getDelay(TimeUnit unit) {
            long delay = unit.convert(expireTime.toInstant().toEpochMilli() - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
            return delay;
        }

        @Override
        public int compareTo(Delayed o) {
            if (this.getDelay(TimeUnit.MILLISECONDS) > o.getDelay(TimeUnit.MILLISECONDS)) {
                return 1;
            } else if (this.getDelay(TimeUnit.MILLISECONDS) < o.getDelay(TimeUnit.MILLISECONDS)) {
                return -1;
            } else {
                return 0;
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        String orderId = "12345";
        //订单延时5s消费
        ZonedDateTime expireTime = ZonedDateTime.now(ZoneId.of("UTC")).plus(5, ChronoUnit.SECONDS);
        Order order = new Order(orderId, expireTime);
        delayQueue.put(order);
        System.out.println("订单已创建：" + order.getOrderId());

        //阻塞
        Order orderFromQueue = delayQueue.take();
        if (orderFromQueue == order) {
            // 从数据库查询订单的支付状态
            System.out.println("订单延时消费：" + order.getOrderId());
        }
    }
}
