package com.jstarcraft.core.resource.definition;

import java.lang.reflect.Field;
import java.util.Comparator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jstarcraft.core.common.reflection.ReflectionUtility;
import com.jstarcraft.core.resource.exception.StorageException;
import com.jstarcraft.core.utility.StringUtility;

/**
 * 字段访问器
 * 
 * @author Birdy
 *
 */
class FieldAccessor implements PropertyAccessor {

    private static final Logger logger = LoggerFactory.getLogger(FieldAccessor.class);

    private final Field field;

    private final String name;

    private final boolean unique;

    private final Comparator comparator;

    public FieldAccessor(Field field, String name, boolean unique, Comparator comparator) {
        ReflectionUtility.makeAccessible(field);
        this.field = field;
        this.name = name;
        this.unique = unique;
        this.comparator = comparator;
    }

    @Override
    public Object getValue(Object object) {
        Object value = null;
        try {
            value = field.get(object);
        } catch (Exception exception) {
            String message = StringUtility.format("属性[{}]访问异常", field);
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
