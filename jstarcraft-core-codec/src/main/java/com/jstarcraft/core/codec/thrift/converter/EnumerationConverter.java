package com.jstarcraft.core.codec.thrift.converter;

import com.jstarcraft.core.codec.specification.ClassDefinition;
import com.jstarcraft.core.codec.thrift.ThriftReader;
import com.jstarcraft.core.codec.thrift.ThriftWriter;
import org.apache.thrift.TException;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * 枚举转换器
 * 
 * @author Birdy
 *
 */
public class EnumerationConverter extends ProtocolConverter<Object> {

    /**
     * 空标记
     */
    private static final byte NULL=0;

    @Override
    public Object readValueFrom(ThriftReader context, Type type, ClassDefinition definition) throws IOException, TException {
        int value=protocol.readI32();
        if(value==NULL){
            return null;
        }
        Class<?> clazz = definition.getType();
        return clazz.getEnumConstants()[value - 1];
    }

    @Override
    public void writeValueTo(ThriftWriter context, Type type, ClassDefinition definition, Object value) throws IOException, TException {
        if(value==null){
            protocol.writeI32(NULL);
            return;
        }
        Enum<?> enumeration = (Enum<?>) value;
        int ordinal = enumeration.ordinal() + 1;
        protocol.writeI32(ordinal);
    }

}
