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
        String[] names = (String[]) fields;
        int reference = in.addRef(null);
        long data = 0L;
        for (int index = 0; index < names.length; index++) {
            if ("data".equals(names[index])) {
                data = in.readUTCDate();
            } else {
                in.readObject();
            }
        }
        Instant instant = Instant.ofEpochMilli(data);
        in.setRef(reference, instant);
        return instant;
    }

}
