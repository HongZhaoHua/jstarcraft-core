package com.jstarcraft.core.common.lockable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 可锁定参数
 * 
 * @author Birdy
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface LockableParameter {

    // TODO 考虑取消此方法, 直接使用Lock4Element
    /**
     * 是否锁定对象中的元素<br/>
     * 支持<code>Collection</code>,<code>Array</code>,<code>Map</code>(只锁定value)
     * 
     * @return
     */
    boolean element() default false;

}
