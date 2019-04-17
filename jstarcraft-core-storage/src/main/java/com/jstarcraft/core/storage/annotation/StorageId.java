package com.jstarcraft.core.storage.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Comparator;

/**
 * 配置标识
 * 
 * @author Birdy
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface StorageId {

	/** 排序器 */
	Class<? extends Comparator> comparator() default Comparator.class;

}
