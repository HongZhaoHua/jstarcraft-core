package com.jstarcraft.core.communication.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.jstarcraft.core.communication.message.MessageFormat;

/**
 * 指令编解码
 * 
 * @author Birdy
 *
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface MessageCodec {

    /** 输入格式(默认使用通讯模块的格式,{@link MessageFormat#toByte(MessageFormat, boolean)}) */
    MessageFormat inputFormat();

    /** 输出格式(默认使用通讯模块的格式,{@link MessageFormat#toByte(MessageFormat, boolean)}) */
    MessageFormat outputFormat();

    /** 输入是否zip */
    boolean inputZip() default false;

    /** 输出是否zip */
    boolean outputZip() default false;

}
