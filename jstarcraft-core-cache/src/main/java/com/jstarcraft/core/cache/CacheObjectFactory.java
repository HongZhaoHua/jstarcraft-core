package com.jstarcraft.core.cache;

import com.jstarcraft.core.common.identification.IdentityObject;

/**
 * 缓存对象工厂
 * 
 * @author Birdy
 *
 * @param <K>
 * @param <T>
 */
public interface CacheObjectFactory<K extends Comparable, T extends IdentityObject<K>> {

    T instanceOf(K id);

}
