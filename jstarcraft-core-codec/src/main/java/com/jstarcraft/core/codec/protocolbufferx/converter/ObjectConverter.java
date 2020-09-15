package com.jstarcraft.core.codec.protocolbufferx.converter;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;

import com.jstarcraft.core.codec.exception.CodecConvertionException;
import com.jstarcraft.core.codec.protocolbufferx.ProtocolReader;
import com.jstarcraft.core.codec.protocolbufferx.ProtocolWriter;
import com.jstarcraft.core.codec.specification.ClassDefinition;
import com.jstarcraft.core.codec.specification.PropertyDefinition;
import com.jstarcraft.core.common.reflection.Specification;
import com.jstarcraft.core.utility.StringUtility;

/**
 * 对象转换器
 * 
 * @author Birdy
 *
 */
public class ObjectConverter extends ProtocolConverter<Object> {

    /** 0000 0000(Null标记) */
    private static final byte NULL_MARK = (byte) 0x00;

    /** 0000 0000(对象标记) */
    private static final byte OBJECT_MARK = (byte) 0x01;

    /** 0000 0001(引用标记) */
    private static final byte REFERENCE_MARK = (byte) 0x02;

    @Override
    public Object readValueFrom(ProtocolReader context, Type type, ClassDefinition definition) throws Exception {
        InputStream in = context.getInputStream();
        byte information = (byte) in.read();
        byte mark = getMark(information);
        if (mark == NULL_MARK) {
            return null;
        }
        if (mark == OBJECT_MARK) {
            // #### 0000
            PropertyDefinition[] properties = definition.getProperties();
            Object object;
            try {
                object = definition.getInstance();
            } catch (Exception exception) {
                String message = StringUtility.format("获取类型[{}]实例异常", definition.getName());
                throw new CodecConvertionException(message, exception);
            }
            context.putObjectValue(object);
            // int length = PROPERTY_LIMIT & (byte) in.read();
            for (int index = 0; index < properties.length; index++) {
                PropertyDefinition property = properties[index];
                ProtocolConverter converter = context.getProtocolConverter(property.getSpecification());
                definition = context.getClassDefinition(property.getCode());
                Object value = converter.readValueFrom(context, property.getType(), definition);
                if (value == null) {
                    continue;
                }
                try {
                    property.setValue(object, value);
                } catch (Exception exception) {
                    String message = StringUtility.format("赋值[{}]实例属性[{}]异常", definition.getName(), property.getName());
                    throw new CodecConvertionException(message, exception);
                }
            }
            return object;
        } else if (mark == REFERENCE_MARK) {
            int reference = NumberConverter.readNumber(in).intValue();
            Object value = context.getObjectValue(reference);
            return value;
        }
        String message = StringUtility.format("类型码[{}]没有对应标记码[{}]", type, mark);
        throw new CodecConvertionException(message);
    }

    @Override
    public void writeValueTo(ProtocolWriter context, Type type, ClassDefinition definition, Object value) throws Exception {
        OutputStream out = context.getOutputStream();
        byte information = ClassDefinition.getMark(Specification.OBJECT);
        if (value == null) {
            out.write(information);
            return;
        }
        int reference = context.getObjectIndex(value);
        if (reference != -1) {
            information |= REFERENCE_MARK;
            out.write(information);
            NumberConverter.writeNumber(out, reference);
        } else {
            information |= OBJECT_MARK;
            out.write(information);
            context.putObjectValue(value);

            // int code = definition.getCode();
            // NumberConverter.writeNumber(out, code);
            PropertyDefinition[] properties = definition.getProperties();
            // int size = properties.size();
            // if (size > PROPERTY_LIMIT) {
            // String message = StringUtility.format("类型[{}]属性数量[{}]超过最大值[{}]",
            // definition.getClass(), size, PROPERTY_LIMIT);
            // throw new ProtocolConverterException(message, new
            // RuntimeException(message));
            // }
            // out.write((byte) size);
            for (PropertyDefinition property : properties) {
                Object object;
                try {
                    object = property.getValue(value);
                    ProtocolConverter converter = context.getProtocolConverter(property.getSpecification());
                    definition = context.getClassDefinition(property.getCode());
                    converter.writeValueTo(context, property.getType(), definition, object);
                } catch (Exception exception) {
                    String message = StringUtility.format("取值[{}]实例属性[{}]异常", definition.getName(), property.getName());
                    throw new CodecConvertionException(message, exception);
                }
            }
        }
    }

}
