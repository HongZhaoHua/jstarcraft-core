package com.jstarcraft.core.cache.persistence;

import com.jstarcraft.core.cache.CacheObject;
import com.jstarcraft.core.cache.persistence.PersistenceStrategy.PersistenceOperation;

/**
 * 持久监听器
 * 
 * @author Birdy
 */
public interface PersistenceMonitor {

	/**
	 * 操作通知
	 * 
	 * @param operation
	 * @param result
	 * @param id
	 * @param object
	 * @param exception
	 */
	void notifyOperate(PersistenceOperation operation, Object id, CacheObject<?> object, Exception exception);

}
