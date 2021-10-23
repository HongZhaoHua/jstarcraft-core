package com.jstarcraft.core.script.python;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.python.core.PyJavaType;

import com.jstarcraft.core.script.MockObject;
import com.jstarcraft.core.script.ScriptContext;
import com.jstarcraft.core.script.ScriptExpression;
import com.jstarcraft.core.script.ScriptExpressionTestCase;
import com.jstarcraft.core.utility.StringUtility;

public class PythonExpressionTestCase extends ScriptExpressionTestCase {

    private String method = "_data = fibonacciMethod(number);";

    private String object = "mock = Mock(index, 'birdy', 'mickey' + bytes(index), size, Instant.now(), MockEnumeration.TERRAN); mock.toString(); _data = mock";

    private String fibonacci = "fibonacci = [0.0] * (size + 1)\r\nfibonacci[0] = 0.0\r\nfibonacci[1] = 1.0\r\nfor index in range(2, size + 1):\r\n\tfibonacci[index] = fibonacci[index - 2] + fibonacci[index - 1]\r\n_data = fibonacci[size]";

    private String load = "_data = loader.loadClass('com.jstarcraft.core.script.MockObject')";

    @BeforeClass
    public static void setProperty() {
        System.setProperty("python.console.encoding", StringUtility.CHARSET.name());
    }

    @Override
    protected ScriptExpression getMethodExpression(ScriptContext context) {
        PythonExpression expression = new PythonExpression(context, method);
        return expression;
    }

    @Override
    protected ScriptExpression getObjectExpression(ScriptContext context) {
        PythonExpression expression = new PythonExpression(context, object);
        return expression;
    }

    @Override
    protected ScriptExpression getFibonacciExpression(ScriptContext context) {
        PythonExpression expression = new PythonExpression(context, fibonacci);
        return expression;
    }

    @Override
    protected ScriptExpression getLoadExpression(ScriptContext context) {
        PythonExpression expression = new PythonExpression(context, load);
        return expression;
    }

    @Test
    public void testLoad() {
        ScriptContext context = new ScriptContext();
        ScriptExpression expression = getLoadExpression(context);
        Map<String, Object> scope = new HashMap<>();
        scope.put("loader", loader);
        Assert.assertEquals(MockObject.class, expression.doWith(PyJavaType.class, scope).getProxyType());
    }

}
