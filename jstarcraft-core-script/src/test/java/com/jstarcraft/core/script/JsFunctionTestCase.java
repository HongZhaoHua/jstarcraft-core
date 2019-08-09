package com.jstarcraft.core.script;

public class JsFunctionTestCase extends ScriptFunctionTestCase {

    private String method = "function method(number) { return fibonacciMethod(number) }";

    private String object = "function method(index, size) { var mock = new Mock(index, 'birdy', 'mickey' + index, size, Instant.now(), MockEnumeration.TERRAN); mock.toString(); return mock }";

    private String fibonacci = "function method(size) { var index; var fibonacci = []; fibonacci[0] = 0.0; fibonacci[1] = 1.0; for(index = 2; index <= size; index++) { fibonacci[index] = fibonacci[index - 2] + fibonacci[index - 1]; } return fibonacci[size] }";

    @Override
    protected ScriptFunction getMethodFunction(ScriptContext context) {
        JsFunction function = new JsFunction(context, method, "method", Integer.class);
        return function;
    }

    @Override
    protected ScriptFunction getObjectFunction(ScriptContext context) {
        JsFunction function = new JsFunction(context, object, "method", Integer.class, Integer.class);
        return function;
    }

    @Override
    protected ScriptFunction getFibonacciFunction(ScriptContext context) {
        JsFunction function = new JsFunction(context, fibonacci, "method", Integer.class);
        return function;
    }

}
