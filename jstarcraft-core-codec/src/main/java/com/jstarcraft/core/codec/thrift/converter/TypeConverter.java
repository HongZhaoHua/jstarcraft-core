package com.jstarcraft.core.codec.thrift.converter;

import java.lang.reflect.Type;

import org.apache.thrift.protocol.TField;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TStruct;
import org.apache.thrift.protocol.TType;

import com.jstarcraft.core.codec.specification.ClassDefinition;
import com.jstarcraft.core.common.reflection.TypeUtility;
import com.jstarcraft.core.utility.StringUtility;

/**
 * 类型转换器
 * 
 * @author Birdy
 *
 */
public class TypeConverter extends ThriftConverter<Type> {

    protected static final TField NULL_MARK = new TField(StringUtility.EMPTY, TType.BYTE, (short) 1);

    @Override
    public Type readValueFrom(ThriftContext context, Type type, ClassDefinition definition) throws Exception {
        TProtocol protocol = context.getProtocol();
        protocol.readStructBegin();
        Type instance;
        TField feild = protocol.readFieldBegin();
        if (NULL_MARK.equals(feild)) {
            instance = null;
        } else {
            instance = TypeUtility.string2Type(protocol.readString());
        }
        protocol.readFieldEnd();
        protocol.readFieldBegin();
        protocol.readStructEnd();
        return instance;
    }

    @Override
    public void writeValueTo(ThriftContext context, Type type, ClassDefinition definition, Type instance) throws Exception {
        TProtocol protocol = context.getProtocol();
        protocol.writeStructBegin(new TStruct(definition.getName()));
        if (instance == null) {
            protocol.writeFieldBegin(NULL_MARK);
            protocol.writeFieldEnd();
        } else {
            protocol.writeFieldBegin(new TField(StringUtility.EMPTY, TType.STRING, (short) 2));
            protocol.writeString(TypeUtility.type2String(instance));
            protocol.writeFieldEnd();
        }
        protocol.writeFieldStop();
        protocol.writeStructEnd();
    }

}
