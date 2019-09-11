package com.jstarcraft.core.communication.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Array;
import java.util.Map;

import com.jstarcraft.core.communication.message.MessageHead;
import com.jstarcraft.core.communication.message.MessageTail;
import com.jstarcraft.core.communication.session.CommunicationSession;
import com.jstarcraft.core.utility.StringUtility;

/**
 * 指令变量
 * 
 * <pre>
 * 配合{@link CommunicationCommand}实现指令输入/输出类型与消息体类型之间的转换.
 * </pre>
 * 
 * @author Birdy
 *
 */
@Target({ ElementType.TYPE_USE, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandVariable {

    /** 类型 */
    VariableType type() default VariableType.MESSAGE_BODY;

    /** 属性 */
    String property() default StringUtility.EMPTY;

    /** 是否可以为Null */
    boolean nullable() default false;

    /**
     * 变量类型
     * 
     * @author Birdy
     *
     */
    public enum VariableType {

        /**
         * 消息头
         * 
         * <pre>
         * {@link MessageHead}的Getter
         * </pre>
         * 
         */
        MESSAGE_HEAD,

        /**
         * 消息体
         * 
         * <pre>
         * {@link Map}的Key
         * {@link Object}的Field/Getter
         * {@link Array}的Index
         * </pre>
         */
        MESSAGE_BODY,

        /**
         * 消息尾
         * 
         * <pre>
         * {@link MessageTail}的Getter
         * </pre>
         * 
         */
        MESSAGE_TAIL,

        /**
         * 会话属性
         * 
         * <pre>
         * {@link CommunicationSession}的Getter
         * </pre>
         * 
         */
        SESSION_ATTRIBUTE,

        /**
         * 会话上下文
         * 
         * <pre>
         * {@link CommunicationSession#getContext(String)}的Name
         * </pre>
         * 
         */
        SESSION_CONTEXT,

    }

}
