package com.jstarcraft.core.codec.thrift.converter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.Instant;
import java.util.Date;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TProtocol;

import com.jstarcraft.core.codec.exception.CodecConvertionException;
import com.jstarcraft.core.codec.specification.ClassDefinition;

/**
 * 时间转换器
 * 
 * @author Birdy
 *
 */
public class InstantConverter extends ThriftConverter<Object> {

    @Override
    public Object readValueFrom(ThriftContext context, Type type, ClassDefinition definition) throws IOException, TException {
        TProtocol protocol = context.getProtocol();
        long time = protocol.readI64();
        if (type == Date.class) {
            return new Date(time);
        } else if (type == Instant.class) {
            return Instant.ofEpochMilli(time);
        }
        throw new CodecConvertionException();
    }

    @Override
    public void writeValueTo(ThriftContext context, Type type, ClassDefinition definition, Object value) throws IOException, TException {
        TProtocol protocol = context.getProtocol();
        if (value == null) {
            protocol.writeI64(0);
            return;
        }
        long time;
        if (value instanceof Date) {
            Date date = (Date) value;
            time = date.getTime();
        } else if (value instanceof Instant) {
            Instant instant = (Instant) value;
            time = instant.toEpochMilli();
        } else {
            throw new CodecConvertionException();
        }
        protocol.writeI64(time);
    }

}
