package com.jstarcraft.core.resource.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Comparator;

/**
 * 资源索引
 * 
 * @author Birdy
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface ResourceIndex {

    /** 名称 */
    String name();

    /** 是否唯一 */
    boolean unique() default false;

    /** 排序器 */
    Class<? extends Comparator> comparator() default Comparator.class;

}
