package com.jstarcraft.core.script.beanshell;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Map.Entry;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;

import com.jstarcraft.core.script.ScriptContext;
import com.jstarcraft.core.script.ScriptExpression;
import com.jstarcraft.core.script.exception.ScriptExpressionException;
import com.jstarcraft.core.utility.StringUtility;

/**
 * BeanShell表达式
 * 
 * @author Birdy
 *
 */
public class BeanShellExpression implements ScriptExpression {

    private final static String ENGINE_NAME = "beanshell";

    private final static ScriptEngineManager factory = new ScriptEngineManager();

    private String expression;

    private ScriptEngine engine;

    private javax.script.ScriptContext attributes;

    public BeanShellExpression(ScriptContext context, String expression) {
        StringBuilder buffer = new StringBuilder();
        for (Entry<String, Class<?>> keyValue : context.getClasses().entrySet()) {
            buffer.append(StringUtility.format("import {}; ", keyValue.getValue().getName()));
            // 替代表达式中对应的类名(注意兼容开头与结尾的情况)
            expression = expression.replaceAll("(^|[^\\w]+)" + keyValue.getKey() + "([^\\w]+|$)", "$1" + keyValue.getValue().getSimpleName() + "$2");
        }
        for (Entry<String, Method> keyValue : context.getMethods().entrySet()) {
            buffer.append(StringUtility.format("import {}; ", keyValue.getValue().getDeclaringClass().getName()));
            // 替代表达式中对应的方法名(注意兼容开头与结尾的情况)
            expression = expression.replaceAll("(^|[^\\w]+)" + keyValue.getKey() + "([^\\w]+|$)", "$1" + keyValue.getValue().getDeclaringClass().getSimpleName() + "." + keyValue.getValue().getName() + "$2");
        }
        buffer.append(expression);
        this.expression = buffer.toString();
        this.engine = factory.getEngineByName(ENGINE_NAME);
        // 注意:BshScriptEngine在2.0b6版本没有支持Compilable接口.
        this.attributes = new SimpleScriptContext();
        this.attributes.setBindings(engine.getContext().getBindings(javax.script.ScriptContext.GLOBAL_SCOPE), javax.script.ScriptContext.GLOBAL_SCOPE);
        this.attributes.setWriter(engine.getContext().getWriter());
        this.attributes.setReader(engine.getContext().getReader());
        this.attributes.setErrorWriter(engine.getContext().getErrorWriter());
    }

    @Override
    public synchronized <T> T doWith(Class<T> clazz, Map<String, Object> scope) {
        // BshScriptEngine似乎为非线程安全
        try {
            attributes.getBindings(javax.script.ScriptContext.ENGINE_SCOPE).putAll(scope);
            T object = (T) engine.eval(expression, attributes);
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
