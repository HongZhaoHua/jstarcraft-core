package com.jstarcraft.core.script.php;

import java.lang.reflect.Method;
import java.util.Map.Entry;

import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;

import com.jstarcraft.core.script.ScriptContext;
import com.jstarcraft.core.script.ScriptExpression;
import com.jstarcraft.core.script.ScriptScope;
import com.jstarcraft.core.script.exception.ScriptExpressionException;
import com.jstarcraft.core.utility.StringUtility;

/**
 * PHP表达式
 * 
 * @author Birdy
 *
 */
public class PhpExpression implements ScriptExpression {

    private final static String ENGINE_NAME = "php";
    
    private final static ScriptEngineManager factory = new ScriptEngineManager();

    private class PhpHolder {

        private ScriptScope scope;

        private javax.script.ScriptContext attributes;

        private PhpHolder(ScriptScope scope, ScriptEngine engine) {
            this.scope = scope.copyScope();
            javax.script.ScriptContext context = engine.getContext();
            this.attributes = new SimpleScriptContext();
            this.attributes.setBindings(context.getBindings(javax.script.ScriptContext.GLOBAL_SCOPE), javax.script.ScriptContext.GLOBAL_SCOPE);
            this.attributes.setWriter(context.getWriter());
            this.attributes.setReader(context.getReader());
            this.attributes.setErrorWriter(context.getErrorWriter());
        }

    }

    private ThreadLocal<PhpHolder> threadHolder = new ThreadLocal<PhpHolder>() {

        @Override
        protected PhpHolder initialValue() {
            PhpHolder holder = new PhpHolder(scope, engine);
            return holder;
        }

    };

    private ScriptScope scope;

    private String expression;

    private ScriptEngine engine;

    private CompiledScript script;

    public PhpExpression(ScriptContext context, ScriptScope scope, String expression) {
        StringBuilder buffer = new StringBuilder();
        for (Entry<String, Class<?>> keyValue : context.getClasses().entrySet()) {
            buffer.append(StringUtility.format("import {} as {}; ", keyValue.getValue().getName(), keyValue.getKey()));
        }
        for (Entry<String, Method> keyValue : context.getMethods().entrySet()) {
            buffer.append(StringUtility.format("import {}; ", keyValue.getValue().getDeclaringClass().getName()));
            buffer.append(StringUtility.format("def {} = {}.&{}; ", keyValue.getKey(), keyValue.getValue().getDeclaringClass().getName(), keyValue.getValue().getName()));
        }
        buffer.append(expression);
        this.scope = scope.copyScope();
        this.expression = buffer.toString();
        try {
            this.engine = factory.getEngineByName(ENGINE_NAME);
            Compilable compilable = (Compilable) engine;
            this.script = compilable.compile(this.expression);
        } catch (ScriptException exception) {
            throw new ScriptExpressionException(exception);
        }
    }

    @Override
    public ScriptScope getScope() {
        return threadHolder.get().scope;
    }

    @Override
    public <T> T doWith(Class<T> clazz) {
        try {
            PhpHolder holder = threadHolder.get();
            holder.attributes.getBindings(javax.script.ScriptContext.ENGINE_SCOPE).putAll(holder.scope.getAttributes());
            T object = (T) script.eval(holder.attributes);
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
