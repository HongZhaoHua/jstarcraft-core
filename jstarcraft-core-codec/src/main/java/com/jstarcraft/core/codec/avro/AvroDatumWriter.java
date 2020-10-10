package com.jstarcraft.core.codec.avro;

import java.io.IOException;
import java.util.Collection;

import org.apache.avro.AvroRuntimeException;
import org.apache.avro.Schema;
import org.apache.avro.io.Encoder;
import org.apache.avro.reflect.ReflectDatumWriter;

public class AvroDatumWriter<T> extends ReflectDatumWriter<T> {

    public AvroDatumWriter(Schema root, AvroData reflectData) {
        super(root, reflectData);
    }

    // TODO 此处重写是为了修复官方JsonEncoder无法处理数组的Bug
    @Override
    protected void writeArray(Schema schema, Object datum, Encoder out) throws IOException {
        if (datum instanceof Collection) {
            super.writeArray(schema, datum, out);
            return;
        }
        Class<?> elementClass = datum.getClass().getComponentType();
        if (null == elementClass) {
            throw new AvroRuntimeException("Array data must be a Collection or Array");
        }
        Schema element = schema.getElementType();
        if (elementClass.isPrimitive()) {
            super.writeArray(schema, datum, out);
        } else {
            out.writeArrayStart();
            writeObjectArray(element, (Object[]) datum, out);
            out.writeArrayEnd();
        }
    }

    private void writeObjectArray(Schema element, Object[] data, Encoder out) throws IOException {
        int size = data.length;
        out.setItemCount(size);
        for (Object datum : data) {
            out.startItem();
            this.write(element, datum, out);
        }
    }

}
