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
public class EnumerationConverter extends AvroConverter<Object> {

    @Override
    protected Object readValue(AvroReader avroReader, Object input, Type type) throws Exception {
        Class clazz = (Class<?>) type;
        return Enum.valueOf(clazz, String.valueOf(input));
    }

    @Override
    protected Object writeValue(AvroWriter writer, Object value, Type type) throws Exception {
//        return new GenericData.EnumSymbol(super.getSchema(type), value);
        return value;
    }
}
