package com.jstarcraft.core.script;

public class RubyFunctionTestCase extends ScriptFunctionTestCase {

    private String method = "def method(number) fibonacciMethod(number) end;";

    private String object = "def method(index, size) mock = Mock.new(index, 'birdy', \"mickey#{index}\", size, Instant.now(), MockEnumeration::TERRAN); mock.toString(); mock; end;";

    private String fibonacci = "def method(size) fibonacci = Array.new(size); fibonacci[0] = 0.0; fibonacci[1] = 1.0; for index in 2..size\r\nfibonacci[index] = fibonacci[index - 2] + fibonacci[index - 1]; end; fibonacci[size]; end";

    @Override
    protected ScriptFunction getMethodFunction(ScriptContext context) {
        RubyFunction function = new RubyFunction(context, method, "method", Integer.class);
        return function;
    }

    @Override
    protected ScriptFunction getObjectFunction(ScriptContext context) {
        RubyFunction function = new RubyFunction(context, object, "method", Integer.class, Integer.class);
        return function;
    }

    @Override
    protected ScriptFunction getFibonacciFunction(ScriptContext context) {
        RubyFunction function = new RubyFunction(context, fibonacci, "method", Integer.class);
        return function;
    }

}
