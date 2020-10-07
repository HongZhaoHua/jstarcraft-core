package com.jstarcraft.core.codec.avro.converter;

import java.lang.reflect.Type;
import java.util.concurrent.atomic.AtomicBoolean;

import com.jstarcraft.core.codec.avro.AvroReader;
import com.jstarcraft.core.codec.avro.AvroWriter;

/**
 * 布尔转换器
 * 
 * @author Yue Zhen Wei
 *
 */
public class BooleanConverter extends AvroConverter<Object> {

    @Override
    protected Object readValue(AvroReader avroReader, Object input, Type type) throws Exception {
        if (type == AtomicBoolean.class) {
            return new AtomicBoolean((Boolean) input);
        } else {
            return input;
        }
    }

    @Override
    protected Object writeValue(AvroWriter writer, Object value, Type type) throws Exception {
        Class clazz = (Class) type;
        Boolean input;
        if (AtomicBoolean.class.isAssignableFrom(clazz)) {
            AtomicBoolean atomicBoolean = (AtomicBoolean) value;
            input = atomicBoolean.get();
        } else {
            input = (Boolean) value;
        }
        return input;
    }

}
