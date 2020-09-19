package com.jstarcraft.core.codec.hessian.converter;

import java.io.IOException;
import java.time.LocalTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;

import com.caucho.hessian.io.AbstractDeserializer;
import com.caucho.hessian.io.AbstractHessianInput;

public class OffsetTimeDeserializer extends AbstractDeserializer {

    public Class<?> getType() {
        return OffsetTime.class;
    }

    @Override
    public Object readObject(AbstractHessianInput in, Object[] fields) throws IOException {
        String[] names = (String[]) fields;
        int reference = in.addRef(null);
        int hour = 0;
        int minute = 0;
        int second = 0;
        int zone = 0;
        for (int index = 0; index < names.length; index++) {
            if ("data".equals(names[index])) {
                hour = in.readInt();
                minute = in.readInt();
                second = in.readInt();
                zone = in.readInt();
            } else {
                in.readObject();
            }
        }
        OffsetTime time = OffsetTime.of(LocalTime.of(hour, minute, second), ZoneOffset.ofTotalSeconds(zone));
        in.setRef(reference, time);
        return time;
    }

}
