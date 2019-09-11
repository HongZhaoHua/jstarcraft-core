package com.jstarcraft.core.utility;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang3.math.NumberUtils;

/**
 * 数值工具
 * 
 * @author Bridy
 *
 */
public class NumberUtility extends NumberUtils {

    @SuppressWarnings("unchecked")
    public static <T extends Number> T convert(Number value, Class<T> type) {
        if (type == null) {
            throw new IllegalArgumentException();
        }
        if (type == AtomicInteger.class) {
            return (T) new AtomicInteger(value.intValue());
        }
        if (type == AtomicLong.class) {
            return (T) new AtomicLong(value.longValue());
        }
        if (type == byte.class || type == Byte.class) {
            return (T) Byte.valueOf(value.byteValue());
        } else if (type == short.class || type == Short.class) {
            return (T) Short.valueOf(value.shortValue());
        } else if (type == int.class || type == Integer.class) {
            return (T) Integer.valueOf(value.intValue());
        } else if (type == long.class || type == Long.class) {
            return (T) Long.valueOf(value.longValue());
        } else if (type == double.class || type == Double.class) {
            return (T) Double.valueOf(value.doubleValue());
        } else if (type == float.class || type == Float.class) {
            return (T) Float.valueOf(value.floatValue());
        } else if (type == BigInteger.class || type == BigDecimal.class) {
            return (T) convert(value.toString(), type);
        }
        throw new IllegalArgumentException("不支持的数值类型");
    }

    @SuppressWarnings("unchecked")
    public static <T extends Number> T convert(String value, Class<T> type) {
        if (type == null) {
            throw new IllegalArgumentException();
        }
        if (type == AtomicInteger.class) {
            return (T) new AtomicInteger(Integer.valueOf(value));
        }
        if (type == AtomicLong.class) {
            return (T) new AtomicLong(Long.valueOf(value));
        }
        if (type == byte.class || type == Byte.class) {
            return (T) Byte.valueOf(value);
        } else if (type == short.class || type == Short.class) {
            return (T) Short.valueOf(value);
        } else if (type == int.class || type == Integer.class) {
            return (T) Integer.valueOf(value);
        } else if (type == long.class || type == Long.class) {
            return (T) Long.valueOf(value);
        } else if (type == double.class || type == Double.class) {
            return (T) Double.valueOf(value);
        } else if (type == float.class || type == Float.class) {
            return (T) Float.valueOf(value);
        } else if (type == BigInteger.class) {
            return (T) new BigInteger(value);
        } else if (type == BigDecimal.class) {
            return (T) new BigDecimal(value);
        }
        throw new IllegalArgumentException("不支持的数值类型");
    }

}
