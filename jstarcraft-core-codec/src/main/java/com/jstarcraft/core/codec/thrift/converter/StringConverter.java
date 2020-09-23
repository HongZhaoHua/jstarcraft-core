package com.jstarcraft.core.codec.thrift.converter;

import com.jstarcraft.core.codec.specification.ClassDefinition;
import com.jstarcraft.core.codec.thrift.ThriftReader;
import com.jstarcraft.core.codec.thrift.ThriftWriter;
import org.apache.thrift.TException;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Objects;

/**
 * 字符串转换器
 * 
 * @author Birdy
 *
 */
public class StringConverter extends ProtocolConverter<Object> {

    /**
     * 空标记
     */
    private static final byte NULL=1;
    /**
     * 非空标记
     */
    private static final byte NOT_NULL=0;

    @Override
    public Object readValueFrom(ThriftReader context, Type type, ClassDefinition definition) throws IOException, TException {
        byte nil=protocol.readByte();
        String value =protocol.readString();
        if(nil==NULL){
            return null;
        }
        if (type == char.class || type == Character.class) {
            return value.charAt(0);
        } else {
            return value;
        }
    }

    @Override
    public void writeValueTo(ThriftWriter context, Type type, ClassDefinition definition, Object value) throws IOException, TException {
        if(value==null){
            protocol.writeByte(NULL);
        }else{
            protocol.writeByte(NOT_NULL);
        }
        protocol.writeString(String.valueOf(value));
    }

}
