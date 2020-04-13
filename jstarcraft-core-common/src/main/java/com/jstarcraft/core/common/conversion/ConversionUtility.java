package com.jstarcraft.core.common.conversion;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.GenericConversionService;

import com.jstarcraft.core.common.conversion.json.JsonUtility;
import com.jstarcraft.core.common.reflection.TypeUtility;
import com.jstarcraft.core.utility.NumberUtility;

/**
 * 转换工具
 * 
 * @author Birdy
 *
 */
public class ConversionUtility {

    /** 类型转换器 */
    private static final GenericConversionService CONVERTORS = new GenericConversionService();

    /**
     * 捆绑指定的转换器
     * 
     * @param converter
     */
    public static void bindConverter(Converter<?, ?> converter) {
        CONVERTORS.addConverter(converter);
    }

    /**
     * 解绑指定的转换器
     * 
     * @param sourceType
     * @param targetType
     */
    public static void unbindConverter(Class<?> sourceType, Class<?> targetType) {
        CONVERTORS.removeConvertible(sourceType, targetType);
    }

    /**
     * 对象类型转换
     * 
     * @param instance
     * @param type
     * @return
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static <T> T convert(Object instance, Type type) {
        if (type == null) {
            throw new IllegalArgumentException();
        }
        if (instance == null) {
            return null;
        }
        if (TypeUtility.isInstance(instance, type)) {
            return (T) instance;
        }
        // 变量类型的泛型
        if (type instanceof TypeVariable<?>) {
            return (T) instance;
        }
        try {
            // 枚举
            if (TypeUtility.isAssignable(type, Enum.class)) {
                Class clazz = (Class<T>) type;
                if (instance instanceof Number) {
                    T[] enumerations = (T[]) clazz.getEnumConstants();
                    int ordinal = ((Number) instance).intValue();
                    return enumerations[ordinal];
                } else if (instance instanceof String) {
                    return (T) Enum.valueOf(clazz, (String) instance);
                }
            }
            // 数值
            if (TypeUtility.isAssignable(type, Number.class)) {
                if (instance instanceof Number) {
                    return (T) NumberUtility.convert((Number) instance, (Class<? extends Number>) type);
                }
                if (instance instanceof String) {
                    return (T) NumberUtility.convert((String) instance, (Class<? extends Number>) type);
                }
            }
        } catch (Exception exception) {
        }
        Class<?> clazz = TypeUtility.getRawType(type, null);
        if (CONVERTORS.canConvert(instance.getClass(), clazz)) {
            return (T) CONVERTORS.convert(instance, clazz);
        }
        return JsonUtility.convert(instance, type);
    }

    public static final Boolean primitiveToWrap(boolean value) {
        return Boolean.valueOf(value);
    }

    public static final Byte primitiveToWrap(byte value) {
        return Byte.valueOf(value);
    }

    public static final Character primitiveToWrap(char value) {
        return Character.valueOf(value);
    }

    public static final Double primitiveToWrap(double value) {
        return Double.valueOf(value);
    }

    public static final Float primitiveToWrap(float value) {
        return Float.valueOf(value);
    }

    public static final Integer primitiveToWrap(int value) {
        return Integer.valueOf(value);
    }

    public static final Long primitiveToWrap(long value) {
        return Long.valueOf(value);
    }

    public static final Short primitiveToWrap(short value) {
        return Short.valueOf(value);
    }

    public static final Object primitiveToWrap(Object value) {
        return value;
    }

}
