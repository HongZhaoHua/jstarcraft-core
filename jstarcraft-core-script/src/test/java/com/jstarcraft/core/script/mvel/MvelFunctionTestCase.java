package com.jstarcraft.core.script.mvel;

import com.jstarcraft.core.script.ScriptContext;
import com.jstarcraft.core.script.ScriptFunction;
import com.jstarcraft.core.script.ScriptFunctionTestCase;
import com.jstarcraft.core.script.mvel.MvelFunction;

public class MvelFunctionTestCase extends ScriptFunctionTestCase {

    private String method = "def method(number) { fibonacciMethod(number) }";

    private String object = "def method(index, size) { mock = new Mock(index, 'birdy', 'mickey' + index, size, Instant.now(), MockEnumeration.TERRAN); mock.toString(); mock }";

    private String fibonacci = "def method(size) { fibonacci = new Double[size + 1]; fibonacci[0] = 0.0; fibonacci[1] = 1.0; for(index = 2; index <= size; index++) { fibonacci[index] = fibonacci[index - 2] + fibonacci[index - 1]; } fibonacci[size] }";

    private String load = "def method(loader) { loader.loadClass('com.jstarcraft.core.script.MockObject') }";

    @Override
    protected ScriptFunction getMethodFunction(ScriptContext context) {
        MvelFunction function = new MvelFunction(context, method, "method", Integer.class);
        return function;
    }

    @Override
    protected ScriptFunction getObjectFunction(ScriptContext context) {
        MvelFunction function = new MvelFunction(context, object, "method", Integer.class, Integer.class);
        return function;
    }

    @Override
    protected ScriptFunction getFibonacciFunction(ScriptContext context) {
        MvelFunction function = new MvelFunction(context, fibonacci, "method", Integer.class);
        return function;
    }

    @Override
    protected ScriptFunction getLoadFunction(ScriptContext context) {
        MvelFunction function = new MvelFunction(context, load, "method", ClassLoader.class);
        return function;
    }

}
