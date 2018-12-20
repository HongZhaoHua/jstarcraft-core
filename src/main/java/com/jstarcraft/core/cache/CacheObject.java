package com.jstarcraft.core.cache;

/**
 * 缓存对象
 * 
 * <pre>
 * 所有缓存类型都需要实现此接口
 * </pre>
 * 
 * @author Birdy
 *
 * @param <K>
 */
public interface CacheObject<K extends Comparable> extends Comparable<CacheObject> {

	/**
	 * 获取缓存标识
	 * 
	 * @return
	 */
	K getId();

	default int compareTo(CacheObject that) {
		int compare = this.getClass().getName().compareTo(that.getClass().getName());
		if (compare == 0) {
			return this.getId().compareTo(that.getId());
		} else {
			return compare;
		}
	}

}
