package com.jstarcraft.core.codec.thrift.converter;

import com.jstarcraft.core.codec.specification.ClassDefinition;
import com.jstarcraft.core.codec.thrift.ThriftReader;
import com.jstarcraft.core.codec.thrift.ThriftWriter;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * Exception转换器
 * 
 * @author huang hong fei
 * @createAt 2020/09/15
 * @description
 *
 */
public class ServiceConverter extends ProtocolConverter<Object> {

    @Override
    public Object readValueFrom(ThriftReader context, Type type, ClassDefinition definition) throws IOException {
        return null;
    }

    @Override
    public void writeValueTo(ThriftWriter context, Type type, ClassDefinition definition, Object value) throws IOException {
    }

}
