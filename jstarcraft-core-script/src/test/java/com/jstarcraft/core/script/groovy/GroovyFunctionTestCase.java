package com.jstarcraft.core.script.groovy;

import com.jstarcraft.core.script.ScriptContext;
import com.jstarcraft.core.script.ScriptFunction;
import com.jstarcraft.core.script.ScriptFunctionTestCase;
import com.jstarcraft.core.script.groovy.GroovyFunction;

public class GroovyFunctionTestCase extends ScriptFunctionTestCase {

    private String method = "def method(number) { fibonacciMethod(number) }";

    private String object = "def method(index, size) { def mock = new Mock(index, 'birdy', 'mickey' + index, size, Instant.now(), MockEnumeration.TERRAN); mock.toString(); mock }";

    private String fibonacci = "def method(size) { def index; def fibonacci = []; fibonacci[0] = 0.0; fibonacci[1] = 1.0; for(index = 2; index <= size; index++) { fibonacci.add(fibonacci[index - 2] + fibonacci[index - 1]); }; fibonacci[size] }";

    private String load = "def method(loader) { loader.loadClass(\"com.jstarcraft.core.script.MockObject\") }";

    @Override
    protected ScriptFunction getMethodFunction(ScriptContext context) {
        GroovyFunction function = new GroovyFunction(context, method, "method", Integer.class);
        return function;
    }

    @Override
    protected ScriptFunction getObjectFunction(ScriptContext context) {
        GroovyFunction function = new GroovyFunction(context, object, "method", Integer.class, Integer.class);
        return function;
    }

    @Override
    protected ScriptFunction getFibonacciFunction(ScriptContext context) {
        GroovyFunction function = new GroovyFunction(context, fibonacci, "method", Integer.class);
        return function;
    }

    @Override
    protected ScriptFunction getLoadFunction(ScriptContext context) {
        GroovyFunction function = new GroovyFunction(context, load, "method", ClassLoader.class);
        return function;
    }

}
