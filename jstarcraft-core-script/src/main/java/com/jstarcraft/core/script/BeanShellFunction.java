package com.jstarcraft.core.script;

import java.lang.reflect.Method;
import java.util.Map.Entry;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import com.jstarcraft.core.script.exception.ScriptExpressionException;
import com.jstarcraft.core.utility.StringUtility;

/**
 * BeanShell函数
 * 
 * @author Birdy
 *
 */
public class BeanShellFunction implements ScriptFunction {

    private final static String ENGINE_NAME = "beanshell";

    private String function;

    private Invocable engine;

    private String name;

    private Class<?>[] classes;

    public BeanShellFunction(ScriptContext context, String function, String name, Class<?>... classes) {
        StringBuilder buffer = new StringBuilder();
        for (Entry<String, Class<?>> keyValue : context.getClasses().entrySet()) {
            buffer.append(StringUtility.format("import {}; ", keyValue.getValue().getName()));
            // 替代函数中对应的类名(注意兼容开头与结尾的情况)
            function = function.replaceAll("(^|[^\\w]+)" + keyValue.getKey() + "([^\\w]+|$)", "$1" + keyValue.getValue().getSimpleName() + "$2");
        }
        for (Entry<String, Method> keyValue : context.getMethods().entrySet()) {
            buffer.append(StringUtility.format("import {}; ", keyValue.getValue().getDeclaringClass().getName()));
            // 替代函数中对应的方法名(注意兼容开头与结尾的情况)
            function = function.replaceAll("(^|[^\\w]+)" + keyValue.getKey() + "([^\\w]+|$)", "$1" + keyValue.getValue().getDeclaringClass().getSimpleName() + "." + keyValue.getValue().getName() + "$2");
        }
        buffer.append(function);
        this.function = buffer.toString();
        try {
            ScriptEngineManager factory = new ScriptEngineManager();
            ScriptEngine engine = factory.getEngineByName(ENGINE_NAME);
            engine.eval(this.function);
            this.engine = (Invocable) engine;
            this.name = name;
            this.classes = classes;
        } catch (ScriptException exception) {
            throw new ScriptExpressionException(exception);
        }
    }

    @Override
    public synchronized <T> T doWith(Class<T> clazz, Object... arguments) {
        // BshScriptEngine似乎为非线程安全
        try {
            T object = (T) engine.invokeFunction(name, arguments);
            return object;
        } catch (Exception exception) {
            throw new ScriptExpressionException(exception);
        }
    }

}
