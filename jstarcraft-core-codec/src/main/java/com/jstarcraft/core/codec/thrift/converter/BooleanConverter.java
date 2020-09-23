package com.jstarcraft.core.codec.thrift.converter;

import com.jstarcraft.core.codec.specification.ClassDefinition;
import com.jstarcraft.core.codec.thrift.ThriftReader;
import com.jstarcraft.core.codec.thrift.ThriftWriter;
import org.apache.thrift.TException;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 布尔转换器
 * 
 * @author Birdy
 *
 */
public class BooleanConverter extends ProtocolConverter<Object> {

    @Override
    public Object readValueFrom(ThriftReader context, Type type, ClassDefinition definition) throws IOException, TException {
        boolean value=protocol.readBool();
        if(type== AtomicBoolean.class){
            return new AtomicBoolean(value);
        }
        return value;
    }

    @Override
    public void writeValueTo(ThriftWriter context, Type type, ClassDefinition definition, Object value) throws IOException, TException {
        protocol.writeBool(Boolean.valueOf(String.valueOf(value)));
    }

}
