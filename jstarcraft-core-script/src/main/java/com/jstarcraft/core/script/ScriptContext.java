package com.jstarcraft.core.script;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.jstarcraft.core.script.exception.ScriptContextException;
import com.jstarcraft.core.utility.StringUtility;
import com.jstarcraft.core.utility.PackageUtility.PackageScanner;

/**
 * 脚本上下文
 * 
 * <pre>
 *  每个表达式,函数独立使用各自的上下文,保证线程安全
 * </pre>
 * 
 * @author Birdy
 *
 */
public class ScriptContext {

    private HashMap<String, Class<?>> contextClasses = new HashMap<>();

    private HashMap<String, Method> contextMethods = new HashMap<>();

    /**
     * 在上下文中使用指定类
     * 
     * @param name
     * @param clazz
     * @return
     */
    public ScriptContext useClass(String name, Class<?> clazz) {
        if (contextClasses.containsKey(name) || contextMethods.containsKey(name)) {
            throw new ScriptContextException(StringUtility.format("脚本上下文名称冲突[{}]", name));
        } else {
            contextClasses.put(name, clazz);
            return this;
        }
    }

    /**
     * 在上下文中使用指定类
     * 
     * @param classes
     * @return
     */
    public ScriptContext useClasses(String... packages) {
        PackageScanner scanner = new PackageScanner(packages);
        Collection<Class<?>> classes = scanner.getClazzCollection();
        useClasses(classes.toArray(new Class[classes.size()]));
        return this;
    }

    /**
     * 在上下文中使用指定类
     * 
     * @param classes
     * @return
     */
    public ScriptContext useClasses(Class<?>... classes) {
        for (Class<?> clazz : classes) {
            useClass(clazz.getSimpleName(), clazz);
        }
        return this;
    }

    /**
     * 在上下文中使用指定类
     * 
     * @param classes
     * @return
     */
    public ScriptContext useClasses(Map<String, Class<?>> classes) {
        for (Entry<String, Class<?>> keyValue : classes.entrySet()) {
            useClass(keyValue.getKey(), keyValue.getValue());
        }
        return this;
    }

    /**
     * 在上下文中使用指定方法
     * 
     * @param name
     * @param method
     * @return
     */
    public ScriptContext useMethod(String name, Method method) {
        if (contextClasses.containsKey(name) || contextMethods.containsKey(name)) {
            throw new ScriptContextException(StringUtility.format("脚本上下文名称冲突[{}]", name));
        } else {
            contextMethods.put(name, method);
            return this;
        }
    }

    /**
     * 在上下文中使用指定方法
     * 
     * @param methods
     * @return
     */
    public ScriptContext useMethods(Method... methods) {
        for (Method method : methods) {
            useMethod(method.getName(), method);
        }
        return this;
    }

    /**
     * 在上下文中使用指定方法
     * 
     * @param methods
     * @return
     */
    public ScriptContext useMethods(Collection<Method> methods) {
        for (Method method : methods) {
            useMethod(method.getName(), method);
        }
        return this;
    }

    /**
     * 在上下文中使用指定方法
     * 
     * @param methods
     * @return
     */
    public ScriptContext useMethods(Map<String, Method> methods) {
        for (Entry<String, Method> keyValue : methods.entrySet()) {
            useMethod(keyValue.getKey(), keyValue.getValue());
        }
        return this;
    }

    /**
     * 获取上下文使用的类
     * 
     * @return
     */
    public HashMap<String, Class<?>> getClasses() {
        return new HashMap<>(contextClasses);
    }

    /**
     * 获取上下文使用的方法
     * 
     * @return
     */
    public HashMap<String, Method> getMethods() {
        return new HashMap<>(contextMethods);
    }

    /**
     * 拷贝上下文
     * 
     * @return
     */
    ScriptContext copyContext() {
        ScriptContext context = new ScriptContext();
        context.contextClasses.putAll(contextClasses);
        context.contextMethods.putAll(contextMethods);
        return context;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object)
            return true;
        if (object == null)
            return false;
        if (getClass() != object.getClass())
            return false;
        ScriptContext that = (ScriptContext) object;
        EqualsBuilder equal = new EqualsBuilder();
        equal.append(this.contextClasses, that.contextClasses);
        equal.append(this.contextMethods, that.contextMethods);
        return equal.isEquals();
    }

    @Override
    public int hashCode() {
        HashCodeBuilder hash = new HashCodeBuilder();
        hash.append(contextClasses);
        hash.append(contextMethods);
        return hash.toHashCode();
    }

}
