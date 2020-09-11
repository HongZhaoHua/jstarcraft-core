package com.jstarcraft.core.codec.hessian.converter;

import java.io.IOException;
import java.time.Instant;

import com.caucho.hessian.io.AbstractHessianOutput;
import com.caucho.hessian.io.AbstractSerializer;

public class InstantSerializer extends AbstractSerializer {

    @Override
    public void writeObject(Object object, AbstractHessianOutput out) throws IOException {
        if (object == null) {
            out.writeNull();
        } else if (out.addRef(object)) {
            return;
        } else {
            Instant instant = (Instant) object;
            long data = instant.toEpochMilli();
            int reference = out.writeObjectBegin(Instant.class.getName());
            if (reference < -1) {
                out.writeString("data");
                out.writeUTCDate(data);
                out.writeMapEnd();
            } else {
                if (reference == -1) {
                    out.writeInt(1);
                    out.writeString("data");
                    out.writeObjectBegin(Instant.class.getName());
                }
                out.writeUTCDate(data);
            }
        }
    }

}
