package com.jstarcraft.core.codec.avro.converter;

import java.lang.reflect.Type;

import com.jstarcraft.core.codec.avro.AvroReader;
import com.jstarcraft.core.codec.avro.AvroWriter;
import com.jstarcraft.core.utility.StringUtility;

/**
 * 字符串转换器
 * 
 * @author Yue Zhen Wei
 *
 */
@Deprecated
public class StringConverter extends AvroConverter<Object> {

    @Override
    protected Object readValue(AvroReader context, Object record, Type type) throws Exception {
        String instance = String.valueOf(record);
        if (StringUtility.isEmpty(instance)) {
            return null;
        }
        if (type == char.class || type == Character.class) {
            return instance.charAt(0);
        } else {
            return instance;
        }
    }

    @Override
    protected Object writeValue(AvroWriter context, Object instance, Type type) throws Exception {
        if (instance == null) {
            return StringUtility.EMPTY;
        }
        String record;
        if (type == char.class || type == Character.class) {
            record = String.valueOf(instance);
        } else {
            record = (String) instance;
        }
        return record;
    }
}
