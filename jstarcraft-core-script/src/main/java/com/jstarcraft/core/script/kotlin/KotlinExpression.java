package com.jstarcraft.core.script.kotlin;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Map.Entry;

import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;

import com.jstarcraft.core.script.ScriptContext;
import com.jstarcraft.core.script.ScriptExpression;
import com.jstarcraft.core.script.exception.ScriptExpressionException;
import com.jstarcraft.core.utility.StringUtility;

/**
 * Kotlin表达式
 * 
 * @author Birdy
 *
 */
public class KotlinExpression implements ScriptExpression {

    private final static String ENGINE_NAME = "kotlin";

    private final static ScriptEngineManager factory = new ScriptEngineManager();

   
    private String expression;

    private ScriptEngine engine;
    
    private javax.script.ScriptContext attributes;

    private CompiledScript script;

    public KotlinExpression(ScriptContext context, String expression) {
        StringBuilder buffer = new StringBuilder();
        for (Entry<String, Class<?>> keyValue : context.getClasses().entrySet()) {
            buffer.append(StringUtility.format("import {} as {}; ", keyValue.getValue().getName(), keyValue.getKey()));
        }
        for (Entry<String, Method> keyValue : context.getMethods().entrySet()) {
            buffer.append(StringUtility.format("import {}.{} as {}; ", keyValue.getValue().getDeclaringClass().getName(), keyValue.getValue().getName(), keyValue.getKey()));
        }
        buffer.append(expression);
        this.expression = buffer.toString();
        try {
            this.engine = factory.getEngineByName(ENGINE_NAME);
            Compilable compilable = (Compilable) engine;
            this.attributes = new SimpleScriptContext();
            this.attributes.setBindings(engine.getContext().getBindings(javax.script.ScriptContext.GLOBAL_SCOPE), javax.script.ScriptContext.GLOBAL_SCOPE);
            this.attributes.setWriter(engine.getContext().getWriter());
            this.attributes.setReader(engine.getContext().getReader());
            this.attributes.setErrorWriter(engine.getContext().getErrorWriter());
            this.script = compilable.compile(this.expression);
        } catch (ScriptException exception) {
            throw new ScriptExpressionException(exception);
        }
    }

    @Override
    public synchronized <T> T doWith(Class<T> clazz, Map<String, Object> scope) {
        try {
            attributes.getBindings(javax.script.ScriptContext.ENGINE_SCOPE).putAll(scope);
            T object = (T) script.eval(attributes);
            return object;
        } catch (ScriptException exception) {
            throw new ScriptExpressionException(exception);
        }
    }

    @Override
    public String toString() {
        return expression;
    }

}
