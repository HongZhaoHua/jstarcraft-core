package com.jstarcraft.core.codec.hessian.converter;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import com.caucho.hessian.io.AbstractDeserializer;
import com.caucho.hessian.io.AbstractHessianInput;

public class ZonedDateTimeDeserializer extends AbstractDeserializer {

    public Class<?> getType() {
        return ZonedDateTime.class;
    }

    @Override
    public Object readObject(AbstractHessianInput in, Object[] fields) throws IOException {
        String[] names = (String[]) fields;
        int reference = in.addRef(null);
        long instant = 0L;
        String zone = null;
        for (int index = 0; index < names.length; index++) {
            if ("data".equals(names[index])) {
                instant = in.readUTCDate();
                zone = in.readString();
            } else {
                in.readObject();
            }
        }
        ZonedDateTime dateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(instant), ZoneId.of(zone));
        in.setRef(reference, dateTime);
        return dateTime;
    }

}
