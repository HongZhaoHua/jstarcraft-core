package com.jstarcraft.core.script.mvel;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Map.Entry;

import org.mvel2.MVEL;
import org.mvel2.ParserContext;

import com.jstarcraft.core.script.ScriptContext;
import com.jstarcraft.core.script.ScriptExpression;
import com.jstarcraft.core.script.ScriptScope;

/**
 * MVEL表达式
 * 
 * @author Birdy
 *
 */
public class MvelExpression implements ScriptExpression {

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

    private String expression;

    private Serializable script;

    public MvelExpression(ScriptContext context, ScriptScope scope, String expression) {
        this.scope = scope.copyScope();
        this.expression = expression;
        ParserContext parserContext = new ParserContext();
        for (Entry<String, Class<?>> keyValue : context.getClasses().entrySet()) {
            parserContext.addImport(keyValue.getKey(), keyValue.getValue());
        }
        for (Entry<String, Method> keyValue : context.getMethods().entrySet()) {
            parserContext.addImport(keyValue.getKey(), keyValue.getValue());
        }
        this.script = MVEL.compileExpression(expression, parserContext);
    }

    @Override
    public ScriptScope getScope() {
        return threadHolder.get().scope;
    }

    @Override
    public <T> T doWith(Class<T> clazz) {
        return MVEL.executeExpression(script, threadHolder.get().scope.getAttributes(), clazz);
    }

}
