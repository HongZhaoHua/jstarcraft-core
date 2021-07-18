package com.jstarcraft.core.script.kotlin;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import com.jstarcraft.core.utility.KeyValue;

public class KotlinTestCase {

    @Test
    public void testAlias() throws Exception {
        ScriptEngineManager factory = new ScriptEngineManager();
        ScriptEngine engine = factory.getEngineByName("kotlin");
        engine.eval("import com.jstarcraft.core.utility.KeyValue as KV;");
        engine.eval("import com.jstarcraft.core.common.reflection.TypeUtility.type2String as toString;");
        Assert.assertEquals(new KeyValue<>("key", "value"), engine.eval("KV(\"key\", \"value\")"));
        Assert.assertEquals("com.jstarcraft.core.utility.KeyValue<K, V>", engine.eval("toString(KV::class.java)"));
    }

    @Test
    public void testCompilable() throws Exception {
        ScriptEngineManager factory = new ScriptEngineManager();
        ScriptEngine engine = factory.getEngineByName("kotlin");
        Assert.assertTrue(engine instanceof Compilable);
        Compilable compilable = (Compilable) engine;
        CompiledScript script = compilable.compile("val number = 1000 + bindings[\"size\"] as Int; number / 2");
        Bindings context = engine.createBindings();
        context.put("size", 1000);
        Assert.assertThat(script.eval(context), CoreMatchers.equalTo(1000));
    }

    @Test
    public void testInvocable() throws Exception {
        ScriptEngineManager factory = new ScriptEngineManager();
        ScriptEngine engine = factory.getEngineByName("kotlin");
        Assert.assertTrue(engine instanceof Invocable);
        engine.eval("fun middle(left: Int, right: Int): Int { return (left + right) / 2; }");
        Invocable invocable = (Invocable) engine;
        System.out.println(invocable.invokeFunction("middle", 1000, 1000));
        Assert.assertThat(invocable.invokeFunction("middle", 1000, 1000), CoreMatchers.equalTo(1000));
    }

}
