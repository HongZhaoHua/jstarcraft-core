package com.jstarcraft.core.script.js;

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
 * JS表达式
 * 
 * @author Birdy
 *
 */
public class JsExpression implements ScriptExpression {

    private final static String ENGINE_NAME = "nashorn";

    private final static ScriptEngineManager factory = new ScriptEngineManager();

    private String expression;

    private Bindings attributes;

    private CompiledScript script;

    public JsExpression(ScriptContext context, String expression) {
        StringBuilder buffer = new StringBuilder();
        for (Entry<String, Class<?>> keyValue : context.getClasses().entrySet()) {
            buffer.append(StringUtility.format("var {} = Java.type('{}'); ", keyValue.getKey(), keyValue.getValue().getName()));
        }
        for (Entry<String, Method> keyValue : context.getMethods().entrySet()) {
            buffer.append(StringUtility.format("var {} = Java.type('{}').{}; ", keyValue.getKey(), keyValue.getValue().getDeclaringClass().getName(), keyValue.getValue().getName()));
        }
        buffer.append(expression);
        this.expression = buffer.toString();
        try {
            ScriptEngine engine = factory.getEngineByName(ENGINE_NAME);
            this.attributes = engine.getBindings(javax.script.ScriptContext.ENGINE_SCOPE);
            Compilable compilable = (Compilable) engine;
            this.script = compilable.compile(this.expression);
        } catch (ScriptException exception) {
            throw new ScriptExpressionException(exception);
        }
    }

    @Override
    public synchronized <T> T doWith(Class<T> clazz, Map<String, Object> scope) {
        try {
            attributes.putAll(scope);
            T object = (T) script.eval();
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
