package com.jstarcraft.core.cache.aspect;

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

	/**
	 * 获取类型唯一锁
	 * 
	 * @return
	 */
	public Lock getClassLock() {
		return clazzLock;
	}

	public static CompareLockManager getManager(Class clazz) {
		synchronized (clazz) {
			CompareLockManager manager = sortLockManagers.get(clazz);
			if (manager == null) {
				manager = new CompareLockManager();
				sortLockManagers.put(clazz, manager);
			}
			return manager;
		}
	}

}
