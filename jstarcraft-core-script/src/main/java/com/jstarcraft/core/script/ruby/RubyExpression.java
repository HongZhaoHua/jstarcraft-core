package com.jstarcraft.core.script.ruby;

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
 * Ruby表达式
 * 
 * @author Birdy
 *
 */
public class RubyExpression implements ScriptExpression {

    private final static String ENGINE_NAME = "ruby";

    private final static ScriptEngineManager factory;

    static {
        // TODO 配置JRuby环境,参考org.jruby.embed.PropertyName
        System.setProperty("org.jruby.embed.localcontext.scope", "threadsafe");
        System.setProperty("org.jruby.embed.localvariable.behavior", "global");
        factory = new ScriptEngineManager();
    }

    private final static ScriptEngine engine = factory.getEngineByName(ENGINE_NAME);

    private final static Compilable compilable = (Compilable) engine;

    private String expression;

    private Bindings attributes;

    private CompiledScript script;

    public RubyExpression(ScriptContext context, String expression) {
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
                T object = (T) script.eval(attributes);
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
