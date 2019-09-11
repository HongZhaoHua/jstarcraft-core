package com.jstarcraft.core.common.lockable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 可锁定方法
 * 
 * @author Birdy
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface LockableMethod {

    /**
     * 锁策略
     * 
     * @return
     */
    Class<? extends LockableStrategy> strategy();

    /**
     * 是否强制使用锁
     */
    boolean value() default false;

}
