package com.jstarcraft.core.resource.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 资源访问器
 * 
 * @author Birdy
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ResourceAccessor {

    /**
     * 仓储类型(默认注解的字段类型作为仓储类型)
     * 
     * @return
     */
    Class<?> clazz() default Void.class;

    /**
     * 仓储
     * 
     * @return
     */
    String storage() default "resourceStorage";

    /**
     * 标识
     * 
     * @return
     */
    String value() default "";

    /**
     * 属性
     * 
     * @return
     */
    String property() default "";

    /**
     * 是否必须
     * 
     * @return
     */
    boolean necessary() default true;

}
