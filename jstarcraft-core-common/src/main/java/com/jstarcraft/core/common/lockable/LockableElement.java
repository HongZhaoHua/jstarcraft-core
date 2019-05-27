package com.jstarcraft.core.common.lockable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 可锁定元素
 * 
 * <pre>
 * 支持Array, Collection, Map(不锁键,锁值)
 * </pre>
 * 
 * @author Birdy
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface LockableElement {

}
