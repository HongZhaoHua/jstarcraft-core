package com.jstarcraft.core.codec.thrift.converter;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TProtocol;

import com.jstarcraft.core.codec.exception.CodecConvertionException;
import com.jstarcraft.core.codec.specification.ClassDefinition;
import com.jstarcraft.core.common.reflection.Specification;
import com.jstarcraft.core.common.reflection.TypeUtility;

/**
 * 类型转换器
 * 
 * @author Birdy
 *
 */
public class TypeConverter extends ThriftConverter<Type> {

    /** 0000 0000(Null标记) */
    private static final byte NULL_MARK = (byte) 0x00;

    /** 0000 0002(数组标记) */
    private static final byte ARRAY_MARK = (byte) 0x01;

    /** 0000 0001(类型标记) */
    private static final byte CLASS_MARK = (byte) 0x02;

    /** 0000 0003(泛型标记) */
    private static final byte GENERIC_MARK = (byte) 0x03;

    @Override
    public Type readValueFrom(ThriftContext context, Type type, ClassDefinition definition) throws IOException, TException {
        TProtocol protocol = context.getProtocol();
        byte information = protocol.readByte();
        byte mark = getMark(information);
        if (mark == NULL_MARK) {
            return null;
        }
        if (mark == CLASS_MARK) {
            int code = protocol.readI32();
            definition = context.getClassDefinition(code);
            return definition.getType();
        } else if (mark == ARRAY_MARK) {
            if (type == Class.class) {
                type = readValueFrom(context, type, definition);
                Class<?> clazz = Class.class.cast(type);
                return Array.newInstance(clazz, 0).getClass();
            } else {
                type = readValueFrom(context, type, definition);
                return TypeUtility.genericArrayType(type);
            }
        } else if (mark == GENERIC_MARK) {
            int code = protocol.readI32();
            definition = context.getClassDefinition(code);
            int size = protocol.readI32();
            Type[] types = new Type[size];
            for (int index = 0; index < size; index++) {
                types[index] = readValueFrom(context, type, definition);
            }
            return TypeUtility.parameterize(definition.getType(), types);
        } else {
            throw new CodecConvertionException();
        }
    }

    @Override
    public void writeValueTo(ThriftContext context, Type type, ClassDefinition definition, Type value) throws IOException, TException {
        TProtocol protocol = context.getProtocol();
        byte information = ClassDefinition.getMark(Specification.TYPE);
        if (value == null) {
            protocol.writeByte(information);
            return;
        }
        if (value instanceof Class) {
            Class<?> clazz = TypeUtility.getRawType(value, null);
            if (clazz.isArray()) {
                information |= ARRAY_MARK;
                protocol.writeByte(information);
                value = TypeUtility.getArrayComponentType(value);
                writeValueTo(context, value.getClass(), definition, value);
            } else {
                information |= CLASS_MARK;
                protocol.writeByte(information);
                definition = context.getClassDefinition(clazz);
                protocol.writeI32(definition.getCode());
            }
        } else if (value instanceof GenericArrayType) {
            information |= ARRAY_MARK;
            protocol.writeByte(information);
            value = TypeUtility.getArrayComponentType(value);
            writeValueTo(context, value.getClass(), definition, value);
        } else if (value instanceof ParameterizedType) {
            information |= GENERIC_MARK;
            protocol.writeByte(information);
            Class<?> clazz = TypeUtility.getRawType(value, null);
            definition = context.getClassDefinition(clazz);
            protocol.writeI32(definition.getCode());
            ParameterizedType parameterizedType = (ParameterizedType) value;
            Type[] types = parameterizedType.getActualTypeArguments();
            protocol.writeI32(types.length);
            for (int index = 0; index < types.length; index++) {
                writeValueTo(context, types[index].getClass(), definition, types[index]);
            }
        } else {
            throw new CodecConvertionException();
        }
    }

}
