package com.jstarcraft.core.script.kotlin;

import com.jstarcraft.core.script.ScriptContext;
import com.jstarcraft.core.script.ScriptFunction;
import com.jstarcraft.core.script.ScriptFunctionTestCase;
import com.jstarcraft.core.script.kotlin.KotlinFunction;

public class KotlinFunctionTestCase extends ScriptFunctionTestCase {

    private String method = "fun method(number: Int): Double { return fibonacciMethod(number) }";

    private String object = "fun method(index: Int, size: Int): Mock { var mock = Mock(index, \"birdy\", \"mickey\" + index, size, Instant.now(), MockEnumeration.TERRAN); mock.toString(); return mock }";

    private String fibonacci = "fun method(size: Int): Double { var index = 2; var fibonacci = DoubleArray(size + 1); fibonacci[0] = 0.0; fibonacci[1] = 1.0; while(index <= size) { fibonacci[index] = fibonacci[index - 2] + fibonacci[index - 1]; index = index + 1; }; return fibonacci[size] }";

    private String load = "fun method(loader: ClassLoader): Class<*> { return loader.loadClass(\"com.jstarcraft.core.script.MockObject\") }";

    @Override
    protected ScriptFunction getMethodFunction(ScriptContext context) {
        KotlinFunction function = new KotlinFunction(context, method, "method", Integer.class);
        return function;
    }

    @Override
    protected ScriptFunction getObjectFunction(ScriptContext context) {
        KotlinFunction function = new KotlinFunction(context, object, "method", Integer.class, Integer.class);
        return function;
    }

    @Override
    protected ScriptFunction getFibonacciFunction(ScriptContext context) {
        KotlinFunction function = new KotlinFunction(context, fibonacci, "method", Integer.class);
        return function;
    }

    @Override
    protected ScriptFunction getLoadFunction(ScriptContext context) {
        KotlinFunction function = new KotlinFunction(context, load, "method", ClassLoader.class);
        return function;
    }

}
