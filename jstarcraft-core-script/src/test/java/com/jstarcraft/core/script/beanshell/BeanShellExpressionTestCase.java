package com.jstarcraft.core.script.beanshell;

import com.jstarcraft.core.script.ScriptContext;
import com.jstarcraft.core.script.ScriptExpression;
import com.jstarcraft.core.script.ScriptExpressionTestCase;
import com.jstarcraft.core.script.ScriptScope;
import com.jstarcraft.core.script.beanshell.BeanShellExpression;

public class BeanShellExpressionTestCase extends ScriptExpressionTestCase {

    private String method = "fibonacciMethod(number)";

    private String object = "Mock mock = new Mock(index, \"birdy\", \"mickey\" + index, size, Instant.now(), MockEnumeration.TERRAN); mock.toString(); mock";

    private String fibonacci = "int index; double[] fibonacci = new double[(int)(size + 1)]; fibonacci[0] = 0.0; fibonacci[1] = 1.0; for(index = 2; index <= size; index++) { fibonacci[index] = fibonacci[index - 2] + fibonacci[index - 1]; } fibonacci[size]";

    private String load = "loader.loadClass(\"com.jstarcraft.core.script.MockObject\")";
    
    @Override
    protected ScriptExpression getMethodExpression(ScriptContext context, ScriptScope scope) {
        BeanShellExpression expression = new BeanShellExpression(context, scope, method);
        return expression;
    }

    @Override
    protected ScriptExpression getObjectExpression(ScriptContext context, ScriptScope scope) {
        BeanShellExpression expression = new BeanShellExpression(context, scope, object);
        return expression;
    }

    @Override
    protected ScriptExpression getFibonacciExpression(ScriptContext context, ScriptScope scope) {
        BeanShellExpression expression = new BeanShellExpression(context, scope, fibonacci);
        return expression;
    }

    @Override
    protected ScriptExpression getLoadExpression(ScriptContext context, ScriptScope scope) {
        BeanShellExpression expression = new BeanShellExpression(context, scope, load);
        return expression;
    }

}
