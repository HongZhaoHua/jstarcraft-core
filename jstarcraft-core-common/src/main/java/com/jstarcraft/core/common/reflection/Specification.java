package com.jstarcraft.core.common.reflection;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 类型规范
 * 
 * @author Birdy
 *
 */
public enum Specification {

    /** 数组 */
    ARRAY,
    /** 布尔 */
    BOOLEAN,
    /** 集合 */
    COLLECTION,
    /** 枚举 */
    ENUMERATION,
    /** 时间 */
    INSTANT,
    /** 映射 */
    MAP,
    /** 数值 */
    NUMBER,
    /** 对象 */
    OBJECT,
    /** 字符串 */
    STRING,
    /** 类型 */
    TYPE,
    /** 未知 */
    VOID;

    public static final HashMap<Class<?>, Specification> type2Specifitions = new HashMap<>();

    static {
        // 布尔规范
        type2Specifitions.put(AtomicBoolean.class, Specification.BOOLEAN);
        type2Specifitions.put(boolean.class, Specification.BOOLEAN);
        type2Specifitions.put(Boolean.class, Specification.BOOLEAN);

        // 数值规范
        type2Specifitions.put(AtomicInteger.class, Specification.NUMBER);
        type2Specifitions.put(AtomicLong.class, Specification.NUMBER);
        type2Specifitions.put(byte.class, Specification.NUMBER);
        type2Specifitions.put(short.class, Specification.NUMBER);
        type2Specifitions.put(int.class, Specification.NUMBER);
        type2Specifitions.put(long.class, Specification.NUMBER);
        type2Specifitions.put(float.class, Specification.NUMBER);
        type2Specifitions.put(double.class, Specification.NUMBER);
        type2Specifitions.put(Byte.class, Specification.NUMBER);
        type2Specifitions.put(Short.class, Specification.NUMBER);
        type2Specifitions.put(Integer.class, Specification.NUMBER);
        type2Specifitions.put(Long.class, Specification.NUMBER);
        type2Specifitions.put(Float.class, Specification.NUMBER);
        type2Specifitions.put(Double.class, Specification.NUMBER);
        type2Specifitions.put(BigInteger.class, Specification.NUMBER);
        type2Specifitions.put(BigDecimal.class, Specification.NUMBER);

        // 字符规范
        type2Specifitions.put(char.class, Specification.STRING);
        type2Specifitions.put(Character.class, Specification.STRING);
        type2Specifitions.put(String.class, Specification.STRING);

        // 时间规范
        type2Specifitions.put(Date.class, Specification.INSTANT);
        type2Specifitions.put(Instant.class, Specification.INSTANT);

        // 类型规范
        type2Specifitions.put(Class.class, Specification.TYPE);
        type2Specifitions.put(GenericArrayType.class, Specification.TYPE);
        type2Specifitions.put(ParameterizedType.class, Specification.TYPE);
        type2Specifitions.put(TypeVariable.class, Specification.TYPE);
        type2Specifitions.put(WildcardType.class, Specification.TYPE);

        // 未知规范
        type2Specifitions.put(void.class, Specification.VOID);
        type2Specifitions.put(Void.class, Specification.VOID);
    }

    /**
     * 根据指定实例获取对应的规范类型
     * 
     * @param instance
     * @return
     */
    public static Specification getSpecification(Type type) {
        if (type == null) {
            return Specification.VOID;
        }
        if (TypeUtility.isAssignable(type, Type.class)) {
            return Specification.TYPE;
        }
        Class<?> clazz = TypeUtility.getRawType(type, null);
        if (clazz.isArray()) {
            return Specification.ARRAY;
        }
        if (clazz.isEnum()) {
            return Specification.ENUMERATION;
        }
        if (Collection.class.isAssignableFrom(clazz)) {
            return Specification.COLLECTION;
        }
        if (Map.class.isAssignableFrom(clazz)) {
            return Specification.MAP;
        }
        Specification specification = type2Specifitions.get(clazz);
        return specification != null ? specification : Specification.OBJECT;
    }

}
