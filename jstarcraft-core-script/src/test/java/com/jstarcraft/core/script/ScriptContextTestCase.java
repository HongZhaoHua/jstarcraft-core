package com.jstarcraft.core.script;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import com.jstarcraft.core.script.exception.ScriptContextException;

public class ScriptContextTestCase {

    @Test
    public void testConflict() throws Exception {
        ScriptContext context = new ScriptContext();
        // 允许一个类对应多个名称
        context.useClasses(Math.class);
        context.useClass("math", Math.class);

        // 不允许一个名称对应多个类
        try {
            context.useClass("Math", ScriptContextTestCase.class);
            Assert.fail();
        } catch (ScriptContextException exception) {
        }
        try {
            context.useClass("math", ScriptContextTestCase.class);
            Assert.fail();
        } catch (ScriptContextException exception) {
        }

        // 允许一个方法对应多个名称
        context.useMethods(Math.class.getMethod("abs", int.class));
        context.useMethod("Abs", Math.class.getMethod("abs", int.class));
        // 不允许一个名称对应多个方法
        try {
            context.useMethod("abs", Math.class.getMethod("abs", float.class));
            Assert.fail();
        } catch (ScriptContextException exception) {
        }
        try {
            context.useMethod("Abs", Math.class.getMethod("abs", float.class));
            Assert.fail();
        } catch (ScriptContextException exception) {
        }
    }

    @Test
    public void testCopy() throws Exception {
        ScriptContext context = new ScriptContext();
        // 允许一个类对应多个名称
        context.useClasses(Math.class);
        context.useClass("math", Math.class);

        // 允许一个方法对应多个名称
        context.useMethods(Math.class.getMethod("abs", int.class));
        context.useMethod("Abs", Math.class.getMethod("abs", int.class));

        ScriptContext copy = context.copyContext();
        Assert.assertThat(copy.getClasses().size(), CoreMatchers.equalTo(2));
        Assert.assertThat(copy.getMethods().size(), CoreMatchers.equalTo(2));
        Assert.assertTrue(copy.equals(context));
    }

}
