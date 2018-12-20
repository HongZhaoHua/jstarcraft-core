package com.jstarcraft.core.storage.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.jstarcraft.core.utility.StringUtility;

/**
 * 仓储配置
 * 
 * @author Birdy
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface StorageConfiguration {

	/** 仓储格式 */
	String format() default StringUtility.EMPTY;

	/** 仓储位置 */
	String path() default StringUtility.EMPTY;

}
