package com.jstarcraft.core.script.lua;

import com.jstarcraft.core.script.ScriptContext;
import com.jstarcraft.core.script.ScriptExpression;
import com.jstarcraft.core.script.ScriptExpressionTestCase;

public class LuaExpressionTestCase extends ScriptExpressionTestCase {

    private String method = "local value = fibonacciMethod(ScriptExpressionTestCase, number); return value";

    private String object = "local mock = Mock.new(index, 'birdy', 'mickey'..index, size, Instant:now(), MockEnumeration.TERRAN); mock:toString(); return mock";

    private String fibonacci = "local index; local fibonacci = {}; fibonacci[0] = 0.0; fibonacci[1] = 1.0; for index = 2, size, 1 do fibonacci[index] = fibonacci[index - 2] + fibonacci[index - 1] end; return fibonacci[size]";

    private String load = "return loader:loadClass(\"com.jstarcraft.core.script.MockObject\")";

    @Override
    protected ScriptExpression getMethodExpression(ScriptContext context) {
        LuaExpression expression = new LuaExpression(context, method);
        return expression;
    }

    @Override
    protected ScriptExpression getObjectExpression(ScriptContext context) {
        LuaExpression expression = new LuaExpression(context, object);
        return expression;
    }

    @Override
    protected ScriptExpression getFibonacciExpression(ScriptContext context) {
        LuaExpression expression = new LuaExpression(context, fibonacci);
        return expression;
    }

    @Override
    protected ScriptExpression getLoadExpression(ScriptContext context) {
        LuaExpression expression = new LuaExpression(context, load);
        return expression;
    }

}
