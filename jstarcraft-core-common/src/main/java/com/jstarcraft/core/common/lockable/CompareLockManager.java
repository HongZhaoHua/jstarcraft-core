package com.jstarcraft.core.common.lockable;

import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 排序锁管理器
 * 
 * @author Birdy
 */
@SuppressWarnings("rawtypes")
public class CompareLockManager {

    /** 排序锁管理器 */
    private static final ConcurrentHashMap<Class, CompareLockManager> sortLockManagers = new ConcurrentHashMap<Class, CompareLockManager>();
    /** 类型锁 */
    private final Lock clazzLock = new ReentrantLock();
    /** 实例锁(使用弱引用缓存) */
    private final WeakHashMap<Object, CompareLock> instanceLocks = new WeakHashMap<Object, CompareLock>();

    private CompareLockManager() {
    }

    /**
     * 获取类型锁
     * 
     * @return
     */
    public Lock getClassLock() {
        return clazzLock;
    }

    /**
     * 获取实例锁
     * 
     * @param instance
     * @return
     */
    public CompareLock getInstanceLock(Comparable instance) {
        synchronized (instance) {
            CompareLock value = instanceLocks.get(instance);
            if (value == null) {
                value = new CompareLock(instance, false);
                instanceLocks.put(instance, value);
            }
            return value;
        }
    }

    public static CompareLockManager getManager(Class clazz) {
        CompareLockManager manager = sortLockManagers.get(clazz);
        if (manager == null) {
            synchronized (clazz) {
                if (manager == null) {
                    manager = new CompareLockManager();
                    sortLockManagers.put(clazz, manager);
                }
            }
        }
        return manager;
    }

}
