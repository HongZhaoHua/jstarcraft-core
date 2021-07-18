package com.jstarcraft.core.script.mvel;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Map.Entry;

import org.mvel2.MVEL;
import org.mvel2.ParserContext;

import com.jstarcraft.core.script.ScriptContext;
import com.jstarcraft.core.script.ScriptFunction;
import com.jstarcraft.core.script.ScriptScope;
import com.jstarcraft.core.utility.StringUtility;

/**
 * MVEL函数
 * 
 * @author Birdy
 *
 */
public class MvelFunction implements ScriptFunction {

    private class MvelHolder {

        private ScriptScope scope;

        private MvelHolder(ScriptScope scope) {
            this.scope = scope.copyScope();
        }

    }

    private ThreadLocal<MvelHolder> threadHolder = new ThreadLocal<MvelHolder>() {

        @Override
        protected MvelHolder initialValue() {
            MvelHolder holder = new MvelHolder(scope);
            return holder;
        }

    };

    private ScriptScope scope;

    private String function;

    private String name;

    private Class<?>[] classes;

    private Serializable script;

    public MvelFunction(ScriptContext context, String function, String name, Class<?>... classes) {
        this.scope = new ScriptScope();
        StringBuilder buffer = new StringBuilder(function);
        buffer.append(StringUtility.format(" {}(", name));
        for (int index = 0, size = classes.length; index < size; index++) {
            buffer.append(StringUtility.format(index == 0 ? "argument{}" : ", argument{}", index));
        }
        buffer.append(")");
        this.function = buffer.toString();
        this.name = name;
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
        MvelHolder holder = threadHolder.get();
        for (int index = 0, size = classes.length; index < size; index++) {
            holder.scope.createAttribute(StringUtility.format("argument{}", index), arguments[index]);
        }
        T object = (T) MVEL.executeExpression(script, threadHolder.get().scope.getAttributes(), clazz);
        holder.scope.deleteAttributes();
        return object;
    }

}
