package com.jstarcraft.core.storage.berkeley.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.jstarcraft.core.utility.StringUtility;

/**
 * Berkeley配置
 * 
 * @author Birdy
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface BerkeleyConfiguration {

    /** 贮存名称 */
    String store();

    /** 版本字段(类型必须是{@link Integer}) */
    String version() default StringUtility.EMPTY;

}
