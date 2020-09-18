package com.jstarcraft.core.codec.hessian.converter;

import java.io.IOException;
import java.time.ZoneOffset;

import com.caucho.hessian.io.AbstractHessianOutput;
import com.caucho.hessian.io.AbstractSerializer;

public class ZoneOffsetSerializer extends AbstractSerializer {

    @Override
    public void writeObject(Object object, AbstractHessianOutput out) throws IOException {
        if (object == null) {
            out.writeNull();
        } else if (out.addRef(object)) {
            return;
        } else {
            ZoneOffset zoneOffset = (ZoneOffset) object;
            int data = zoneOffset.getTotalSeconds();
            int reference = out.writeObjectBegin(ZoneOffset.class.getName());
            if (reference < -1) {
                out.writeString("data");
                out.writeInt(data);
                out.writeMapEnd();
            } else {
                if (reference == -1) {
                    out.writeInt(1);
                    out.writeString("data");
                    out.writeObjectBegin(ZoneOffset.class.getName());
                }
                out.writeInt(data);
            }
        }
    }

}
