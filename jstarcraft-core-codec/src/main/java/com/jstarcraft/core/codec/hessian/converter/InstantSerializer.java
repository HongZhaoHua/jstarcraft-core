package com.jstarcraft.core.codec.hessian.converter;

import java.io.IOException;
import java.time.Instant;

import com.caucho.hessian.io.AbstractHessianOutput;
import com.caucho.hessian.io.AbstractSerializer;

public class InstantSerializer extends AbstractSerializer {

    @Override
    public void writeObject(Object object, AbstractHessianOutput out) throws IOException {
        Instant instant = (Instant) object;

        if (instant == null) {
            out.writeNull();
        } else if (out.addRef(object)) {
            return;
        } else {
            int reference = out.writeObjectBegin(Instant.class.getName());

            if (reference < -1) {
                out.writeString("data");
                out.writeUTCDate(instant.toEpochMilli());
                out.writeMapEnd();
            } else {
                if (reference == -1) {
                    out.writeInt(1);
                    out.writeString("data");
                    out.writeObjectBegin(Instant.class.getName());
                }

                out.writeUTCDate(instant.toEpochMilli());
            }
        }
    }

}
