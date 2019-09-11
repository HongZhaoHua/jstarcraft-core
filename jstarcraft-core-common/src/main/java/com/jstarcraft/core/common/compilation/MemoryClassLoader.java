package com.jstarcraft.core.common.compilation;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

/**
 * 内存装载器
 * 
 * @author Birdy
 *
 */
public class MemoryClassLoader extends URLClassLoader {

    private Map<String, byte[]> bytes = new HashMap<String, byte[]>();

    public MemoryClassLoader(Map<String, byte[]> bytes) {
        this(bytes, Thread.currentThread().getContextClassLoader());
    }

    public MemoryClassLoader(Map<String, byte[]> bytes, ClassLoader container) {
        super(new URL[0], container);
        this.bytes.putAll(bytes);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        try {
            Class<?> clazz = super.findClass(name);
            return clazz;
        } catch (ClassNotFoundException exception) {
            byte[] buffer = bytes.get(name);
            if (buffer == null) {
                throw exception;
            } else {
                return super.defineClass(name, buffer, 0, buffer.length);
            }
        }
    }

    public byte[] getBytes(String name) {
        return bytes.get(name);
    }

}
