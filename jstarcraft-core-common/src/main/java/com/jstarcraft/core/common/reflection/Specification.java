package com.jstarcraft.core.common.reflection;

import java.lang.reflect.Type;
import java.time.Clock;
import java.time.chrono.Chronology;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalUnit;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;

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
    /** 日期时间 */
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

    private static final HashMap<Class<?>, Specification> dynamic2Specifitions = new HashMap<>();

    private static final HashMap<Class<?>, Specification> static2Specifitions = new HashMap<>();

    static {
        // 布尔规范
        static2Specifitions.put(AtomicBoolean.class, Specification.BOOLEAN);
        static2Specifitions.put(boolean.class, Specification.BOOLEAN);
        static2Specifitions.put(Boolean.class, Specification.BOOLEAN);

        // 数值规范
        dynamic2Specifitions.put(Number.class, Specification.NUMBER);
        static2Specifitions.put(byte.class, Specification.NUMBER);
        static2Specifitions.put(short.class, Specification.NUMBER);
        static2Specifitions.put(int.class, Specification.NUMBER);
        static2Specifitions.put(long.class, Specification.NUMBER);
        static2Specifitions.put(float.class, Specification.NUMBER);
        static2Specifitions.put(double.class, Specification.NUMBER);

        // 字符规范
        dynamic2Specifitions.put(CharSequence.class, Specification.STRING);
        static2Specifitions.put(char.class, Specification.STRING);
        static2Specifitions.put(Character.class, Specification.STRING);

        // 日期时间规范
        dynamic2Specifitions.put(Calendar.class, Specification.INSTANT);
        dynamic2Specifitions.put(Date.class, Specification.INSTANT);
        dynamic2Specifitions.put(Chronology.class, Specification.INSTANT);
        dynamic2Specifitions.put(Clock.class, Specification.INSTANT);
        dynamic2Specifitions.put(Temporal.class, Specification.INSTANT);
        dynamic2Specifitions.put(TemporalAccessor.class, Specification.INSTANT);
        dynamic2Specifitions.put(TemporalAdjuster.class, Specification.INSTANT);
        dynamic2Specifitions.put(TemporalAmount.class, Specification.INSTANT);
        dynamic2Specifitions.put(TemporalField.class, Specification.INSTANT);
        dynamic2Specifitions.put(TemporalUnit.class, Specification.INSTANT);

        // 类型规范
        dynamic2Specifitions.put(Type.class, Specification.TYPE);

        // 未知规范
        static2Specifitions.put(void.class, Specification.VOID);
        static2Specifitions.put(Void.class, Specification.VOID);
    }

    /**
     * 根据指定实例获取对应的规范类型
     * 
     * @param instance
     * @return
     */
    public static Specification getSpecification(Type type) {
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
        Specification specification = static2Specifitions.get(clazz);
        if (specification != null) {
            return specification;
        }
        for (Entry<Class<?>, Specification> keyValue : dynamic2Specifitions.entrySet()) {
            if (keyValue.getKey().isAssignableFrom(clazz)) {
                return keyValue.getValue();
            }
        }
        return Specification.OBJECT;
    }

}
