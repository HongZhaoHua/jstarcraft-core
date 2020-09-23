package com.jstarcraft.core.codec.thrift.converter;

import com.jstarcraft.core.codec.specification.ClassDefinition;
import com.jstarcraft.core.codec.thrift.ThriftReader;
import com.jstarcraft.core.codec.thrift.ThriftWriter;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * Void转换器
 * 
 * @author Birdy
 *
 */
public class VoidConverter extends ProtocolConverter<Object> {

    @Override
    public Object readValueFrom(ThriftReader context, Type type, ClassDefinition definition) throws IOException {
        return null;
    }

    @Override
    public void writeValueTo(ThriftWriter context, Type type, ClassDefinition definition, Object value) throws IOException {

    }

}
