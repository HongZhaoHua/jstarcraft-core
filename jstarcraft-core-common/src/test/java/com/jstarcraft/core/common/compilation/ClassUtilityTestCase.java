package com.jstarcraft.core.common.compilation;

import java.io.DataInputStream;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import org.junit.Assert;
import org.junit.Test;

import com.jstarcraft.core.common.conversion.json.JsonUtility;
import com.jstarcraft.core.common.reflection.TypeUtility;
import com.jstarcraft.core.utility.ClassUtility;

public class ClassUtilityTestCase {

    @Test
    public void test() throws Exception {
        // 模拟类不在ClassLoader的情况
        String name = "com.jstarcraft.core.common.compilation.MockTask";
        String path = "MockTask.clazz";
        Map<String, byte[]> bytes = new HashMap<>();
        try (InputStream stream = ClassUtilityTestCase.class.getResourceAsStream(path); DataInputStream buffer = new DataInputStream(stream)) {
            byte[] data = new byte[buffer.available()];
            buffer.readFully(data);
            bytes.put(name, data);
            Class<?>[] classes = ClassUtility.bytes2Classes(bytes);
            Type type = TypeUtility.parameterize(classes[0], String.class);
            Callable<String> task = JsonUtility.string2Object("{\"value\":\"birdy\"}", type);
            Assert.assertEquals("birdy", task.call());
            Assert.assertEquals(bytes, ClassUtility.classes2Bytes(classes));
        }
    }

}
