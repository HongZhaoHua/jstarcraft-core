package com.jstarcraft.core.script.js;

import java.lang.reflect.Method;
import java.util.Map.Entry;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import com.jstarcraft.core.script.ScriptContext;
import com.jstarcraft.core.script.ScriptExpression;
import com.jstarcraft.core.script.ScriptScope;
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

    private class JsHolder {

        private ScriptScope scope;

        private Bindings attributes;

        private CompiledScript script;

        private JsHolder(ScriptScope scope, String expression) {
            try {
                this.scope = scope.copyScope();
                ScriptEngineManager factory = new ScriptEngineManager();
                ScriptEngine engine = factory.getEngineByName(ENGINE_NAME);
                this.attributes = engine.getBindings(javax.script.ScriptContext.ENGINE_SCOPE);
                Compilable compilable = (Compilable) engine;
                this.script = compilable.compile(expression);
            } catch (ScriptException exception) {
                throw new ScriptExpressionException(exception);
            }
        }

    }

    private ThreadLocal<JsHolder> threadHolder = new ThreadLocal<JsHolder>() {

        @Override
        protected JsHolder initialValue() {
            JsHolder holder = new JsHolder(scope, expression);
            return holder;
        }

    };

    private ScriptScope scope;

    private String expression;

    public JsExpression(ScriptContext context, ScriptScope scope, String expression) {
        StringBuilder buffer = new StringBuilder();
        for (Entry<String, Class<?>> keyValue : context.getClasses().entrySet()) {
            buffer.append(StringUtility.format("var {} = Java.type('{}'); ", keyValue.getKey(), keyValue.getValue().getName()));
        }
        for (Entry<String, Method> keyValue : context.getMethods().entrySet()) {
            buffer.append(StringUtility.format("var {} = Java.type('{}').{}; ", keyValue.getKey(), keyValue.getValue().getDeclaringClass().getName(), keyValue.getValue().getName()));
        }
        buffer.append(expression);
        this.scope = scope.copyScope();
        this.expression = buffer.toString();
    }

    @Override
    public ScriptScope getScope() {
        return threadHolder.get().scope;
    }

    @Override
    public <T> T doWith(Class<T> clazz) {
        try {
            JsHolder holder = threadHolder.get();
            holder.attributes.putAll(holder.scope.getAttributes());
            CompiledScript script = holder.script;
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
