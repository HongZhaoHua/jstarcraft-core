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
@Deprecated
public class BooleanConverter extends AvroConverter<Object> {

    @Override
    protected Object readValue(AvroReader writer, Object record, Type type) throws Exception {
        if (type == AtomicBoolean.class) {
            return new AtomicBoolean((Boolean) record);
        } else {
            return record;
        }
    }

    @Override
    protected Object writeValue(AvroWriter writer, Object instance, Type type) throws Exception {
        Class clazz = (Class) type;
        Boolean record;
        if (AtomicBoolean.class.isAssignableFrom(clazz)) {
            AtomicBoolean atomicBoolean = (AtomicBoolean) instance;
            record = atomicBoolean.get();
        } else {
            record = (Boolean) instance;
        }
        return record;
    }

}
