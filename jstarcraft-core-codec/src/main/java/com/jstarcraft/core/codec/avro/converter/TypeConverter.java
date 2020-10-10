package com.jstarcraft.core.codec.avro.converter;

import java.lang.reflect.Type;

import com.jstarcraft.core.codec.avro.AvroReader;
import com.jstarcraft.core.codec.avro.AvroWriter;
import com.jstarcraft.core.common.reflection.TypeUtility;

/**
 * 类型转换器
 * 
 * @author Yue Zhen Wei
 *
 */
@Deprecated
public class TypeConverter extends AvroConverter<Type> {

    @Override
    protected Type readValue(AvroReader context, Object record, Type type) throws Exception {
        return TypeUtility.string2Type(record.toString());
    }

    @Override
    protected Object writeValue(AvroWriter context, Type instance, Type type) throws Exception {
        return TypeUtility.type2String(instance);
    }
}
