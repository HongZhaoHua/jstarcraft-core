package com.jstarcraft.core.codec.avro.converter;

import java.lang.reflect.Type;
import java.time.Instant;
import java.util.Date;

import com.jstarcraft.core.codec.avro.AvroReader;
import com.jstarcraft.core.codec.avro.AvroWriter;
import com.jstarcraft.core.codec.exception.CodecConvertionException;

/**
 * 日期时间转换器
 * 
 * @author Yue Zhen Wei
 *
 */
public class InstantConverter extends AvroConverter<Object> {

    @Override
    protected Object readValue(AvroReader avroReader, Object input, Type type) throws Exception {
        Class clazz = (Class) type;
        if (Date.class.isAssignableFrom(clazz)) {
            return new Date((Long) input);
        } else if (Instant.class.isAssignableFrom(clazz)) {
            return Instant.ofEpochMilli((Long) input);
        } else {
            throw new CodecConvertionException();
        }
    }

    @Override
    protected Object writeValue(AvroWriter writer, Object value, Type type) throws Exception {
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
        return time;
    }
}
