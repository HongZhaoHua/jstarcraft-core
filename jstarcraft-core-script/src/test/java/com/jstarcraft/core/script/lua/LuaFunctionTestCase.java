package com.jstarcraft.core.script.lua;

import com.jstarcraft.core.script.ScriptContext;
import com.jstarcraft.core.script.ScriptFunction;
import com.jstarcraft.core.script.ScriptFunctionTestCase;
import com.jstarcraft.core.script.lua.LuaFunction;

public class LuaFunctionTestCase extends ScriptFunctionTestCase {

    private String method = "function method(number) return fibonacciMethod(ScriptFunctionTestCase, number) end";

    private String object = "function method(index, size) local mock = Mock.new(index, 'birdy', 'mickey'..index, size, Instant:now(), MockEnumeration.TERRAN); mock:toString(); return mock end";

    private String fibonacci = "function method(size) local index; local fibonacci = {}; fibonacci[0] = 0.0; fibonacci[1] = 1.0; for index = 2, size, 1 do fibonacci[index] = fibonacci[index - 2] + fibonacci[index - 1] end; return fibonacci[size] end";

    private String load = "function method(loader) return loader:loadClass(\"com.jstarcraft.core.script.MockObject\") end";

    @Override
    protected ScriptFunction getMethodFunction(ScriptContext context) {
        LuaFunction function = new LuaFunction(context, method, "method", Integer.class);
        return function;
    }

    @Override
    protected ScriptFunction getObjectFunction(ScriptContext context) {
        LuaFunction function = new LuaFunction(context, object, "method", Integer.class, Integer.class);
        return function;
    }

    @Override
    protected ScriptFunction getFibonacciFunction(ScriptContext context) {
        LuaFunction function = new LuaFunction(context, fibonacci, "method", Integer.class);
        return function;
    }

    @Override
    protected ScriptFunction getLoadFunction(ScriptContext context) {
        LuaFunction function = new LuaFunction(context, load, "method", ClassLoader.class);
        return function;
    }

}
