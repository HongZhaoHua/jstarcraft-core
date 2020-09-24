package com.jstarcraft.core.codec.thrift.converter;

import java.lang.reflect.Type;

import org.apache.thrift.protocol.TField;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TStruct;
import org.apache.thrift.protocol.TType;

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

    @Override
    public Object readValueFrom(ThriftContext context, Type type, ClassDefinition definition) throws Exception {
        TProtocol protocol = context.getProtocol();
        protocol.readStructBegin();
        protocol.readFieldBegin();
        boolean nullable = protocol.readBool();
        protocol.readFieldEnd();
        Object object = null;
        if (!nullable) {
            try {
                object = definition.getInstance();
            } catch (Exception exception) {
                String message = StringUtility.format("获取类型[{}]实例异常", definition.getName());
                throw new CodecConvertionException(message, exception);
            }
            PropertyDefinition[] properties = definition.getProperties();
            while (true) {
                TField field = protocol.readFieldBegin();
                if (field.type == org.apache.thrift.protocol.TType.STOP) {
                    break;
                }
                PropertyDefinition property = properties[field.id - 2];
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
        }
        protocol.readStructEnd();
        return object;
    }

    @Override
    public void writeValueTo(ThriftContext context, Type type, ClassDefinition definition, Object value) throws Exception {
        TProtocol protocol = context.getProtocol();
        boolean nullable = value == null;
        protocol.writeStructBegin(new TStruct(definition.getName()));
        protocol.writeFieldBegin(new TField("_nullable", TType.BOOL, (short) (1)));
        protocol.writeBool(nullable);
        protocol.writeFieldEnd();
        if (!nullable) {
            PropertyDefinition[] properties = definition.getProperties();
            for (int index = 0; index < properties.length; index++) {
                PropertyDefinition property = properties[index];
                Object object;
                try {
                    object = property.getValue(value);
                    if (object != null) {
                        protocol.writeFieldBegin(new TField(property.getName(), context.getThriftType(property.getType()), (short) (index + 2)));
                        ThriftConverter converter = context.getProtocolConverter(property.getSpecification());
                        definition = context.getClassDefinition(property.getCode());
                        converter.writeValueTo(context, property.getType(), definition, object);
                        protocol.writeFieldEnd();
                    }
                } catch (Exception exception) {
                    String message = StringUtility.format("取值[{}]实例属性[{}]异常", definition.getName(), property.getName());
                    throw new CodecConvertionException(message, exception);
                }
            }
        }
        protocol.writeFieldStop();
        protocol.writeStructEnd();
    }
}
