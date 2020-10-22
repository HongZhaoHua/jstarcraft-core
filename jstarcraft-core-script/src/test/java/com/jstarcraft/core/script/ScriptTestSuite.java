package com.jstarcraft.core.script;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * 脚本集成测试
 * 
 * <pre>
 * Java 11执行Lua单元测试会抛<b>Unable to make {member} accessible: module {A} does not '{operation} {package}' to {B}</b>异常
 * 是由于Java 9模块化导致
 * 需要使用JVM参数:--add-exports java.base/jdk.internal.loader=ALL-UNNAMED
 * </pre>
 * 
 * @throws Exception
 */
@RunWith(Suite.class)
@SuiteClasses({
        // 上下文测试
        ScriptContextTestCase.class,
        // 表达式测试
        BeanShellExpressionTestCase.class,

        GroovyExpressionTestCase.class,

        KotlinExpressionTestCase.class,

        JsExpressionTestCase.class,

        LuaExpressionTestCase.class,

        MvelExpressionTestCase.class,

        PythonExpressionTestCase.class,

        RubyExpressionTestCase.class,
        // 函数测试
        BeanShellFunctionTestCase.class,

        GroovyFunctionTestCase.class,

        KotlinFunctionTestCase.class,

        JsFunctionTestCase.class,

        LuaFunctionTestCase.class,

        MvelFunctionTestCase.class,

        PythonFunctionTestCase.class,

        RubyFunctionTestCase.class, })
public class ScriptTestSuite {

}
