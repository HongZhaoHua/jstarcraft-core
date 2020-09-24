package com.jstarcraft.core.codec.thrift.converter;

import java.io.IOException;
import java.lang.reflect.Type;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TProtocol;

import com.jstarcraft.core.codec.specification.ClassDefinition;

/**
 * 枚举转换器
 * 
 * @author Birdy
 *
 */
public class EnumerationConverter extends ThriftConverter<Object> {

    @Override
    public Object readValueFrom(ThriftContext context, Type type, ClassDefinition definition) throws IOException, TException {
        TProtocol protocol = context.getProtocol();
        int ordinal = protocol.readI32();
        if (ordinal == 0) {
            return null;
        }
        Class<?> clazz = definition.getType();
        return clazz.getEnumConstants()[ordinal - 1];
    }

    @Override
    public void writeValueTo(ThriftContext context, Type type, ClassDefinition definition, Object value) throws IOException, TException {
        TProtocol protocol = context.getProtocol();
        if (value == null) {
            protocol.writeI32(0);
            return;
        }
        Enum<?> enumeration = (Enum<?>) value;
        int ordinal = enumeration.ordinal() + 1;
        protocol.writeI32(ordinal);
    }

}
