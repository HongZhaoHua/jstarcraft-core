package com.jstarcraft.core.script;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
        // 上下文测试
        ScriptContextTestCase.class,
        // 表达式测试
        GroovyExpressionTestCase.class,

        JsExpressionTestCase.class,

        LuaExpressionTestCase.class,

        MvelExpressionTestCase.class,

        PythonExpressionTestCase.class,
        
        RubyExpressionTestCase.class,
        // 函数测试
        GroovyFunctionTestCase.class,

        JsFunctionTestCase.class,

        LuaFunctionTestCase.class,

        MvelFunctionTestCase.class,

        PythonFunctionTestCase.class,
        
        RubyFunctionTestCase.class,})
public class ScriptTestSuite {

}
