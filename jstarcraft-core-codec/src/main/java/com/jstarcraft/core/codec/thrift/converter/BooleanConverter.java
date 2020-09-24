package com.jstarcraft.core.codec.thrift.converter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TProtocol;

import com.jstarcraft.core.codec.specification.ClassDefinition;

/**
 * 布尔转换器
 * 
 * @author Birdy
 *
 */
public class BooleanConverter extends ThriftConverter<Object> {

    @Override
    public Object readValueFrom(ThriftContext context, Type type, ClassDefinition definition) throws IOException, TException {
        TProtocol protocol = context.getProtocol();
        boolean value = protocol.readBool();
        if (type == AtomicBoolean.class) {
            return new AtomicBoolean(value);
        }
        return value;
    }

    @Override
    public void writeValueTo(ThriftContext context, Type type, ClassDefinition definition, Object value) throws IOException, TException {
        TProtocol protocol = context.getProtocol();
        protocol.writeBool(Boolean.valueOf(String.valueOf(value)));
    }

}
