package com.jstarcraft.core.codec.hessian.converter;

import java.io.IOException;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import com.caucho.hessian.io.AbstractDeserializer;
import com.caucho.hessian.io.AbstractHessianInput;

public class OffsetDateTimeDeserializer extends AbstractDeserializer {

    public Class<?> getType() {
        return OffsetDateTime.class;
    }

    @Override
    public Object readObject(AbstractHessianInput in, Object[] fields) throws IOException {
        String[] names = (String[]) fields;
        int reference = in.addRef(null);
        long instant = 0L;
        int zone = 0;
        for (int index = 0; index < names.length; index++) {
            if ("data".equals(names[index])) {
                instant = in.readUTCDate();
                zone = in.readInt();
            } else {
                in.readObject();
            }
        }
        OffsetDateTime dateTime = OffsetDateTime.ofInstant(Instant.ofEpochMilli(instant), ZoneOffset.ofTotalSeconds(zone));
        in.setRef(reference, dateTime);
        return dateTime;
    }

}
