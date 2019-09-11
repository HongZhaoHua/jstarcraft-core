package com.jstarcraft.core.common.lockable;

import java.util.concurrent.locks.ReentrantLock;

/**
 * 哈希锁
 * 
 * @author Birdy
 *
 */
public class HashLockable implements Lockable {

    private ReentrantLock lock;

    public HashLockable() {
        this.lock = new ReentrantLock();
    }

    @Override
    public void open() {
        lock.lock();
    }

    @Override
    public void close() {
        lock.unlock();
    }

}
