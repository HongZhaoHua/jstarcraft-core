package com.jstarcraft.core.script.lua;

import java.lang.reflect.Method;
import java.util.Map.Entry;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import com.jstarcraft.core.script.ScriptContext;
import com.jstarcraft.core.script.ScriptFunction;
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

    private final static ScriptEngineManager factory = new ScriptEngineManager();

    private final static ScriptEngine engine = factory.getEngineByName(ENGINE_NAME);

    private final static Compilable compilable = (Compilable) engine;

    private String function;

    private String name;

    private Class<?>[] classes;

    private Bindings attributes;

    private CompiledScript script;

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
        this.function = buffer.toString();
        this.name = name;
        this.classes = classes;
        try {
            this.attributes = engine.getBindings(javax.script.ScriptContext.ENGINE_SCOPE);
            this.script = compilable.compile(this.function);
        } catch (ScriptException exception) {
            throw new ScriptExpressionException(exception);
        }
    }

    @Override
    public <T> T doWith(Class<T> clazz, Object... arguments) {
        try {
            synchronized (engine) {
                for (int index = 0, size = classes.length; index < size; index++) {
                    attributes.put(StringUtility.format("argument{}", index), arguments[index]);
                }
                T object = (T) script.eval();
                attributes.clear();
                return object;
            }
        } catch (ScriptException exception) {
            throw new ScriptExpressionException(exception);
        }
    }

    @Override
    public String toString() {
        return function;
    }

}