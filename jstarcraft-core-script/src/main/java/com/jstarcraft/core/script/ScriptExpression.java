package com.jstarcraft.core.script;

import java.util.Map;

/**
 * 脚本表达式
 * 
 * @author Birdy
 *
 */
public interface ScriptExpression {

    /**
     * 执行表达式
     * 
     * @param <T>
     * @param clazz
     * @param scope
     * @return
     */
    <T> T doWith(Class<T> clazz, Map<String, Object> scope);

}
