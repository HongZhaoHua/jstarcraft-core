package com.jstarcraft.core.resource.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.jstarcraft.core.utility.StringUtility;

/**
 * 资源配置
 * 
 * @author Birdy
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ResourceConfiguration {

	/** 资源格式 */
	String format() default StringUtility.EMPTY;

	/** 资源位置 */
	String path() default StringUtility.EMPTY;

}
