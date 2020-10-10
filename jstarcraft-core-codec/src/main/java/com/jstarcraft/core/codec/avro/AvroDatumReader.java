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

    public AvroDatumReader(Schema schema, AvroData utility) {
        super(schema, schema, utility);
    }

    // TODO 此处重写是为了修复官方JsonDecoder无法处理数组的Bug
    @Override
    protected Object readArray(Object datum, Schema schema, ResolvingDecoder in) throws IOException {
        Object array = newArray(datum, 0, schema);
        if (array instanceof Collection) {
            return super.readArray(datum, schema, in);
        } else if (array instanceof Map) {
            return super.readArray(datum, schema, in);
        } else {
            Class<?> clazz = array.getClass().getComponentType();
            if (clazz.isPrimitive()) {
                return super.readArray(datum, schema, in);
            } else {
                schema = schema.getElementType();
                long length = in.readArrayStart();
                ArrayList datums = new ArrayList();
                readCollection(datums, schema, length, in);
                return datums.toArray((Object[]) Array.newInstance(clazz, datums.size()));
            }
        }
    }

    private Object readCollection(Collection<Object> datums, Schema schema, long length, ResolvingDecoder in) throws IOException {
        LogicalType logicalType = schema.getLogicalType();
        Conversion<?> conversion = getData().getConversionFor(logicalType);
        if (logicalType != null && conversion != null) {
            do {
                for (int index = 0; index < length; index++) {
                    Object datum = readWithConversion(null, schema, logicalType, conversion, in);
                    datums.add(datum);
                }
            } while ((length = in.arrayNext()) > 0);
        } else {
            do {
                for (int index = 0; index < length; index++) {
                    Object datum = readWithoutConversion(null, schema, in);
                    datums.add(datum);
                }
            } while ((length = in.arrayNext()) > 0);
        }
        return datums;
    }

}
