package com.jstarcraft.core.utility;

import java.io.DataInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.ClassUtils;

import com.jstarcraft.core.common.compilation.MemoryClassLoader;

/**
 * 类型工具
 * 
 * @author Birdy
 *
 */
public class ClassUtility extends ClassUtils {

    public static Map<String, byte[]> classes2Bytes(Class<?>... classes) {
        HashMap<String, byte[]> bytes = new HashMap<>();
        for (Class<?> clazz : classes) {
            String name = clazz.getName();
            ClassLoader container = clazz.getClassLoader();
            if (container instanceof MemoryClassLoader) {
                bytes.put(name, MemoryClassLoader.class.cast(container).getBytes(name));
            } else {
                String path = name.replace('.', '/') + ".class";
                try (InputStream stream = container.getResourceAsStream(path); DataInputStream buffer = new DataInputStream(stream)) {
                    byte[] data = new byte[buffer.available()];
                    buffer.readFully(data);
                    bytes.put(name, data);
                } catch (Exception exception) {
                    throw new IllegalArgumentException(exception);
                }
            }
        }
        return bytes;
    }

    public static Class<?>[] bytes2Classes(Map<String, byte[]> bytes) {
        try (MemoryClassLoader classLoader = new MemoryClassLoader(bytes)) {
            Class<?>[] classes = new Class<?>[bytes.size()];
            int index = 0;
            for (String name : bytes.keySet()) {
                classes[index++] = classLoader.loadClass(name);
            }
            return classes;
        } catch (Exception exception) {
            throw new IllegalArgumentException(exception);
        }
    }

}
