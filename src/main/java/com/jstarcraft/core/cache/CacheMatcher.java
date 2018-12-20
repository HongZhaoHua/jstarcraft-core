package com.jstarcraft.core.cache;

/**
 * 缓存匹配器
 * 
 * @author Birdy
 *
 * @param <K>
 */
public interface CacheMatcher<K extends Comparable, T extends CacheObject<K>> {

	/**
	 * 指定对象是否匹配
	 * 
	 * @param object
	 * @return
	 */
	boolean match(T object);

}
