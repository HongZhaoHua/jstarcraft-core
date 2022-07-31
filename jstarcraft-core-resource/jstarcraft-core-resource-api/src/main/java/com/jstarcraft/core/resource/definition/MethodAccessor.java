package com.jstarcraft.core.resource.definition;

import java.lang.reflect.Method;
import java.util.Comparator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jstarcraft.core.common.reflection.ReflectionUtility;
import com.jstarcraft.core.resource.exception.StorageException;
import com.jstarcraft.core.utility.StringUtility;

/**
 * 方法访问器
 * 
 * @author Birdy
 *
 */
class MethodAccessor implements PropertyAccessor {

    private static final Logger logger = LoggerFactory.getLogger(MethodAccessor.class);

    private final Method method;

    private final String name;

    private final boolean unique;

    private final Comparator comparator;

    public MethodAccessor(Method method, String name, boolean unique, Comparator comparator) {
        ReflectionUtility.makeAccessible(method);
        this.method = method;
        this.name = name;
        this.unique = unique;
        this.comparator = comparator;
    }

    @Override
    public Object getValue(Object object) {
        Object value = null;
        try {
            value = method.invoke(object);
        } catch (Exception exception) {
            String message = StringUtility.format("属性[{}]访问异常", method);
            logger.error(message);
            throw new StorageException(message, exception);
        }
        return value;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isUnique() {
        return unique;
    }

    @Override
    public Comparator getComparator() {
        return comparator;
    }

}
