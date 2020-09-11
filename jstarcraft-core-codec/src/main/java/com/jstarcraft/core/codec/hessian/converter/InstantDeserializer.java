package com.jstarcraft.core.codec.hessian.converter;

import java.io.IOException;
import java.time.Instant;

import com.caucho.hessian.io.AbstractDeserializer;
import com.caucho.hessian.io.AbstractHessianInput;

public class InstantDeserializer extends AbstractDeserializer {

    public Class<?> getType() {
        return Instant.class;
    }

    @Override
    public Object readObject(AbstractHessianInput in, Object[] fields) throws IOException {
        String[] fieldNames = (String[]) fields;
        int reference = in.addRef(null);
        long instant = 0L;
        for (int index = 0; index < fieldNames.length; index++) {
            if ("data".equals(fieldNames[index])) {
                instant = in.readUTCDate();
            } else {
                in.readObject();
            }
        }
        Object value = Instant.ofEpochMilli(instant);
        in.setRef(reference, value);
        return value;
    }

}
