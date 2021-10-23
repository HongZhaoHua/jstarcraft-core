package com.jstarcraft.core.script.ruby;

import com.jstarcraft.core.script.ScriptContext;
import com.jstarcraft.core.script.ScriptExpression;
import com.jstarcraft.core.script.ScriptExpressionTestCase;

public class RubyExpressionTestCase extends ScriptExpressionTestCase {

    private String method = "fibonacciMethod($number);";

    private String object = "mock = Mock.new($index, 'birdy', \"mickey#{$index}\", $size, Instant.now(), MockEnumeration::TERRAN); mock.toString(); mock;";

    private String fibonacci = "fibonacci = Array.new($size); fibonacci[0] = 0.0; fibonacci[1] = 1.0; for index in 2..$size\r\nfibonacci[index] = fibonacci[index - 2] + fibonacci[index - 1]; end; fibonacci[$size];";

    private String load = "$loader.loadClass('com.jstarcraft.core.script.MockObject')";
    
    @Override
    protected ScriptExpression getMethodExpression(ScriptContext context) {
        RubyExpression expression = new RubyExpression(context, method);
        return expression;
    }

    @Override
    protected ScriptExpression getObjectExpression(ScriptContext context) {
        RubyExpression expression = new RubyExpression(context, object);
        return expression;
    }

    @Override
    protected ScriptExpression getFibonacciExpression(ScriptContext context) {
        RubyExpression expression = new RubyExpression(context, fibonacci);
        return expression;
    }
    
    @Override
    protected ScriptExpression getLoadExpression(ScriptContext context) {
        RubyExpression expression = new RubyExpression(context, load);
        return expression;
    }

}
