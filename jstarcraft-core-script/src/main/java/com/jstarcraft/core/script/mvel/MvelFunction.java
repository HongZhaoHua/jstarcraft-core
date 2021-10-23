package com.jstarcraft.core.script.mvel;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.mvel2.MVEL;
import org.mvel2.ParserContext;

import com.jstarcraft.core.script.ScriptContext;
import com.jstarcraft.core.script.ScriptFunction;
import com.jstarcraft.core.utility.StringUtility;

/**
 * MVEL函数
 * 
 * @author Birdy
 *
 */
public class MvelFunction implements ScriptFunction {

    private String function;

    private Class<?>[] classes;

    private Serializable script;

    public MvelFunction(ScriptContext context, String function, String name, Class<?>... classes) {
        StringBuilder buffer = new StringBuilder(function);
        buffer.append(StringUtility.format(" {}(", name));
        for (int index = 0, size = classes.length; index < size; index++) {
            buffer.append(StringUtility.format(index == 0 ? "argument{}" : ", argument{}", index));
        }
        buffer.append(")");
        this.function = buffer.toString();
        this.classes = classes;
        ParserContext parserContext = new ParserContext();
        for (Entry<String, Class<?>> keyValue : context.getClasses().entrySet()) {
            parserContext.addImport(keyValue.getKey(), keyValue.getValue());
        }
        for (Entry<String, Method> keyValue : context.getMethods().entrySet()) {
            parserContext.addImport(keyValue.getKey(), keyValue.getValue());
        }
        this.script = MVEL.compileExpression(this.function, parserContext);
    }

    @Override
    public <T> T doWith(Class<T> clazz, Object... arguments) {
        Map<String, Object> scope = new HashMap<>();
        for (int index = 0, size = classes.length; index < size; index++) {
            scope.put(StringUtility.format("argument{}", index), arguments[index]);
        }
        T object = (T) MVEL.executeExpression(script, scope, clazz);
        return object;
    }

    @Override
    public String toString() {
        return function;
    }

}
