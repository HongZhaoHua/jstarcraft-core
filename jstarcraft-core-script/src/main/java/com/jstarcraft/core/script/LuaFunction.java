package com.jstarcraft.core.script;

import java.lang.reflect.Method;
import java.util.Map.Entry;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import com.jstarcraft.core.script.exception.ScriptExpressionException;
import com.jstarcraft.core.utility.StringUtility;

/**
 * Lua函数
 * 
 * @author Birdy
 *
 */
public class LuaFunction implements ScriptFunction {

    private final static String ENGINE_NAME = "luaj";

    private class LuaHolder {

        private ScriptScope scope;

        private Bindings attributes;

        private CompiledScript script;

        private LuaHolder(ScriptScope scope, String expression) {
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

    private ThreadLocal<LuaHolder> threadHolder = new ThreadLocal<LuaHolder>() {

        @Override
        protected LuaHolder initialValue() {
            LuaHolder holder = new LuaHolder(scope, function);
            return holder;
        }

    };

    private ScriptScope scope;

    private String function;

    private String name;

    private Class<?>[] classes;

    public LuaFunction(ScriptContext context, String function, String name, Class<?>... classes) {
        StringBuilder buffer = new StringBuilder();
        for (Entry<String, Class<?>> keyValue : context.getClasses().entrySet()) {
            buffer.append(StringUtility.format("local {} = luajava.bindClass('{}'); ", keyValue.getKey(), keyValue.getValue().getName()));
        }
        for (Entry<String, Method> keyValue : context.getMethods().entrySet()) {
            buffer.append(StringUtility.format("local {} = luajava.bindClass('{}').{}; ", keyValue.getKey(), keyValue.getValue().getDeclaringClass().getName(), keyValue.getValue().getName()));
        }
        buffer.append(function);
        buffer.append(StringUtility.format("; return {}(", name));
        for (int index = 0, size = classes.length; index < size; index++) {
            buffer.append(StringUtility.format(index == 0 ? "argument{}" : ", argument{}", index));
        }
        buffer.append(")");
        this.scope = new ScriptScope();
        this.function = buffer.toString();
        this.name = name;
        this.classes = classes;
    }

    @Override
    public <T> T doWith(Class<T> clazz, Object... arguments) {
        try {
            LuaHolder holder = threadHolder.get();
            for (int index = 0, size = classes.length; index < size; index++) {
                holder.attributes.put(StringUtility.format("argument{}", index), arguments[index]);
            }
            holder.attributes.putAll(holder.scope.getAttributes());
            CompiledScript script = holder.script;
            T object = (T) script.eval();
            holder.scope.deleteAttributes();
            return object;
        } catch (ScriptException exception) {
            throw new ScriptExpressionException(exception);
        }
    }

}