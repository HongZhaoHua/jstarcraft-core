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
@Deprecated
public class InstantConverter extends AvroConverter<Object> {

    @Override
    protected Object readValue(AvroReader context, Object record, Type type) throws Exception {
        Class clazz = (Class) type;
        if (Date.class.isAssignableFrom(clazz)) {
            return new Date((Long) record);
        } else if (Instant.class.isAssignableFrom(clazz)) {
            return Instant.ofEpochMilli((Long) record);
        } else {
            throw new CodecConvertionException();
        }
    }

    @Override
    protected Object writeValue(AvroWriter context, Object instance, Type type) throws Exception {
        long record;
        if (instance instanceof Date) {
            Date date = (Date) instance;
            record = date.getTime();
        } else if (instance instanceof Instant) {
            Instant instant = (Instant) instance;
            record = instant.toEpochMilli();
        } else {
            throw new CodecConvertionException();
        }
        return record;
    }
}
