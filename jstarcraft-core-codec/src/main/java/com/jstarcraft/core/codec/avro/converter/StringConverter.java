package com.jstarcraft.core.codec.avro.converter;

import com.jstarcraft.core.codec.avro.AvroReader;
import com.jstarcraft.core.codec.avro.AvroWriter;
import com.jstarcraft.core.utility.StringUtility;
import java.lang.reflect.Type;

public class StringConverter extends AvroConverter<Object>{


    @Override
    protected Object readValue(AvroReader avroReader, Object input, Type type) throws Exception {

        String value = String.valueOf(input);
        if (StringUtility.isEmpty(value)) {
            return null;
        }
        if (type == char.class || type == Character.class) {
            return value.charAt(0);
        } else {
            return value;
        }
    }

    @Override
    protected Object writeValue(AvroWriter writer, Object value, Type type) throws Exception {
        if (value == null) {
            return StringUtility.EMPTY;
        }
        String element;
        if (type == char.class || type == Character.class) {
            element = String.valueOf(value);
        } else {
            element = (String) value;
        }
        return element;
    }
}
