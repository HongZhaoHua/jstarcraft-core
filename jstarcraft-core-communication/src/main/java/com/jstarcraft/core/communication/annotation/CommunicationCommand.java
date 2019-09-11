package com.jstarcraft.core.communication.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.jstarcraft.core.utility.StringUtility;

/**
 * 通讯指令
 * 
 * @author Birdy
 *
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface CommunicationCommand {

    /** 指令编号 */
    byte code();

    /** 指令名称 */
    String name() default StringUtility.EMPTY;

    /** 指令执行策略 */
    String strategy() default StringUtility.EMPTY;

    /** 指令输入类型(消息体内容的类型, void.class代表以参数类型作为指令输入类型) */
    Class<?> input() default void.class;

    /** 指令输出类型(消息体内容的类型, void.class代表以返回类型作为指令输出类型) */
    Class<?> output() default void.class;

}
