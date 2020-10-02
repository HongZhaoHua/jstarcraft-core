package com.jstarcraft.core.codec.standard.converter;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Type;

import com.jstarcraft.core.codec.exception.CodecConvertionException;
import com.jstarcraft.core.codec.specification.ClassDefinition;
import com.jstarcraft.core.codec.standard.StandardReader;
import com.jstarcraft.core.codec.standard.StandardWriter;
import com.jstarcraft.core.common.io.IoUtility;
import com.jstarcraft.core.common.reflection.Specification;
import com.jstarcraft.core.common.reflection.TypeUtility;
import com.jstarcraft.core.utility.StringUtility;

/**
 * 数组转换器
 * 
 * @author Birdy
 *
 */
public class ArrayConverter extends StandardConverter<Object> {

    /** 0000 0000(Null标记) */
    private static final byte NULL_MARK = (byte) 0x00;

    /** 0000 0001(显式标记) */
    private static final byte EXPLICIT_MARK = (byte) 0x01;

    /** 0000 0002(隐式标记) */
    private static final byte IMPLICIT_MARK = (byte) 0x02;

    /** 0000 0003(引用标记) */
    private static final byte REFERENCE_MARK = (byte) 0x03;

    @Override
    public Object readValueFrom(StandardReader context, Type type, ClassDefinition definition) throws Exception {
        InputStream in = context.getInputStream();
        byte information = (byte) in.read();
        byte mark = getMark(information);
        if (mark == NULL_MARK) {
            return null;
        }
        if (mark == EXPLICIT_MARK) {
            int size = NumberConverter.readNumber(in).intValue();
            int code = NumberConverter.readNumber(in).intValue();
            Object instance = null;
            definition = context.getClassDefinition(code);
            if (definition.getType() == byte.class) {
                // 对字节数组做特殊处理
                byte[] data = new byte[size];
                IoUtility.read(in, data);
                instance = data;
                context.putArrayValue(instance);
            } else {
                Class<?> clazz = TypeUtility.getRawType(type, null);
                clazz = clazz.getComponentType();
                instance = Array.newInstance(clazz, size);
                context.putArrayValue(instance);
                Specification specification = clazz.isArray() ? Specification.ARRAY : definition.getSpecification();
                StandardConverter converter = context.getStandardConverter(specification);
                for (int index = 0; index < size; index++) {
                    Object element = converter.readValueFrom(context, clazz, definition);
                    Array.set(instance, index, element);
                }
            }
            return instance;
        } else if (mark == IMPLICIT_MARK) {
            int size = NumberConverter.readNumber(in).intValue();
            Object[] instance = new Object[size];
            context.putArrayValue(instance);
            for (int index = 0; index < size; index++) {
                int code = NumberConverter.readNumber(in).intValue();
                definition = context.getClassDefinition(code);
                StandardConverter converter = context.getStandardConverter(definition.getSpecification());
                Object element = converter.readValueFrom(context, definition.getType(), definition);
                Array.set(instance, index, element);
            }
            return instance;
        } else if (mark == REFERENCE_MARK) {
            int reference = NumberConverter.readNumber(in).intValue();
            Object[] instance = (Object[]) context.getArrayValue(reference);
            return instance;
        }
        String message = StringUtility.format("类型码[{}]没有对应标记码[{}]", type, mark);
        throw new CodecConvertionException(message);
    }

    @Override
    public void writeValueTo(StandardWriter context, Type type, ClassDefinition definition, Object instance) throws Exception {
        OutputStream out = context.getOutputStream();
        byte information = ClassDefinition.getMark(Specification.ARRAY);
        if (instance == null) {
            out.write(information);
            return;
        }
        int reference = context.getArrayIndex(instance);
        if (reference != -1) {
            information |= REFERENCE_MARK;
            out.write(information);
            NumberConverter.writeNumber(out, reference);
        } else {
            Class<?> clazz = TypeUtility.getRawType(type, null);
            clazz = clazz.getComponentType();
            if (clazz == Object.class) {
                information |= IMPLICIT_MARK;
                context.putArrayValue(instance);
                out.write(information);
                int size = Array.getLength(instance);
                NumberConverter.writeNumber(out, size);
                for (int index = 0; index < size; index++) {
                    Object element = Array.get(instance, index);
                    definition = context.getClassDefinition(element == null ? Object.class : element.getClass());
                    int code = definition.getCode();
                    NumberConverter.writeNumber(out, code);
                    StandardConverter converter = context.getStandardConverter(definition.getSpecification());
                    converter.writeValueTo(context, definition.getType(), definition, element);
                }
            } else {
                information |= EXPLICIT_MARK;
                context.putArrayValue(instance);
                out.write(information);
                int size = Array.getLength(instance);
                NumberConverter.writeNumber(out, size);
                definition = context.getClassDefinition(clazz);
                int code = definition.getCode();
                NumberConverter.writeNumber(out, code);
                if (clazz == byte.class) {
                    // 对字节数组做特殊处理
                    byte[] data = (byte[]) instance;
                    IoUtility.write(data, out);
                } else {
                    Specification specification = clazz.isArray() ? Specification.ARRAY : definition.getSpecification();
                    StandardConverter converter = context.getStandardConverter(specification);
                    for (int index = 0; index < size; index++) {
                        Object element = Array.get(instance, index);
                        converter.writeValueTo(context, clazz, definition, element);
                    }
                }
            }
        }
    }

}
