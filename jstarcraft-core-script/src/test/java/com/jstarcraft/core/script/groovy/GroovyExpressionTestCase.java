package com.jstarcraft.core.script.groovy;

import com.jstarcraft.core.script.ScriptContext;
import com.jstarcraft.core.script.ScriptExpression;
import com.jstarcraft.core.script.ScriptExpressionTestCase;
import com.jstarcraft.core.script.ScriptScope;
import com.jstarcraft.core.script.groovy.GroovyExpression;

public class GroovyExpressionTestCase extends ScriptExpressionTestCase {

    private String method = "fibonacciMethod(number)";

    private String object = "def mock = new Mock(index, 'birdy', 'mickey' + index, size, Instant.now(), MockEnumeration.TERRAN); mock.toString(); mock";

    private String fibonacci = "def index; def fibonacci = []; fibonacci[0] = 0.0; fibonacci[1] = 1.0; for(index = 2; index <= size; index++) { fibonacci.add(fibonacci[index - 2] + fibonacci[index - 1]); }; fibonacci[size]";
    
    private String load = "loader.loadClass(\"com.jstarcraft.core.script.MockObject\")";

    @Override
    protected ScriptExpression getMethodExpression(ScriptContext context, ScriptScope scope) {
        GroovyExpression expression = new GroovyExpression(context, scope, method);
        return expression;
    }

    @Override
    protected ScriptExpression getObjectExpression(ScriptContext context, ScriptScope scope) {
        GroovyExpression expression = new GroovyExpression(context, scope, object);
        return expression;
    }

    @Override
    protected ScriptExpression getFibonacciExpression(ScriptContext context, ScriptScope scope) {
        GroovyExpression expression = new GroovyExpression(context, scope, fibonacci);
        return expression;
    }
    
    @Override
    protected ScriptExpression getLoadExpression(ScriptContext context, ScriptScope scope) {
        GroovyExpression expression = new GroovyExpression(context, scope, load);
        return expression;
    }

}
