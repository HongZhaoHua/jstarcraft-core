package com.jstarcraft.core.codec.hessian.converter;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import com.caucho.hessian.io.AbstractDeserializer;
import com.caucho.hessian.io.AbstractHessianInput;

public class LocalDateTimeDeserializer extends AbstractDeserializer {

    public Class<?> getType() {
        return LocalDateTime.class;
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
        LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(data), ZoneOffset.UTC);
        in.setRef(reference, dateTime);
        return dateTime;
    }

}
