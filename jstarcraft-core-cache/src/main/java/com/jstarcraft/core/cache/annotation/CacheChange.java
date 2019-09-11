package com.jstarcraft.core.cache.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 缓存变更
 * 
 * <pre>
 * 配合{@link CacheConfiguration}使用,实现缓存变更的自动化管理;
 * </pre>
 * 
 * @author Birdy
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CacheChange {

    /** 触发变更的方法返回值 */
    String[] values() default {};

}
