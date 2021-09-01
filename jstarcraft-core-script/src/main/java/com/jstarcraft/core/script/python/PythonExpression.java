package com.jstarcraft.core.script.python;

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
 * Python表达式
 * 
 * @author Birdy
 *
 */
public class PythonExpression implements ScriptExpression {

    private final static String ENGINE_NAME = "jython";

    private class PythonHolder {

        private ScriptScope scope;

        private Bindings attributes;

        private CompiledScript script;

        private PythonHolder(ScriptScope scope, String expression) {
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

    private ThreadLocal<PythonHolder> threadHolder = new ThreadLocal<PythonHolder>() {

        @Override
        protected PythonHolder initialValue() {
            PythonHolder holder = new PythonHolder(scope, expression);
            return holder;
        }

    };

    private ScriptScope scope;

    private String expression;

    public PythonExpression(ScriptContext context, ScriptScope scope, String expression) {
        String separator = System.getProperty("line.separator");
        StringBuilder buffer = new StringBuilder();
        for (Entry<String, Class<?>> keyValue : context.getClasses().entrySet()) {
            buffer.append(StringUtility.format("import {}", keyValue.getValue().getName()));
            buffer.append(separator);
            buffer.append(StringUtility.format("{} = {}", keyValue.getKey(), keyValue.getValue().getName()));
            buffer.append(separator);
        }
        for (Entry<String, Method> keyValue : context.getMethods().entrySet()) {
            buffer.append(StringUtility.format("import {}", keyValue.getValue().getDeclaringClass().getName()));
            buffer.append(separator);
            buffer.append(StringUtility.format("{} = {}.{}", keyValue.getKey(), keyValue.getValue().getDeclaringClass().getName(), keyValue.getValue().getName()));
            buffer.append(separator);
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
            PythonHolder holder = threadHolder.get();
            holder.attributes.putAll(holder.scope.getAttributes());
            CompiledScript script = holder.script;
            script.eval();
            T object = (T) script.getEngine().getContext().getAttribute("_data");
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
