package com.beinglee.top.customize;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * 基于AQS(共享锁模式)实现的限流器
 */

public class LimitLatch {

    private class Sync extends AbstractQueuedSynchronizer {

        @Override
        protected int tryAcquireShared(int arg) {
            long newCount = count.incrementAndGet();
            // 流量已经超过Limit限制了，要排队获取锁。
            if (!released && newCount > limit) {
                count.decrementAndGet();
                return -1;
            } else {
                return 1;
            }
        }

        @Override
        protected boolean tryReleaseShared(int arg) {
            count.decrementAndGet();
            return true;
        }
    }

    private volatile long limit;

    private final Sync sync;

    private final AtomicLong count;

    private volatile boolean released = false;


    public LimitLatch(long limit) {
        this.limit = limit;
        this.sync = new Sync();
        this.count = new AtomicLong(0);
    }


    public long getLimit() {
        return limit;
    }

    public long getCount() {
        return count.longValue();
    }

    public void setLimit(long limit) {
        this.limit = limit;
    }

    public boolean releaseAll() {
        released = true;
        return sync.releaseShared(0);
    }

    public void reset() {
        this.count.set(0);
        released = false;
    }

    public void countUpOrWait() throws InterruptedException {
        sync.acquireSharedInterruptibly(1);
        System.out.println("Counting up[" + Thread.currentThread().getName() + "] latch=" + getCount());
    }

    public void countdown() {
        sync.releaseShared(0);
        long result = getCount();
        System.out.println("Counting down[" + Thread.currentThread().getName() + "] latch=" + result);
    }

    public boolean hasQueuedThreads() {
        return sync.hasQueuedThreads();
    }

    public Collection<Thread> getQueuedThreads() {
        return sync.getQueuedThreads();
    }

}
