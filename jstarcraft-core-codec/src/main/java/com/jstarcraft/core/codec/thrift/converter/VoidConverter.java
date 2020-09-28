package com.jstarcraft.core.codec.thrift.converter;

import java.io.IOException;
import java.lang.reflect.Type;

import com.jstarcraft.core.codec.specification.ClassDefinition;

/**
 * Void转换器
 * 
 * @author Birdy
 *
 */
public class VoidConverter extends ThriftConverter<Object> {

    @Override
    public Object readValueFrom(ThriftContext context, Type type, ClassDefinition definition) throws IOException {
        return null;
    }

    @Override
    public void writeValueTo(ThriftContext context, Type type, ClassDefinition definition, Object value) throws IOException {
    }

}
