package com.jstarcraft.core.codec.specification;

import java.util.HashMap;
import java.util.Map.Entry;

/**
 * 类型匹配器
 * 
 * @author Birdy
 *
 */
public class ClassMatcher<T> {

    /**
     * <pre>
     * 使用Clazz而不使用Class是因为计算效率
     * </pre>
     * 
     * @author Birdy
     *
     */
    public final class Clazz implements Comparable<Clazz> {

        private Class<?> clazz;

        private String name;

        private int hash;

        public Clazz() {
            this.clazz = null;
            this.name = null;
            this.hash = 0;
        }

        public Clazz(Class<?> clazz) {
            this.clazz = clazz;
            this.name = clazz.getName();
            this.hash = name.hashCode();
        }

        public void set(Class<?> clazz) {
            this.clazz = clazz;
            this.name = clazz.getName();
            this.hash = name.hashCode();
        }

        @Override
        public int compareTo(Clazz that) {
            return this.name.compareTo(that.name);
        }

        @Override
        public boolean equals(Object object) {
            if (object == this)
                return true;
            if (object == null)
                return false;
            if (getClass() != object.getClass())
                return false;
            Clazz that = (Clazz) object;
            return this.clazz == that.clazz;
        }

        @Override
        public int hashCode() {
            return hash;
        }

        @Override
        public String toString() {
            return name;
        }

    }

    protected HashMap<Clazz, T> classes = new HashMap<>();

    protected HashMap<Clazz, T> interfaces = new HashMap<>();

    protected boolean hasEnumeration = false;

    public ClassMatcher() {
    }

    /**
     * 匹配
     * 
     * @param key
     * @return
     */
    public T match(Class<?> key) {
        Clazz clazz = new Clazz(key);
        T value = null;
        if (key.isInterface()) {
            // 根据接口查找
            if (!interfaces.isEmpty()) {
                value = interfaces.get(clazz);
                if (value != null) {
                    return value;
                }
            }
        } else {
            // 根据类查找
            if (!classes.isEmpty()) {
                value = classes.get(clazz);
                if (value != null) {
                    return value;
                }
                if (hasEnumeration && key.isEnum()) {
                    clazz.set(Enum.class);
                    value = classes.get(clazz);
                    if (value != null) {
                        return value;
                    }
                }
                for (Class<?> current = key; (current != null); current = current.getSuperclass()) {
                    clazz.set(current);
                    value = classes.get(clazz);
                    if (value != null) {
                        return value;
                    }
                }
                if (key.isArray()) {
                    for (Entry<Clazz, T> keyValue : classes.entrySet()) {
                        if (keyValue.getKey().clazz.isAssignableFrom(key)) {
                            return keyValue.getValue();
                        }
                    }
                }
            }
        }
        // 根据接口查找
        if (!interfaces.isEmpty()) {
            value = match(key.getInterfaces(), clazz);
            if (value != null) {
                return value;
            }
            if (!key.isInterface()) {
                for (Class<?> current = key; (current != null); current = current.getSuperclass()) {
                    value = match(current.getInterfaces(), clazz);
                    if (value != null) {
                        return value;
                    }
                }
            }
        }
        return null;
    }

    protected T match(Class<?>[] keys, Clazz clazz) {
        for (Class<?> key : keys) {
            clazz.set(key);
            T value = interfaces.get(clazz);
            if (value != null) {
                return value;
            }
            value = match(key.getInterfaces(), clazz);
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    /**
     * 设置
     * 
     * @param key
     * @param value
     */
    public void set(Class<?> key, T value) {
        Clazz clazz = new Clazz(key);
        if (value != null) {
            if (key.isInterface()) {
                interfaces.put(clazz, value);
            } else {
                classes.put(clazz, value);
                if (key == Enum.class) {
                    hasEnumeration = true;
                }
            }
        } else {
            if (key.isInterface()) {
                interfaces.remove(clazz);
            } else {
                classes.remove(clazz);
                if (key == Enum.class) {
                    hasEnumeration = false;
                }
            }
        }
    }

}
