package com.jstarcraft.core.codec.avro;

import java.io.IOException;
import java.util.Collection;

import org.apache.avro.AvroRuntimeException;
import org.apache.avro.Schema;
import org.apache.avro.io.Encoder;
import org.apache.avro.reflect.ReflectDatumWriter;

public class AvroDatumWriter<T> extends ReflectDatumWriter<T> {

    public AvroDatumWriter(Schema root, AvroData utility) {
        super(root, utility);
    }

    // TODO 此处重写是为了修复官方JsonEncoder无法处理数组的Bug
    @Override
    protected void writeArray(Schema schema, Object datum, Encoder out) throws IOException {
        if (datum instanceof Collection) {
            super.writeArray(schema, datum, out);
            return;
        }
        Class<?> clazz = datum.getClass().getComponentType();
        if (null == clazz) {
            throw new AvroRuntimeException("Array data must be a Collection or Array");
        }
        if (clazz.isPrimitive()) {
            super.writeArray(schema, datum, out);
        } else {
            schema = schema.getElementType();
            out.writeArrayStart();
            writeObjectArray(schema, (Object[]) datum, out);
            out.writeArrayEnd();
        }
    }

    private void writeObjectArray(Schema schema, Object[] datums, Encoder out) throws IOException {
        int size = datums.length;
        out.setItemCount(size);
        for (Object datum : datums) {
            out.startItem();
            write(schema, datum, out);
        }
    }

}
