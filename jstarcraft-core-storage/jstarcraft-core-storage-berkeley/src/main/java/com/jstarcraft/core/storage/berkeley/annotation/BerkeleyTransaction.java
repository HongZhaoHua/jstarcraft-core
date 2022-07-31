package com.jstarcraft.core.storage.berkeley.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.jstarcraft.core.storage.berkeley.BerkeleyIsolation;

/**
 * Berkeley事务
 * 
 * <pre>
 * 注意:由于Berkeley目前没有实现嵌套事务,所以在AOP拦截中,只有最外层的BerkeleyTransaction生效.
 * </pre>
 * 
 * @author Birdy
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface BerkeleyTransaction {

    /** 隔离级别 */
    BerkeleyIsolation isolation();

    /** 冲突等待(单位:毫秒) */
    int conflictWait() default 100;

    /** 尝试次数 */
    int tryTimes() default 5;

}
