package com.jstarcraft.core.script;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.jstarcraft.core.script.beanshell.BeanShellExpressionTestCase;
import com.jstarcraft.core.script.beanshell.BeanShellFunctionTestCase;
import com.jstarcraft.core.script.groovy.GroovyExpressionTestCase;
import com.jstarcraft.core.script.groovy.GroovyFunctionTestCase;
import com.jstarcraft.core.script.js.JsExpressionTestCase;
import com.jstarcraft.core.script.js.JsFunctionTestCase;
import com.jstarcraft.core.script.kotlin.KotlinExpressionTestCase;
import com.jstarcraft.core.script.kotlin.KotlinFunctionTestCase;
import com.jstarcraft.core.script.lua.LuaExpressionTestCase;
import com.jstarcraft.core.script.lua.LuaFunctionTestCase;
import com.jstarcraft.core.script.mvel.MvelExpressionTestCase;
import com.jstarcraft.core.script.mvel.MvelFunctionTestCase;
import com.jstarcraft.core.script.python.PythonExpressionTestCase;
import com.jstarcraft.core.script.python.PythonFunctionTestCase;
import com.jstarcraft.core.script.ruby.RubyExpressionTestCase;
import com.jstarcraft.core.script.ruby.RubyFunctionTestCase;

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
