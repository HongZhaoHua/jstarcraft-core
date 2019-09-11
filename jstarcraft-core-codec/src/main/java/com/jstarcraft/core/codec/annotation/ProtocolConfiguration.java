package com.jstarcraft.core.codec.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 协议配置
 * 
 * @author Birdy
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ProtocolConfiguration {

    /**
     * 模式
     * 
     * @author Birdy
     */
    public enum Mode {

        /** 字段 */
        FIELD,

        /** 方法(Getter/Setter) */
        METHOD,

        /** 指定 */
        SPECIFY;

    }

    /**
     * 模式
     */
    Mode mode() default Mode.FIELD;

}
