package com.jstarcraft.core.codec.hessian.converter;

import java.io.IOException;
import java.time.OffsetDateTime;

import com.caucho.hessian.io.AbstractHessianOutput;
import com.caucho.hessian.io.AbstractSerializer;

public class OffsetDateTimeSerializer extends AbstractSerializer {

    @Override
    public void writeObject(Object object, AbstractHessianOutput out) throws IOException {
        if (object == null) {
            out.writeNull();
        } else if (out.addRef(object)) {
            return;
        } else {
            OffsetDateTime dateTime = (OffsetDateTime) object;
            long instant = dateTime.toInstant().toEpochMilli();
            int zone = dateTime.getOffset().getTotalSeconds();
            int reference = out.writeObjectBegin(OffsetDateTime.class.getName());
            if (reference < -1) {
                out.writeString("data");
                out.writeUTCDate(instant);
                out.writeInt(zone);
                out.writeMapEnd();
            } else {
                if (reference == -1) {
                    out.writeInt(1);
                    out.writeString("data");
                    out.writeObjectBegin(OffsetDateTime.class.getName());
                }
                out.writeUTCDate(instant);
                out.writeInt(zone);
            }
        }
    }

}
