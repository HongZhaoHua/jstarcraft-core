package com.jstarcraft.core.script.lua;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Map.Entry;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import com.jstarcraft.core.script.ScriptContext;
import com.jstarcraft.core.script.ScriptExpression;
import com.jstarcraft.core.script.exception.ScriptExpressionException;
import com.jstarcraft.core.utility.StringUtility;

/**
 * Lua表达式
 * 
 * @author Birdy
 *
 */
public class LuaExpression implements ScriptExpression {

    private final static String ENGINE_NAME = "luaj";

    private final static ScriptEngineManager factory = new ScriptEngineManager();

    private final static ScriptEngine engine = factory.getEngineByName(ENGINE_NAME);

    private final static Compilable compilable = (Compilable) engine;

    private String expression;

    private Bindings attributes;

    private CompiledScript script;

    public LuaExpression(ScriptContext context, String expression) {
        StringBuilder buffer = new StringBuilder();
        for (Entry<String, Class<?>> keyValue : context.getClasses().entrySet()) {
            buffer.append(StringUtility.format("local {} = luajava.bindClass('{}'); ", keyValue.getKey(), keyValue.getValue().getName()));
        }
        for (Entry<String, Method> keyValue : context.getMethods().entrySet()) {
            buffer.append(StringUtility.format("local {} = luajava.bindClass('{}').{}; ", keyValue.getKey(), keyValue.getValue().getDeclaringClass().getName(), keyValue.getValue().getName()));
        }
        buffer.append(expression);
        this.expression = buffer.toString();
        try {
            this.attributes = engine.getBindings(javax.script.ScriptContext.ENGINE_SCOPE);
            this.script = compilable.compile(this.expression);
        } catch (ScriptException exception) {
            throw new ScriptExpressionException(exception);
        }
    }

    @Override
    public <T> T doWith(Class<T> clazz, Map<String, Object> scope) {
        try {
            synchronized (engine) {
                attributes.putAll(scope);
                T object = (T) script.eval();
                attributes.clear();
                return object;
            }
        } catch (ScriptException exception) {
            throw new ScriptExpressionException(exception);
        }
    }

    @Override
    public String toString() {
        return expression;
    }

}