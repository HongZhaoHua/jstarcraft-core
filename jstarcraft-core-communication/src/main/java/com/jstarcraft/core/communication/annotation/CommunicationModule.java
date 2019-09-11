package com.jstarcraft.core.communication.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.jstarcraft.core.utility.StringUtility;

/**
 * 通讯模块
 * 
 * @author Birdy
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface CommunicationModule {

    /** 模块编号 */
    byte[] code();

    /** 模块名称 */
    String name() default StringUtility.EMPTY;

    /** 模块端 */
    ModuleSide side();

    /**
     * 通讯端
     * 
     * @author Birdy
     *
     */
    public enum ModuleSide {

        /** 客户端 */
        CLIENT,

        /** 服务端 */
        SERVER;

    }

}
