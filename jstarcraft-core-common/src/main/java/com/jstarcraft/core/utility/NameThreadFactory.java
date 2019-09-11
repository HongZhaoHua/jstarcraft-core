package com.jstarcraft.core.utility;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 命名线程工厂
 * 
 * @author Birdy
 */
public class NameThreadFactory implements ThreadFactory {

    /** 线程组 */
    protected ThreadGroup group;
    /** 线程数量 */
    protected AtomicInteger number = new AtomicInteger();

    public NameThreadFactory(String name) {
        this.group = new ThreadGroup(name);
    }

    @Override
    public Thread newThread(Runnable runnable) {
        String name = group.getName() + StringUtility.COLON + number.incrementAndGet();
        Thread thread = new Thread(group, runnable, name, 0);
        return thread;
    }

}
