package com.jstarcraft.core.cache;

/**
 * 缓存对象工厂
 * 
 * @author Birdy
 *
 * @param <K>
 * @param <T>
 */
public interface CacheObjectFactory<K extends Comparable, T extends CacheObject<K>> {

	T instanceOf(K id);

}
