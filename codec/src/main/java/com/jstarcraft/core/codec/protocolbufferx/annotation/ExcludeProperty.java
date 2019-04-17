package com.jstarcraft.core.codec.protocolbufferx.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 排除属性
 * 
 * <pre>
 * 配合{@link ProtocolConfiguration.Mode.FIELD/METHOD},实现排除特定的属性.
 * </pre>
 * 
 * @author Birdy
 */
@Target({ ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcludeProperty {

}
