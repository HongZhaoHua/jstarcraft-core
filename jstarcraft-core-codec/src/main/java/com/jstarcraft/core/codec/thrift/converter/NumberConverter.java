package com.jstarcraft.core.codec.thrift.converter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TField;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TStruct;
import org.apache.thrift.protocol.TType;

import com.jstarcraft.core.codec.exception.CodecConvertionException;
import com.jstarcraft.core.codec.specification.ClassDefinition;
import com.jstarcraft.core.utility.StringUtility;

/**
 * 数值转换器
 * 
 * @author Birdy
 *
 */
public class NumberConverter extends ThriftConverter<Number> {

    protected static final TField NULL_MARK = new TField(StringUtility.EMPTY, TType.BYTE, (short) 1);

    @Override
    public Number readValueFrom(ThriftContext context, Type type, ClassDefinition definition) throws IOException, TException, ParseException {
        TProtocol protocol = context.getProtocol();
        protocol.readStructBegin();
        Object instance;
        TField feild = protocol.readFieldBegin();
        if (NULL_MARK.equals(feild)) {
            instance = null;
        } else {
            if (type == Byte.class || type == byte.class) {
                instance = protocol.readByte();
            } else if (type == Short.class || type == short.class) {
                instance = protocol.readI16();
            } else if (type == Integer.class || type == int.class || type == AtomicInteger.class) {
                if (type == AtomicInteger.class) {
                    instance = new AtomicInteger(protocol.readI32());
                } else {
                    instance = protocol.readI32();
                }
            } else if (type == Long.class || type == long.class || type == AtomicLong.class) {
                if (type == AtomicLong.class) {
                    instance = new AtomicLong(protocol.readI64());
                } else {
                    instance = protocol.readI64();
                }
            } else if (type == BigInteger.class) {
                instance = new BigInteger(protocol.readString());
            } else if (type == Float.class || type == float.class) {
                instance = Float.intBitsToFloat(protocol.readI32());
            } else if (type == Double.class || type == double.class) {
                instance = Double.longBitsToDouble(protocol.readI64());
            } else if (type == BigDecimal.class) {
                instance = new BigDecimal(protocol.readString());
            } else {
                throw new CodecConvertionException();
            }
        }
        protocol.readFieldEnd();
        protocol.readFieldBegin();
        protocol.readStructEnd();
        return (Number) instance;
    }

    @Override
    public void writeValueTo(ThriftContext context, Type type, ClassDefinition definition, Number instance) throws IOException, TException {
        TProtocol protocol = context.getProtocol();
        protocol.writeStructBegin(new TStruct(definition.getName()));
        if (instance == null) {
            protocol.writeFieldBegin(NULL_MARK);
            protocol.writeFieldEnd();
        } else {
            if (type == Byte.class || type == byte.class) {
                protocol.writeFieldBegin(new TField(StringUtility.EMPTY, TType.BYTE, (short) 2));
                protocol.writeByte(instance.byteValue());
            } else if (type == Short.class || type == short.class) {
                protocol.writeFieldBegin(new TField(StringUtility.EMPTY, TType.I16, (short) 2));
                protocol.writeI16(instance.shortValue());
            } else if (type == Integer.class || type == int.class || type == AtomicInteger.class) {
                protocol.writeFieldBegin(new TField(StringUtility.EMPTY, TType.I32, (short) 2));
                protocol.writeI32(instance.intValue());
            } else if (type == Long.class || type == long.class || type == AtomicLong.class) {
                protocol.writeFieldBegin(new TField(StringUtility.EMPTY, TType.I64, (short) 2));
                protocol.writeI64(instance.longValue());
            } else if (type == BigInteger.class) {
                protocol.writeFieldBegin(new TField(StringUtility.EMPTY, TType.STRING, (short) 2));
                protocol.writeString(instance.toString());
            } else if (type == Float.class || type == float.class) {
                protocol.writeFieldBegin(new TField(StringUtility.EMPTY, TType.I32, (short) 2));
                protocol.writeI32(Float.floatToIntBits(instance.floatValue()));
            } else if (type == Double.class || type == double.class) {
                protocol.writeFieldBegin(new TField(StringUtility.EMPTY, TType.I64, (short) 2));
                protocol.writeI64(Double.doubleToLongBits(instance.doubleValue()));
            } else if (type == BigDecimal.class) {
                protocol.writeFieldBegin(new TField(StringUtility.EMPTY, TType.STRING, (short) 2));
                protocol.writeString(instance.toString());
            } else {
                throw new CodecConvertionException();
            }
            protocol.writeFieldEnd();
        }
        protocol.writeFieldStop();
        protocol.writeStructEnd();
    }

}
