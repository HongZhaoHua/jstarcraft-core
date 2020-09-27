package com.jstarcraft.core.codec.standard.converter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import com.jstarcraft.core.codec.exception.CodecConvertionException;
import com.jstarcraft.core.codec.specification.ClassDefinition;
import com.jstarcraft.core.codec.standard.StandardReader;
import com.jstarcraft.core.codec.standard.StandardWriter;
import com.jstarcraft.core.common.reflection.Specification;
import com.jstarcraft.core.common.reflection.TypeUtility;

/**
 * 类型转换器
 * 
 * @author Birdy
 *
 */
public class TypeConverter extends StandardConverter<Type> {

    /** 0000 0000(Null标记) */
    private static final byte NULL_MARK = (byte) 0x00;

    /** 0000 0002(数组标记) */
    private static final byte ARRAY_MARK = (byte) 0x01;

    /** 0000 0001(类型标记) */
    private static final byte CLASS_MARK = (byte) 0x02;

    /** 0000 0003(泛型标记) */
    private static final byte GENERIC_MARK = (byte) 0x03;

    @Override
    public Type readValueFrom(StandardReader context, Type type, ClassDefinition definition) throws IOException {
        InputStream in = context.getInputStream();
        byte information = (byte) in.read();
        byte mark = getMark(information);
        if (mark == NULL_MARK) {
            return null;
        }
        if (mark == CLASS_MARK) {
            int code = NumberConverter.readNumber(in).intValue();
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
            int code = NumberConverter.readNumber(in).intValue();
            definition = context.getClassDefinition(code);
            int size = NumberConverter.readNumber(in).intValue();
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
    public void writeValueTo(StandardWriter context, Type type, ClassDefinition definition, Type instance) throws IOException {
        OutputStream out = context.getOutputStream();
        byte information = ClassDefinition.getMark(Specification.TYPE);
        if (instance == null) {
            out.write(information);
            return;
        }
        if (instance instanceof Class) {
            Class<?> clazz = TypeUtility.getRawType(instance, null);
            if (clazz.isArray()) {
                information |= ARRAY_MARK;
                out.write(information);
                instance = TypeUtility.getArrayComponentType(instance);
                writeValueTo(context, instance.getClass(), definition, instance);
            } else {
                information |= CLASS_MARK;
                out.write(information);
                definition = context.getClassDefinition(clazz);
                NumberConverter.writeNumber(out, definition.getCode());
            }
        } else if (instance instanceof GenericArrayType) {
            information |= ARRAY_MARK;
            out.write(information);
            instance = TypeUtility.getArrayComponentType(instance);
            writeValueTo(context, instance.getClass(), definition, instance);
        } else if (instance instanceof ParameterizedType) {
            information |= GENERIC_MARK;
            out.write(information);
            Class<?> clazz = TypeUtility.getRawType(instance, null);
            definition = context.getClassDefinition(clazz);
            NumberConverter.writeNumber(out, definition.getCode());
            ParameterizedType parameterizedType = (ParameterizedType) instance;
            Type[] types = parameterizedType.getActualTypeArguments();
            NumberConverter.writeNumber(out, types.length);
            for (int index = 0; index < types.length; index++) {
                writeValueTo(context, types[index].getClass(), definition, types[index]);
            }
        } else {
            throw new CodecConvertionException();
        }
    }

}
