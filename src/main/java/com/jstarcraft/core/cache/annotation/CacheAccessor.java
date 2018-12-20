package com.jstarcraft.core.cache.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.jstarcraft.core.cache.CacheService;
import com.jstarcraft.core.cache.EntityManager;
import com.jstarcraft.core.cache.RegionManager;

/**
 * 缓存访问器
 * 
 * <pre>
 * 配合{@link CacheService},{@link EntityManager}, {@link RegionManager}使用,实现Spring的自动装配.
 * </pre>
 * 
 * @author Birdy
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface CacheAccessor {

}
