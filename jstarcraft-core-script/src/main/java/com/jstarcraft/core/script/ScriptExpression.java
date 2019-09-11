package com.jstarcraft.core.script;

/**
 * 脚本表达式
 * 
 * @author Birdy
 *
 */
public interface ScriptExpression {

    /**
     * 获取表达式范围
     * 
     * @return
     */
    ScriptScope getScope();

    /**
     * 执行表达式
     * 
     * @param clazz
     * @return
     */
    <T> T doWith(Class<T> clazz);

}
