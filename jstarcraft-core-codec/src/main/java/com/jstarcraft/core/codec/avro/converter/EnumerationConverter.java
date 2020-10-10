package com.jstarcraft.core.codec.avro.converter;

import java.lang.reflect.Type;

import com.jstarcraft.core.codec.avro.AvroReader;
import com.jstarcraft.core.codec.avro.AvroWriter;

/**
 * 枚举转换器
 * 
 * @author Yue Zhen Wei
 *
 */
@Deprecated
public class EnumerationConverter extends AvroConverter<Object> {

    @Override
    protected Object readValue(AvroReader context, Object record, Type type) throws Exception {
        Class clazz = (Class<?>) type;
        return Enum.valueOf(clazz, String.valueOf(record));
    }

    @Override
    protected Object writeValue(AvroWriter context, Object instance, Type type) throws Exception {
//        return new GenericData.EnumSymbol(super.getSchema(type), value);
        return instance;
    }
}
