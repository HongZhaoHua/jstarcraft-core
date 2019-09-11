package com.jstarcraft.core.script;

/**
 * 脚本函数
 * 
 * @author Birdy
 *
 */
public interface ScriptFunction {

    /**
     * 执行函数
     * 
     * @param clazz
     * @param arguments
     * @return
     */
    <T> T doWith(Class<T> clazz, Object... arguments);

}
