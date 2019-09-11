package com.jstarcraft.core.cache.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.jstarcraft.core.cache.EntityManager;
import com.jstarcraft.core.cache.RegionManager;
import com.jstarcraft.core.cache.persistence.PersistenceStrategy;
import com.jstarcraft.core.cache.transience.TransienceStrategy;

/**
 * 缓存配置
 * 
 * <pre>
 * 配合{@link CacheChange}使用,实现缓存变更的自动化管理;
 * </pre>
 * 
 * @author Birdy
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CacheConfiguration {

    /**
     * 缓存单位
     * 
     * <pre>
     * 决定由哪种缓存管理器管理
     * </pre>
     * 
     * @author Birdy
     */
    public enum Unit {

        /** 实体,配合{@link EntityManager}使用. */
        ENTITY,

        /** 域,配合{@link RegionManager}使用. */
        REGION;

    }

    /** 缓存单位 */
    Unit unit();

    /** 缓存索引 */
    String[] indexes() default {};

    /** 内存策略,配合{@link TransienceStrategy}使用. */
    String transienceStrategy();

    /** 持久策略,配合{@link PersistenceStrategy}使用. */
    String persistenceStrategy();

}
