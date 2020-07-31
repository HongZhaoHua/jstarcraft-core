package com.jstarcraft.core.codec.protocolbufferx.converter;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Type;

import com.jstarcraft.core.codec.exception.CodecConvertionException;
import com.jstarcraft.core.codec.protocolbufferx.ProtocolReader;
import com.jstarcraft.core.codec.protocolbufferx.ProtocolWriter;
import com.jstarcraft.core.codec.specification.ClassDefinition;
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
public class ArrayConverter extends ProtocolConverter<Object> {

    /** 0000 0000(Null标记) */
    private static final byte NULL_MARK = (byte) 0x00;

    /** 0000 0001(显式标记) */
    private static final byte EXPLICIT_MARK = (byte) 0x01;

    /** 0000 0002(隐式标记) */
    private static final byte IMPLICIT_MARK = (byte) 0x02;

    /** 0000 0003(引用标记) */
    private static final byte REFERENCE_MARK = (byte) 0x03;

    @Override
    public Object readValueFrom(ProtocolReader context, Type type, ClassDefinition definition) throws Exception {
        InputStream in = context.getInputStream();
        byte information = (byte) in.read();
        byte mark = getMark(information);
        if (mark == NULL_MARK) {
            return null;
        }
        if (mark == EXPLICIT_MARK) {
            int size = NumberConverter.readNumber(in).intValue();
            int code = NumberConverter.readNumber(in).intValue();
            Object value = null;
            definition = context.getClassDefinition(code);
            if (definition.getType() == byte.class) {
                // 对字节数组做特殊处理
                byte[] data = new byte[size];
                IoUtility.read(in, data);
                value = data;
                context.putArrayValue(value);
            } else {
                Class<?> clazz = TypeUtility.getRawType(type, null);
                clazz = clazz.getComponentType();
                value = Array.newInstance(clazz, size);
                context.putArrayValue(value);
                Specification specification = clazz.isArray() ? Specification.ARRAY : definition.getSpecification();
                ProtocolConverter converter = context.getProtocolConverter(specification);
                for (int index = 0; index < size; index++) {
                    Object object = converter.readValueFrom(context, clazz, definition);
                    Array.set(value, index, object);
                }
            }
            return value;
        } else if (mark == IMPLICIT_MARK) {
            int size = NumberConverter.readNumber(in).intValue();
            Object[] value = new Object[size];
            context.putArrayValue(value);
            for (int index = 0; index < size; index++) {
                int code = NumberConverter.readNumber(in).intValue();
                definition = context.getClassDefinition(code);
                ProtocolConverter converter = context.getProtocolConverter(definition.getSpecification());
                Object object = converter.readValueFrom(context, definition.getType(), definition);
                Array.set(value, index, object);
            }
            return value;
        } else if (mark == REFERENCE_MARK) {
            int reference = NumberConverter.readNumber(in).intValue();
            Object[] value = (Object[]) context.getArrayValue(reference);
            return value;
        }
        String message = StringUtility.format("类型码[{}]没有对应标记码[{}]", type, mark);
        throw new CodecConvertionException(message);
    }

    @Override
    public void writeValueTo(ProtocolWriter context, Type type, ClassDefinition definition, Object value) throws Exception {
        OutputStream out = context.getOutputStream();
        byte information = ClassDefinition.getCode(Specification.ARRAY);
        if (value == null) {
            out.write(information);
            return;
        }
        int reference = context.getArrayIndex(value);
        if (reference != -1) {
            information |= REFERENCE_MARK;
            out.write(information);
            NumberConverter.writeNumber(out, reference);
        } else {
            Class<?> clazz = TypeUtility.getRawType(type, null);
            clazz = clazz.getComponentType();
            if (clazz == Object.class) {
                information |= IMPLICIT_MARK;
                context.putArrayValue(value);
                out.write(information);
                int size = Array.getLength(value);
                NumberConverter.writeNumber(out, size);
                for (int index = 0; index < size; index++) {
                    Object object = Array.get(value, index);
                    definition = context.getClassDefinition(object == null ? Object.class : object.getClass());
                    int code = definition.getCode();
                    NumberConverter.writeNumber(out, code);
                    ProtocolConverter converter = context.getProtocolConverter(definition.getSpecification());
                    converter.writeValueTo(context, definition.getType(), definition, object);
                }
            } else {
                information |= EXPLICIT_MARK;
                context.putArrayValue(value);
                out.write(information);
                int size = Array.getLength(value);
                NumberConverter.writeNumber(out, size);
                definition = context.getClassDefinition(clazz);
                int code = definition.getCode();
                NumberConverter.writeNumber(out, code);
                if (clazz == byte.class) {
                    // 对字节数组做特殊处理
                    byte[] data = (byte[]) value;
                    IoUtility.write(data, out);
                } else {
                    Specification specification = clazz.isArray() ? Specification.ARRAY : definition.getSpecification();
                    ProtocolConverter converter = context.getProtocolConverter(specification);
                    for (int index = 0; index < size; index++) {
                        Object object = Array.get(value, index);
                        converter.writeValueTo(context, clazz, definition, object);
                    }
                }
            }
        }
    }

}
