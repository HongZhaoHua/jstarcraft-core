package com.jstarcraft.core.resource.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.jstarcraft.core.utility.StringUtility;

/**
 * 资源配置
 * 
 * @author Birdy
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ResourceConfiguration {

    /** 资源前缀 */
    String prefix() default StringUtility.EMPTY;

    /** 资源后缀 */
    String suffix() default StringUtility.EMPTY;

}
