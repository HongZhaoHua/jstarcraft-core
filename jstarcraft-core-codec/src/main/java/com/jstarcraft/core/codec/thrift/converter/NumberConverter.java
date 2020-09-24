package com.jstarcraft.core.codec.thrift.converter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.thrift.TException;

import com.jstarcraft.core.codec.exception.CodecConvertionException;
import com.jstarcraft.core.codec.specification.ClassDefinition;
import com.jstarcraft.core.codec.thrift.ThriftReader;
import com.jstarcraft.core.codec.thrift.ThriftWriter;

/**
 * 数值转换器
 * 
 * @author Birdy
 *
 */
public class NumberConverter extends ProtocolConverter<Number> {

    /**
     * 空标记
     */
    private static final byte NULL = 1;
    /**
     * 非空标记
     */
    private static final byte NOT_NULL = 0;

    @Override
    public Number readValueFrom(ThriftReader context, Type type, ClassDefinition definition) throws IOException, TException, ParseException {
        // 获取空标记
        byte nil = protocol.readByte();
        if (nil == NULL) {
            protocol.readByte();
            return null;
        }
        if (type == Byte.class || type == byte.class) {
            Byte value = protocol.readByte();
            return value;
        } else if (type == Short.class || type == short.class) {
            Short value = protocol.readI16();
            return value;
        } else if (type == Integer.class || type == int.class || type == AtomicInteger.class) {
            int value = protocol.readI32();
            if (type == AtomicInteger.class) {
                return new AtomicInteger(value);
            } else {
                return value;
            }
        } else if (type == Long.class || type == long.class || type == AtomicLong.class) {
            long value = protocol.readI64();
            if (type == AtomicLong.class) {
                return new AtomicLong(value);
            } else {
                return value;
            }
        } else if (type == BigInteger.class) {
            String value = protocol.readString();
            return new BigInteger(value);
        } else if (type == Float.class || type == float.class) {
            double value = protocol.readDouble();
            return Float.parseFloat(String.valueOf(value));
        } else if (type == Double.class || type == double.class) {
            double value = protocol.readDouble();
            return value;
        } else if (type == BigDecimal.class) {
            String value = protocol.readString();
            return new BigDecimal(value);
        } else {
            throw new CodecConvertionException();
        }
    }

    @Override
    public void writeValueTo(ThriftWriter context, Type type, ClassDefinition definition, Number value) throws IOException, TException {
        if (value == null) {
            protocol.writeByte(NULL);
            protocol.writeByte((byte) 0);
            return;
        } else {
            protocol.writeByte(NOT_NULL);
        }
        if (type == Byte.class || type == byte.class) {
            protocol.writeByte(value.byteValue());
        } else if (type == Short.class || type == short.class) {
            protocol.writeI16(value.shortValue());
        } else if (type == Integer.class || type == int.class || type == AtomicInteger.class) {
            protocol.writeI32(value.intValue());
        } else if (type == Long.class || type == long.class || type == AtomicLong.class) {
            protocol.writeI64(value.longValue());
        } else if (type == BigInteger.class) {
            protocol.writeString(value.toString());
        } else if (type == Float.class || type == float.class) {
            protocol.writeDouble(value.doubleValue());
        } else if (type == Double.class || type == double.class) {
            protocol.writeDouble(value.doubleValue());
        } else if (type == BigDecimal.class) {
            protocol.writeString(value.toString());
        } else {
            throw new CodecConvertionException();
        }
    }
}
