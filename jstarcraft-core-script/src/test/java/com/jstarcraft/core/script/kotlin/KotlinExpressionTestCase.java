package com.jstarcraft.core.script.kotlin;

import com.jstarcraft.core.script.ScriptContext;
import com.jstarcraft.core.script.ScriptExpression;
import com.jstarcraft.core.script.ScriptExpressionTestCase;

public class KotlinExpressionTestCase extends ScriptExpressionTestCase {
    
    private String method = "fibonacciMethod(bindings[\"number\"] as Int)";

    private String object = "var index = bindings[\"index\"] as Int; var size = bindings[\"size\"] as Int; val mock = Mock(index, \"birdy\", \"mickey\" + index, size, Instant.now(), MockEnumeration.TERRAN); mock.toString(); mock";

    private String fibonacci = "var index = 2; var size = bindings[\"size\"] as Int; val fibonacci = DoubleArray(size + 1); fibonacci[0] = 0.0; fibonacci[1] = 1.0; while(index <= size) { fibonacci[index] = fibonacci[index - 2] + fibonacci[index - 1]; index = index + 1; }; fibonacci[size]";
    
    private String load = "var loader = bindings[\"loader\"] as ClassLoader; loader.loadClass(\"com.jstarcraft.core.script.MockObject\")";

    @Override
    protected ScriptExpression getMethodExpression(ScriptContext context) {
        KotlinExpression expression = new KotlinExpression(context, method);
        return expression;
    }

    @Override
    protected ScriptExpression getObjectExpression(ScriptContext context) {
        KotlinExpression expression = new KotlinExpression(context, object);
        return expression;
    }

    @Override
    protected ScriptExpression getFibonacciExpression(ScriptContext context) {
        KotlinExpression expression = new KotlinExpression(context, fibonacci);
        return expression;
    }
    
    @Override
    protected ScriptExpression getLoadExpression(ScriptContext context) {
        KotlinExpression expression = new KotlinExpression(context, load);
        return expression;
    }

}
