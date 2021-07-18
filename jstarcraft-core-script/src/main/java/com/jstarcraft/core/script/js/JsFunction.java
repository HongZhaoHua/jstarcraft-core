package com.jstarcraft.core.script.js;

import java.lang.reflect.Method;
import java.util.Map.Entry;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import com.jstarcraft.core.script.ScriptContext;
import com.jstarcraft.core.script.ScriptFunction;
import com.jstarcraft.core.script.exception.ScriptExpressionException;
import com.jstarcraft.core.utility.StringUtility;

/**
 * JS函数
 * 
 * @author Birdy
 *
 */
public class JsFunction implements ScriptFunction {

    private final static String ENGINE_NAME = "nashorn";

    private String function;

    private Invocable engine;

    private String name;

    private Class<?>[] classes;

    public JsFunction(ScriptContext context, String function, String name, Class<?>... classes) {
        StringBuilder buffer = new StringBuilder();
        for (Entry<String, Class<?>> keyValue : context.getClasses().entrySet()) {
            buffer.append(StringUtility.format("var {} = Java.type('{}'); ", keyValue.getKey(), keyValue.getValue().getName()));
        }
        for (Entry<String, Method> keyValue : context.getMethods().entrySet()) {
            buffer.append(StringUtility.format("var {} = Java.type('{}').{}; ", keyValue.getKey(), keyValue.getValue().getDeclaringClass().getName(), keyValue.getValue().getName()));
        }
        buffer.append(function);
        this.function = buffer.toString();
        try {
            ScriptEngineManager factory = new ScriptEngineManager();
            ScriptEngine engine = factory.getEngineByName(ENGINE_NAME);
            engine.eval(this.function);
            this.engine = (Invocable) engine;
            this.name = name;
            this.classes = classes;
        } catch (ScriptException exception) {
            throw new ScriptExpressionException(exception);
        }
    }

    @Override
    public <T> T doWith(Class<T> clazz, Object... arguments) {
        try {
            T object = (T) engine.invokeFunction(name, arguments);
            return object;
        } catch (Exception exception) {
            throw new ScriptExpressionException(exception);
        }
    }

}
