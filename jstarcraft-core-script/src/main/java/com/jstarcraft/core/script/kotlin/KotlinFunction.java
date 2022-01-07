package com.jstarcraft.core.script.kotlin;

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
 * Kotlin函数
 * 
 * @author Birdy
 *
 */
public class KotlinFunction implements ScriptFunction {

    private final static String ENGINE_NAME = "kotlin";

    private final static ScriptEngineManager factory = new ScriptEngineManager();

    private final static ScriptEngine engine = factory.getEngineByName(ENGINE_NAME);

    private final static Invocable invocable = (Invocable) engine;

    private String function;

    private String name;

    private Class<?>[] classes;

    public KotlinFunction(ScriptContext context, String function, String name, Class<?>... classes) {
        StringBuilder buffer = new StringBuilder();
        for (Entry<String, Class<?>> keyValue : context.getClasses().entrySet()) {
            buffer.append(StringUtility.format("import {} as {}; ", keyValue.getValue().getName(), keyValue.getKey()));
        }
        for (Entry<String, Method> keyValue : context.getMethods().entrySet()) {
            buffer.append(StringUtility.format("import {}.{} as {}; ", keyValue.getValue().getDeclaringClass().getName(), keyValue.getValue().getName(), keyValue.getKey()));
        }
        buffer.append(function);
        this.function = buffer.toString();
        try {
            engine.eval(this.function);
            this.name = name;
            this.classes = classes;
        } catch (ScriptException exception) {
            throw new ScriptExpressionException(exception);
        }
    }

    @Override
    public <T> T doWith(Class<T> clazz, Object... arguments) {
        try {
            T object = (T) invocable.invokeFunction(name, arguments);
            return object;
        } catch (Exception exception) {
            throw new ScriptExpressionException(exception);
        }
    }

    @Override
    public String toString() {
        return function;
    }

}
