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
        boolean mark = protocol.readBool();
        protocol.readFieldEnd();
        Object object = null;
        if (!mark) {
            try {
                object = definition.getInstance();
            } catch (Exception exception) {
                String message = StringUtility.format("获取类型[{}]实例异常", definition.getName());
                throw new CodecConvertionException(message, exception);
            }
            PropertyDefinition[] properties = definition.getProperties();
            while (true) {
                TField field = protocol.readFieldBegin();
                if (field.type == TType.STOP) {
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
        } else {
            protocol.readFieldBegin();
        }
        protocol.readStructEnd();
        return object;
    }

    @Override
    public void writeValueTo(ThriftContext context, Type type, ClassDefinition definition, Object value) throws Exception {
        TProtocol protocol = context.getProtocol();
        boolean mark = value == null;
        protocol.writeStructBegin(new TStruct(definition.getName()));
        protocol.writeFieldBegin(new TField("_mark", TType.BOOL, (short) (1)));
        protocol.writeBool(mark);
        protocol.writeFieldEnd();
        if (!mark) {
            PropertyDefinition[] properties = definition.getProperties();
            for (int index = 0; index < properties.length; index++) {
                PropertyDefinition property = properties[index];
                Object object;
                try {
                    object = property.getValue(value);
                    if (object != null) {
                        TField field = new TField(property.getName(), context.getThriftType(property.getType()), (short) (index + 2));
                        protocol.writeFieldBegin(field);
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
