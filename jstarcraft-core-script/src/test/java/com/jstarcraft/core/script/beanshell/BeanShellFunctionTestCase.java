package com.jstarcraft.core.script.beanshell;

import com.jstarcraft.core.script.ScriptContext;
import com.jstarcraft.core.script.ScriptFunction;
import com.jstarcraft.core.script.ScriptFunctionTestCase;

public class BeanShellFunctionTestCase extends ScriptFunctionTestCase {

    private String method = "public Double method(int number) { return fibonacciMethod(number); }";

    private String object = "public Mock method(index, size) { Mock mock = new Mock(index, \"birdy\", \"mickey\" + index, size, Instant.now(), MockEnumeration.TERRAN); mock.toString(); return mock; }";

    private String fibonacci = "public Double method(int size) { int index; double[] fibonacci = new double[size + 1]; fibonacci[0] = 0.0; fibonacci[1] = 1.0; for(index = 2; index <= size; index++) { fibonacci[index] = fibonacci[index - 2] + fibonacci[index - 1]; } return fibonacci[size]; }";

    private String load = "public Class method(loader) { return loader.loadClass(\"com.jstarcraft.core.script.MockObject\"); }";

    @Override
    protected ScriptFunction getMethodFunction(ScriptContext context) {
        BeanShellFunction function = new BeanShellFunction(context, method, "method", Integer.class);
        return function;
    }

    @Override
    protected ScriptFunction getObjectFunction(ScriptContext context) {
        BeanShellFunction function = new BeanShellFunction(context, object, "method", Integer.class, Integer.class);
        return function;
    }

    @Override
    protected ScriptFunction getFibonacciFunction(ScriptContext context) {
        BeanShellFunction function = new BeanShellFunction(context, fibonacci, "method", Integer.class);
        return function;
    }

    @Override
    protected ScriptFunction getLoadFunction(ScriptContext context) {
        BeanShellFunction function = new BeanShellFunction(context, load, "method", ClassLoader.class);
        return function;
    }

}
