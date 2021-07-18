package com.jstarcraft.core.script.python;

import org.junit.BeforeClass;

import com.jstarcraft.core.script.ScriptContext;
import com.jstarcraft.core.script.ScriptFunction;
import com.jstarcraft.core.script.ScriptFunctionTestCase;
import com.jstarcraft.core.script.python.PythonFunction;
import com.jstarcraft.core.utility.StringUtility;

public class PythonFunctionTestCase extends ScriptFunctionTestCase {

    private String method = "def method(number):\r\n\treturn fibonacciMethod(number)";

    private String object = "def method(index, size):\r\n\tmock = Mock(index, 'birdy', 'mickey' + bytes(index), size, Instant.now(), MockEnumeration.TERRAN)\r\n\tmock.toString()\r\n\treturn mock";

    private String fibonacci = "def method(size):\r\n\tfibonacci = [0.0] * (size + 1)\r\n\tfibonacci[0] = 0.0\r\n\tfibonacci[1] = 1.0\r\n\tfor index in range(2, size + 1):\r\n\t\tfibonacci[index] = fibonacci[index - 2] + fibonacci[index - 1]\r\n\treturn fibonacci[size]";

    private String load = "def method(loader):\r\n\treturn loader.loadClass('com.jstarcraft.core.script.MockObject')";

    @BeforeClass
    public static void setProperty() {
        System.setProperty("python.console.encoding", StringUtility.CHARSET.name());
    }

    @Override
    protected ScriptFunction getMethodFunction(ScriptContext context) {
        PythonFunction function = new PythonFunction(context, method, "method", Integer.class);
        return function;
    }

    @Override
    protected ScriptFunction getObjectFunction(ScriptContext context) {
        PythonFunction function = new PythonFunction(context, object, "method", Integer.class, Integer.class);
        return function;
    }

    @Override
    protected ScriptFunction getFibonacciFunction(ScriptContext context) {
        PythonFunction function = new PythonFunction(context, fibonacci, "method", Integer.class);
        return function;
    }

    @Override
    protected ScriptFunction getLoadFunction(ScriptContext context) {
        PythonFunction function = new PythonFunction(context, load, "method", ClassLoader.class);
        return function;
    }

}
