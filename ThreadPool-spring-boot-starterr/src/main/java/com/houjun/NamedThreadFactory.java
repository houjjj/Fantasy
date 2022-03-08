package com.houjun;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 带命名的线程创建工厂
 * @author liyang
 * @date 2019-09-03
 */
public class NamedThreadFactory implements ThreadFactory {

    private static final AtomicInteger POOL_NUMBER = new AtomicInteger(1);

    private final ThreadGroup group;

    private final AtomicInteger threadNumber = new AtomicInteger(1);

    private final String namePrefix;

    public NamedThreadFactory(final String name) {
        final SecurityManager s = System.getSecurityManager();
        this.group = s != null ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
        this.namePrefix = generateNamePrefix(name);
    }

    private static String generateNamePrefix(final String name) {
        return name + "-" + POOL_NUMBER.getAndIncrement() + "-thread-";
    }

    @Override
    public Thread newThread(final Runnable r) {
        Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
        if (t.isDaemon()) {
            t.setDaemon(false);
        }
        if (t.getPriority() != Thread.NORM_PRIORITY) {
            t.setPriority(Thread.NORM_PRIORITY);
        }
        return t;
    }
}
