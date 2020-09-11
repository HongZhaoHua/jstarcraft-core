package com.jstarcraft.core.codec.hessian.converter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import com.caucho.hessian.io.AbstractHessianOutput;
import com.caucho.hessian.io.AbstractSerializer;

public class LocalDateTimeSerializer extends AbstractSerializer {

    @Override
    public void writeObject(Object object, AbstractHessianOutput out) throws IOException {
        if (object == null) {
            out.writeNull();
        } else if (out.addRef(object)) {
            return;
        } else {
            LocalDateTime dateTime = (LocalDateTime) object;
            long data = dateTime.toInstant(ZoneOffset.UTC).toEpochMilli();
            int reference = out.writeObjectBegin(LocalDateTime.class.getName());
            if (reference < -1) {
                out.writeString("data");
                out.writeUTCDate(data);
                out.writeMapEnd();
            } else {
                if (reference == -1) {
                    out.writeInt(1);
                    out.writeString("data");
                    out.writeObjectBegin(LocalDateTime.class.getName());
                }
                out.writeUTCDate(data);
            }
        }
    }

}
