package com.jstarcraft.core.script;

public class LuaExpressionTestCase extends ScriptExpressionTestCase {

	private String method = "local value = fibonacciMethod(ScriptExpressionTestCase, number); return value";

	private String object = "local mock = Mock.new(index, 'birdy', 'mickey'..index, size, Instant:now(), MockEnumeration.TERRAN); mock:toString(); return mock";

	private String fibonacci = "local index; local fibonacci = {}; fibonacci[0] = 0.0; fibonacci[1] = 1.0; for index = 2, size, 1 do fibonacci[index] = fibonacci[index-2] + fibonacci[index-1] end; return fibonacci[size]";

	@Override
	protected ScriptExpression getMethodExpression(ScriptContext context, ScriptScope scope) {
		LuaExpression expression = new LuaExpression(context, scope, method);
		return expression;
	}

	@Override
	protected ScriptExpression getObjectExpression(ScriptContext context, ScriptScope scope) {
		LuaExpression expression = new LuaExpression(context, scope, object);
		return expression;
	}

	@Override
	protected ScriptExpression getFibonacciExpression(ScriptContext context, ScriptScope scope) {
		LuaExpression expression = new LuaExpression(context, scope, fibonacci);
		return expression;
	}

}
