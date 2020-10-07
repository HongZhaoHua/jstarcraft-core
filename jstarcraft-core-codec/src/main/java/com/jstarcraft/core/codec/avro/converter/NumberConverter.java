package com.jstarcraft.core.codec.avro.converter;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;

import com.jstarcraft.core.codec.avro.AvroReader;
import com.jstarcraft.core.codec.avro.AvroWriter;
import com.jstarcraft.core.utility.NumberUtility;

/**
 * 数值转换器
 * 
 * @author Yue Zhen Wei
 *
 */
public class NumberConverter extends AvroConverter<Number> {

    @Override
    protected Number readValue(AvroReader avroReader, Object input, Type type) throws Exception {
        if (type == BigInteger.class) {
            return new BigInteger(String.valueOf(input));
        } else if (type == BigDecimal.class) {
            return new BigDecimal(String.valueOf(input));
        }
        if (input == null) {
            return null;
        }
        return NumberUtility.convert(String.valueOf(input), (Class<? extends Number>) type);
    }

    @Override
    protected Object writeValue(AvroWriter writer, Number value, Type type) throws Exception {
        return getInputValue(value, type);
    }

    private Object getInputValue(Number value, Type type) {
        if (type == BigInteger.class) {
            return value + "";
        }

        return value;
    }

}
