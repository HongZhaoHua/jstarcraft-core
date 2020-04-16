package com.jstarcraft.core.script;

public class RubyExpressionTestCase extends ScriptExpressionTestCase {

    private String method = "fibonacciMethod($number);";

    private String object = "mock = Mock.new($index, 'birdy', \"mickey#{$index}\", $size, Instant.now(), MockEnumeration::TERRAN); mock.toString(); mock;";

    private String fibonacci = "fibonacci = Array.new($size); fibonacci[0] = 0.0; fibonacci[1] = 1.0; for index in 2..$size\r\nfibonacci[index] = fibonacci[index - 2] + fibonacci[index - 1]; end; fibonacci[$size];";

    @Override
    protected ScriptExpression getMethodExpression(ScriptContext context, ScriptScope scope) {
        RubyExpression expression = new RubyExpression(context, scope, method);
        return expression;
    }

    @Override
    protected ScriptExpression getObjectExpression(ScriptContext context, ScriptScope scope) {
        RubyExpression expression = new RubyExpression(context, scope, object);
        return expression;
    }

    @Override
    protected ScriptExpression getFibonacciExpression(ScriptContext context, ScriptScope scope) {
        RubyExpression expression = new RubyExpression(context, scope, fibonacci);
        return expression;
    }

}
