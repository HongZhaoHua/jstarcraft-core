package com.jstarcraft.core.codec.thrift.converter;

import java.lang.reflect.Type;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.thrift.protocol.TField;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TStruct;
import org.apache.thrift.protocol.TType;

import com.jstarcraft.core.codec.specification.ClassDefinition;
import com.jstarcraft.core.utility.StringUtility;

/**
 * 布尔转换器
 * 
 * @author Birdy
 *
 */
public class BooleanConverter extends ThriftConverter<Object> {

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
            if (type == AtomicBoolean.class) {
                instance = new AtomicBoolean(protocol.readBool());
            } else {
                instance = protocol.readBool();
            }
        }
        protocol.readFieldEnd();
        protocol.readFieldBegin();
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
            protocol.writeFieldBegin(new TField(StringUtility.EMPTY, TType.BOOL, (short) 2));
            if (type == AtomicBoolean.class) {
                protocol.writeBool(((AtomicBoolean) instance).get());
            } else {
                protocol.writeBool(((Boolean) instance));
            }
            protocol.writeFieldEnd();
        }
        protocol.writeFieldStop();
        protocol.writeStructEnd();
    }

}
