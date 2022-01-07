package com.jstarcraft.core.script.mvel;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Map.Entry;

import org.mvel2.MVEL;
import org.mvel2.ParserContext;

import com.jstarcraft.core.script.ScriptContext;
import com.jstarcraft.core.script.ScriptExpression;

/**
 * MVEL表达式
 * 
 * @author Birdy
 *
 */
public class MvelExpression implements ScriptExpression {

    private String expression;

    private Serializable script;

    public MvelExpression(ScriptContext context, String expression) {
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
    public <T> T doWith(Class<T> clazz, Map<String, Object> scope) {
        return MVEL.executeExpression(script, scope, clazz);
    }

    @Override
    public String toString() {
        return expression;
    }

}
