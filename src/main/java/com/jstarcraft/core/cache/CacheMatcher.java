package com.jstarcraft.core.cache;

import com.jstarcraft.core.utility.IdentityObject;

/**
 * 缓存匹配器
 * 
 * @author Birdy
 *
 * @param <K>
 */
public interface CacheMatcher<K extends Comparable, T extends IdentityObject<K>> {

	/**
	 * 指定对象是否匹配
	 * 
	 * @param object
	 * @return
	 */
	boolean match(T object);

}
