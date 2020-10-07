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

    protected static final TField NULL_MARK = new TField(StringUtility.EMPTY, TType.BYTE, (short) 1);

    @Override
    public Object readValueFrom(ThriftContext context, Type type, ClassDefinition definition) throws Exception {
        TProtocol protocol = context.getProtocol();
        protocol.readStructBegin();
        Object instance;
        TField feild = protocol.readFieldBegin();
        if (NULL_MARK.equals(feild)) {
            instance = null;
        } else {
            try {
                instance = definition.getInstance();
            } catch (Exception exception) {
                String message = StringUtility.format("获取类型[{}]实例异常", definition.getName());
                throw new CodecConvertionException(message, exception);
            }
        }
        PropertyDefinition[] properties = definition.getProperties();
        while (true) {
            if (feild.type == TType.STOP) {
                break;
            }
            if (feild.id != 1) {
                PropertyDefinition property = properties[feild.id - 2];
                ThriftConverter converter = context.getThriftConverter(property.getSpecification());
                definition = context.getClassDefinition(property.getCode());
                Object value = converter.readValueFrom(context, property.getType(), definition);
                try {
                    property.setValue(instance, value);
                } catch (Exception exception) {
                    String message = StringUtility.format("赋值[{}]实例属性[{}]异常", definition.getName(), property.getName());
                    throw new CodecConvertionException(message, exception);
                }
            }
            protocol.readFieldEnd();
            feild = protocol.readFieldBegin();
        }
        protocol.readStructEnd();
        return instance;
    }

    @Override
    public void writeValueTo(ThriftContext context, Type type, ClassDefinition definition, Object instance) throws Exception {
        TProtocol protocol = context.getProtocol();
        protocol.writeStructBegin(new TStruct(definition.getName()));
        if (instance == null) {
            protocol.writeFieldBegin(NULL_MARK);
            protocol.writeFieldEnd();
        } else {
            PropertyDefinition[] properties = definition.getProperties();
            for (int index = 0; index < properties.length; index++) {
                PropertyDefinition property = properties[index];
                Object value;
                try {
                    value = property.getValue(instance);
                    if (value != null) {
                        ThriftConverter converter = context.getThriftConverter(property.getSpecification());
                        TField field = new TField(property.getName(), TType.STRUCT, (short) (index + 2));
                        protocol.writeFieldBegin(field);
                        definition = context.getClassDefinition(property.getCode());
                        converter.writeValueTo(context, property.getType(), definition, value);
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
