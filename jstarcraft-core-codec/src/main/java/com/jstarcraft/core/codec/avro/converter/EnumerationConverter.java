package com.jstarcraft.core.codec.avro.converter;

import com.jstarcraft.core.codec.avro.AvroReader;
import com.jstarcraft.core.codec.avro.AvroWriter;
import com.jstarcraft.core.codec.exception.CodecException;
import java.lang.reflect.Type;

public class EnumerationConverter extends AvroConverter<Object>{

    @Override
    protected Object readValue(AvroReader avroReader, Object input, Type type) throws Exception {
        Class<?> clazz = (Class<?>) type;
        Enum<?> anEnum;
        for (Object enumConstant : clazz.getEnumConstants()) {
            anEnum = (Enum<?>) enumConstant;
            if (anEnum.name().equals(String.valueOf(input))) {
                return anEnum;
            }
        }

        throw new CodecException("Avro 解码失败 枚举类型不存在");
    }


    @Override
    protected Object writeValue(AvroWriter writer, Object value, Type type) throws Exception {
//        return new GenericData.EnumSymbol(super.getSchema(type), value);
        return value;
    }
}
