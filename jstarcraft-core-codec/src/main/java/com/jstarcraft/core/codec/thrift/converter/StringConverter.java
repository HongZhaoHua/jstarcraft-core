package com.jstarcraft.core.codec.thrift.converter;

import java.io.IOException;
import java.lang.reflect.Type;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TProtocol;

import com.jstarcraft.core.codec.specification.ClassDefinition;

/**
 * 字符串转换器
 * 
 * @author Birdy
 *
 */
public class StringConverter extends ThriftConverter<Object> {

    /**
     * 空标记
     */
    private static final byte NULL = 1;
    /**
     * 非空标记
     */
    private static final byte NOT_NULL = 0;

    @Override
    public Object readValueFrom(ThriftContext context, Type type, ClassDefinition definition) throws IOException, TException {
        TProtocol protocol = context.getProtocol();
        byte nil = protocol.readByte();
        String value = protocol.readString();
        if (nil == NULL) {
            return null;
        }
        if (type == char.class || type == Character.class) {
            return value.charAt(0);
        } else {
            return value;
        }
    }

    @Override
    public void writeValueTo(ThriftContext context, Type type, ClassDefinition definition, Object value) throws IOException, TException {
        TProtocol protocol = context.getProtocol();
        if (value == null) {
            protocol.writeByte(NULL);
        } else {
            protocol.writeByte(NOT_NULL);
        }
        protocol.writeString(String.valueOf(value));
    }

}
