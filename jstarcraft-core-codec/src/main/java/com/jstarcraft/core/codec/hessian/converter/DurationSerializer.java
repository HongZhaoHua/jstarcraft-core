package com.jstarcraft.core.codec.hessian.converter;

import java.io.IOException;
import java.time.Duration;

import com.caucho.hessian.io.AbstractHessianOutput;
import com.caucho.hessian.io.AbstractSerializer;

public class DurationSerializer extends AbstractSerializer {

    @Override
    public void writeObject(Object object, AbstractHessianOutput out) throws IOException {
        if (object == null) {
            out.writeNull();
        } else if (out.addRef(object)) {
            return;
        } else {
            Duration duration = (Duration) object;
            long data = duration.getSeconds();
            int reference = out.writeObjectBegin(Duration.class.getName());
            if (reference < -1) {
                out.writeString("data");
                out.writeLong(data);
                out.writeMapEnd();
            } else {
                if (reference == -1) {
                    out.writeInt(1);
                    out.writeString("data");
                    out.writeObjectBegin(Duration.class.getName());
                }
                out.writeLong(data);
            }
        }
    }

}
