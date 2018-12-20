package com.jstarcraft.core.cache;

import java.util.Map;

/**
 * 缓存管理器
 * 
 * @author Birdy
 *
 */
public interface CacheManager<K extends Comparable, T extends CacheObject<K>> {

	/**
	 * 获取缓存实例数量
	 * 
	 * @return
	 */
	int getInstanceCount();

	/**
	 * 获取缓存索引数量
	 * 
	 * @return
	 */
	Map<String, Integer> getIndexesCount();

}
