package com.jstarcraft.core.codec.thrift.converter;

import java.lang.reflect.Type;

import org.apache.thrift.protocol.TField;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TStruct;

import com.jstarcraft.core.codec.exception.CodecConvertionException;
import com.jstarcraft.core.codec.specification.ClassDefinition;
import com.jstarcraft.core.codec.specification.PropertyDefinition;
import com.jstarcraft.core.utility.StringUtility;

/**
 * 对象转换器
 * 
 * @author Birdy
 *
 */
public class ObjectConverter extends ThriftConverter<Object> {

    /**
     * 空标记
     */
    private static final byte NULL = 1;
    /**
     * 非空标记
     */
    private static final byte NOT_NULL = 0;

    @Override
    public Object readValueFrom(ThriftContext context, Type type, ClassDefinition definition) throws Exception {
        TProtocol protocol = context.getProtocol();
        byte nil = protocol.readByte();
        protocol.readStructBegin();
        PropertyDefinition[] properties = definition.getProperties();
        Object object;
        try {
            object = definition.getInstance();
        } catch (Exception exception) {
            String message = StringUtility.format("获取类型[{}]实例异常", definition.getName());
            throw new CodecConvertionException(message, exception);
        }
        for (int index = 0; index < properties.length; index++) {
            PropertyDefinition property = properties[index];
            protocol.readFieldBegin();
            ThriftConverter converter = context.getProtocolConverter(property.getSpecification());
            definition = context.getClassDefinition(property.getCode());
            Object value = converter.readValueFrom(context, property.getType(), definition);
            try {
                property.setValue(object, value);
            } catch (Exception exception) {
                String message = StringUtility.format("赋值[{}]实例属性[{}]异常", definition.getName(), property.getName());
                throw new CodecConvertionException(message, exception);
            }
            protocol.readFieldEnd();
        }
        // 读TType.STOP
        protocol.readFieldBegin();
        protocol.readStructEnd();
        if (nil == NULL) {
            return null;
        }
        return object;
    }

    @Override
    public void writeValueTo(ThriftContext context, Type type, ClassDefinition definition, Object value) throws Exception {
        TProtocol protocol = context.getProtocol();
        if (value == null) {
            protocol.writeByte(NULL);
        } else {
            protocol.writeByte(NOT_NULL);
        }
        protocol.writeStructBegin(new TStruct(definition.getName()));
        PropertyDefinition[] properties = definition.getProperties();
        if (value == null) {
            try {
                value = definition.getInstance();
            } catch (Exception exception) {
                String message = StringUtility.format("获取类型[{}]实例异常", definition.getName());
                throw new CodecConvertionException(message, exception);
            }
        }
        for (int index = 0; index < properties.length; index++) {
            PropertyDefinition property = properties[index];
            protocol.writeFieldBegin(new TField(property.getName(), context.getThriftType(property.getType()), (short) (index + 1)));
            Object object;
            try {
                ThriftConverter converter = context.getProtocolConverter(property.getSpecification());
                definition = context.getClassDefinition(property.getCode());
                object = property.getValue(value);
                converter.writeValueTo(context, property.getType(), definition, object);
            } catch (Exception exception) {
                String message = StringUtility.format("取值[{}]实例属性[{}]异常", definition.getName(), property.getName());
                throw new CodecConvertionException(message, exception);
            }
            protocol.writeFieldEnd();
        }
        // 写TType.STOP
        protocol.writeFieldStop();
        protocol.writeStructEnd();
    }
}
