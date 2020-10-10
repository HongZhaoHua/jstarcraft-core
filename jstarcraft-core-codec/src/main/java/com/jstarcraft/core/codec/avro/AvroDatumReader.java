package com.jstarcraft.core.codec.avro;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.apache.avro.Conversion;
import org.apache.avro.LogicalType;
import org.apache.avro.Schema;
import org.apache.avro.io.ResolvingDecoder;
import org.apache.avro.reflect.ReflectDatumReader;

public class AvroDatumReader<T> extends ReflectDatumReader<T> {

    public AvroDatumReader(Schema schema, AvroData data) {
        super(schema, schema, data);
    }

    // TODO 此处重写是为了修复官方JsonDecoder无法处理数组的Bug
    @Override
    protected Object readArray(Object old, Schema expected, ResolvingDecoder in) throws IOException {
        Object array = newArray(old, 0, expected);
        if (array instanceof Collection) {
            return super.readArray(old, expected, in);
        } else if (array instanceof Map) {
            return super.readArray(old, expected, in);
        } else {
            Class<?> elementType = array.getClass().getComponentType();
            if (elementType.isPrimitive()) {
                return super.readArray(old, expected, in);
            } else {
                Schema expectedType = expected.getElementType();
                long l = in.readArrayStart();
                ArrayList list = new ArrayList<>();
                readCollection(list, expectedType, l, in);
                return list.toArray((Object[]) Array.newInstance(elementType, list.size()));
            }
        }
    }

    private Object readCollection(Collection<Object> collection, Schema expectedType, long length, ResolvingDecoder in) throws IOException {
        LogicalType logicalType = expectedType.getLogicalType();
        Conversion<?> conversion = getData().getConversionFor(logicalType);
        if (logicalType != null && conversion != null) {
            do {
                for (int index = 0; index < length; index++) {
                    Object element = readWithConversion(null, expectedType, logicalType, conversion, in);
                    collection.add(element);
                }
            } while ((length = in.arrayNext()) > 0);
        } else {
            do {
                for (int index = 0; index < length; index++) {
                    Object element = readWithoutConversion(null, expectedType, in);
                    collection.add(element);
                }
            } while ((length = in.arrayNext()) > 0);
        }
        return collection;
    }

}
