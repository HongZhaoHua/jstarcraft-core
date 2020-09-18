package com.jstarcraft.core.codec.hessian.converter;

import java.io.IOException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import com.caucho.hessian.io.AbstractHessianOutput;
import com.caucho.hessian.io.AbstractSerializer;

public class ZonedDateTimeSerializer extends AbstractSerializer {

    @Override
    public void writeObject(Object object, AbstractHessianOutput out) throws IOException {
        if (object == null) {
            out.writeNull();
        } else if (out.addRef(object)) {
            return;
        } else {
            ZonedDateTime dateTime = (ZonedDateTime) object;
            long instant = dateTime.toInstant().toEpochMilli();
            String zone = dateTime.getZone().getId();
            int reference = out.writeObjectBegin(ZonedDateTime.class.getName());
            if (reference < -1) {
                out.writeString("data");
                out.writeUTCDate(instant);
                out.writeString(zone);
                out.writeMapEnd();
            } else {
                if (reference == -1) {
                    out.writeInt(1);
                    out.writeString("data");
                    out.writeObjectBegin(ZonedDateTime.class.getName());
                }
                out.writeUTCDate(instant);
                out.writeString(zone);
            }
        }
    }

}
