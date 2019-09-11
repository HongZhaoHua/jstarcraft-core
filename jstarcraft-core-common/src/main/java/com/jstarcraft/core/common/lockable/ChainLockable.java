package com.jstarcraft.core.common.lockable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.concurrent.locks.Lock;

/**
 * 链锁
 * 
 * @author Birdy
 */
public class ChainLockable implements Lockable {

    private static final Comparator CLASS_COMPARATOR = new Comparator<Class>() {

        @Override
        public int compare(Class left, Class right) {
            return left.getName().compareTo(right.getName());
        }

    };

    /** 锁 */
    private final ArrayList<? extends Lock> locks;

    private ChainLockable(ArrayList<? extends Lock> locks) {
        this.locks = locks;
    }

    /**
     * 开启排序锁
     */
    @Override
    public void open() {
        for (Lock lock : locks) {
            lock.lock();
        }
    }

    /**
     * 关闭排序锁
     */
    @Override
    public void close() {
        for (Lock lock : locks) {
            lock.unlock();
        }
    }

    /**
     * 按照规则排序锁
     * 
     * @param objects
     * @return
     */
    static ArrayList<Lock> sortLocks(Comparable... objects) {
        if (objects.length == 0) {
            throw new IllegalArgumentException();
        }
        // 获取实例的锁
        ArrayList<CompareLock> instanceLocks = new ArrayList<>(objects.length);
        for (Comparable object : objects) {
            CompareLockManager manager = CompareLockManager.getManager(object.getClass());
            CompareLock lock = manager.getInstanceLock(object);
            instanceLocks.add(lock);
        }
        Collections.sort(instanceLocks);
        // 获取类型的锁
        TreeMap<Class, Lock> classLocks = new TreeMap<>(CLASS_COMPARATOR);
        Iterator<CompareLock> iterator = instanceLocks.iterator();
        CompareLock left = iterator.next();
        while (iterator.hasNext()) {
            CompareLock right = iterator.next();
            if (left.equals(right)) {
                Class clazz = left.getClazz();
                CompareLockManager manager = CompareLockManager.getManager(clazz);
                classLocks.put(clazz, manager.getClassLock());
            }
            left = right;
        }

        ArrayList<Lock> locks = new ArrayList<>(classLocks.size() + instanceLocks.size());
        locks.addAll(classLocks.values());
        locks.addAll(instanceLocks);

        return locks;
    }

    /**
     * 
     * @param objects
     * @return
     */
    public static ChainLockable instanceOf(Comparable... objects) {
        ArrayList<? extends Lock> locks = sortLocks(objects);
        return new ChainLockable(locks);
    }

}
