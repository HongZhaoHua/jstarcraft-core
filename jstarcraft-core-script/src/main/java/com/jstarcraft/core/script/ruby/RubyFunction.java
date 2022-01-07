package com.jstarcraft.core.script.ruby;

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
 * Ruby函数
 * 
 * @author Birdy
 *
 */
public class RubyFunction implements ScriptFunction {

    private final static String ENGINE_NAME = "ruby";

    private final static ScriptEngineManager factory = new ScriptEngineManager();

    static {
        // TODO 配置JRuby环境,参考org.jruby.embed.PropertyName
        System.setProperty("org.jruby.embed.localcontext.scope", "singleton");
        System.setProperty("org.jruby.embed.localvariable.behavior", "transient");
    }

    private final static ScriptEngine engine = factory.getEngineByName(ENGINE_NAME);

    private final static Invocable invocable = (Invocable) engine;

    private String function;

    private String name;

    private Class<?>[] classes;

    public RubyFunction(ScriptContext context, String function, String name, Class<?>... classes) {
        String separator = System.getProperty("line.separator");
        StringBuilder buffer = new StringBuilder("require 'java'");
        buffer.append(separator);
        for (Entry<String, Class<?>> keyValue : context.getClasses().entrySet()) {
            buffer.append(StringUtility.format("java_import {}", keyValue.getValue().getName()));
            buffer.append(separator);
            buffer.append(StringUtility.format("{} = {}", keyValue.getKey(), keyValue.getValue().getName()));
            buffer.append(separator);
        }
        for (Entry<String, Method> keyValue : context.getMethods().entrySet()) {
            buffer.append(StringUtility.format("java_import {}", keyValue.getValue().getDeclaringClass().getName()));
            buffer.append(separator);
            buffer.append(StringUtility.format("def {}(*arguments) {}.{}(*arguments) end", keyValue.getKey(), keyValue.getValue().getDeclaringClass().getName(), keyValue.getValue().getName()));
            buffer.append(separator);
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
