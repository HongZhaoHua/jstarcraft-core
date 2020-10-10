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
@Deprecated
public class NumberConverter extends AvroConverter<Number> {

    @Override
    protected Number readValue(AvroReader context, Object record, Type type) throws Exception {
        if (type == BigInteger.class) {
            return new BigInteger(String.valueOf(record));
        } else if (type == BigDecimal.class) {
            return new BigDecimal(String.valueOf(record));
        }
        if (record == null) {
            return null;
        }
        return NumberUtility.convert(String.valueOf(record), (Class<? extends Number>) type);
    }

    @Override
    protected Object writeValue(AvroWriter context, Number instance, Type type) throws Exception {
        return getInputValue(instance, type);
    }

    private Object getInputValue(Number instance, Type type) {
        if (type == BigInteger.class) {
            return instance.toString();
        }
        return instance;
    }

}
