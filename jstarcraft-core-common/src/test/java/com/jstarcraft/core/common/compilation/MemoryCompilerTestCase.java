package com.jstarcraft.core.common.compilation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class MemoryCompilerTestCase {

    private MemoryCompiler compiler;

    @Before
    public void setUp() throws Exception {
        compiler = new MemoryCompiler();
    }

    private final static StringBuilder singleJava = new StringBuilder();

    static {
        singleJava.append("package com.jstarcraft.core.common.compilation;                                   ");
        singleJava.append("import com.jstarcraft.core.common.compilation.*;                 ");
        singleJava.append("public class Single extends MockObject implements Counter {    ");
        singleJava.append("    int count = 0;                                             ");
        singleJava.append("    public void setId(String id) {                             ");
        singleJava.append("        super.setId(id);                                       ");
        singleJava.append("        invokeCount();                                         ");
        singleJava.append("    }                                                          ");
        singleJava.append("    public void setName(String name) {                         ");
        singleJava.append("        super.setName(name);                                   ");
        singleJava.append("        invokeCount();                                         ");
        singleJava.append("    }                                                          ");
        singleJava.append("    public void setInstant(long instant) {                     ");
        singleJava.append("        super.setInstant(instant);                             ");
        singleJava.append("        invokeCount();                                         ");
        singleJava.append("    }                                                          ");
        singleJava.append("    public void invokeCount() {                                ");
        singleJava.append("        this.count++;                                          ");
        singleJava.append("    }                                                          ");
        singleJava.append("    public int getCount() {                                    ");
        singleJava.append("        return this.count;                                     ");
        singleJava.append("    }                                                          ");
        singleJava.append("}                                                              ");
    }

    @Test
    public void testCompileSingleClass() throws Exception {
        Map<String, byte[]> classes = compiler.compile("Single.java", singleJava.toString());
        assertEquals(1, classes.size());
        assertTrue(classes.containsKey("com.jstarcraft.core.common.compilation.Single"));

        try (MemoryClassLoader classLoader = new MemoryClassLoader(classes)) {
            Class<?> clazz = classLoader.loadClass("com.jstarcraft.core.common.compilation.Single");
            Method setId = clazz.getMethod("setId", String.class);
            Method setName = clazz.getMethod("setName", String.class);
            Method setInstant = clazz.getMethod("setInstant", long.class);

            Object instance = clazz.newInstance();
            Counter counter = Counter.class.cast(instance);
            Assert.assertEquals(0, counter.getCount());

            long instant = System.currentTimeMillis();
            setId.invoke(instance, "1");
            setName.invoke(instance, "Birdy");
            setInstant.invoke(instance, instant);

            MockObject object = (MockObject) instance;
            Assert.assertEquals("1", object.getId());
            Assert.assertEquals("Birdy", object.getName());
            Assert.assertEquals(instant, object.getInstant());
            Assert.assertEquals(3, counter.getCount());
        }
    }

    private final static StringBuilder multipleJava = new StringBuilder();

    static {
        multipleJava.append("package com.jstarcraft.core.common.compilation;                                   ");
        multipleJava.append("import java.util.*;                                            ");
        multipleJava.append("public class Multiple {                                        ");
        multipleJava.append("    List<MockObject> list = new ArrayList<MockObject>();       ");
        multipleJava.append("    public void add(String name) {                             ");
        multipleJava.append("        MockObject MockObject = new MockObject();              ");
        multipleJava.append("        MockObject.name = name;                                ");
        multipleJava.append("        this.list.add(MockObject);                             ");
        multipleJava.append("    }                                                          ");
        multipleJava.append("    public MockObject getMockObject() {                        ");
        multipleJava.append("        return this.list.get(0);                               ");
        multipleJava.append("    }                                                          ");
        multipleJava.append("    public static class StaticObject {                         ");
        multipleJava.append("        public int weight = 100;                               ");
        multipleJava.append("    }                                                          ");
        multipleJava.append("    class NestObject {                                         ");
        multipleJava.append("        NestObject() {                                         ");
        multipleJava.append("        }                                                      ");
        multipleJava.append("    }                                                          ");
        multipleJava.append("}                                                              ");
        multipleJava.append("class MockObject {                                             ");
        multipleJava.append("    String name = null;                                        ");
        multipleJava.append("}                                                              ");
    }

    @Test
    public void testCompileMultipleClasses() throws Exception {
        Map<String, byte[]> classes = compiler.compile("Multiple.java", multipleJava.toString());
        assertEquals(4, classes.size());
        assertTrue(classes.containsKey("com.jstarcraft.core.common.compilation.Multiple"));
        assertTrue(classes.containsKey("com.jstarcraft.core.common.compilation.Multiple$StaticObject"));
        assertTrue(classes.containsKey("com.jstarcraft.core.common.compilation.Multiple$NestObject"));
        assertTrue(classes.containsKey("com.jstarcraft.core.common.compilation.MockObject"));

        try (MemoryClassLoader classLoader = new MemoryClassLoader(classes)) {
            Class<?> clazz = classLoader.loadClass("com.jstarcraft.core.common.compilation.Multiple");
            Object instance = clazz.newInstance();
            assertNotNull(instance);
        }
    }

}
