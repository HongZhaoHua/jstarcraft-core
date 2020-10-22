package com.jstarcraft.core.script;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.junit.Test;

public class KotlinTestCase {

    @Test
    public void test() throws Exception {
        ScriptEngineManager factory = new ScriptEngineManager();
        System.out.println(factory.getEngineByName("kotlin"));;
        ScriptEngine engine = factory.getEngineByExtension("kts");
        engine.eval("val x = 3");
        System.out.println(engine.eval("x + 2"));
    }

}
