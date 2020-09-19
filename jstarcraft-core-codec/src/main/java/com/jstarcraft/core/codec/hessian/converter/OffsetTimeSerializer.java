package com.jstarcraft.core.codec.hessian.converter;

import java.io.IOException;
import java.time.OffsetTime;

import com.caucho.hessian.io.AbstractHessianOutput;
import com.caucho.hessian.io.AbstractSerializer;

public class OffsetTimeSerializer extends AbstractSerializer {

    @Override
    public void writeObject(Object object, AbstractHessianOutput out) throws IOException {
        if (object == null) {
            out.writeNull();
        } else if (out.addRef(object)) {
            return;
        } else {
            OffsetTime time = (OffsetTime) object;
            int hour = time.getHour();
            int minute = time.getMinute();
            int second = time.getSecond();
            int zone = time.getOffset().getTotalSeconds();
            int reference = out.writeObjectBegin(OffsetTime.class.getName());
            if (reference < -1) {
                out.writeString("data");
                out.writeInt(hour);
                out.writeInt(minute);
                out.writeInt(second);
                out.writeInt(zone);
                out.writeMapEnd();
            } else {
                if (reference == -1) {
                    out.writeInt(1);
                    out.writeString("data");
                    out.writeObjectBegin(OffsetTime.class.getName());
                }
                out.writeInt(hour);
                out.writeInt(minute);
                out.writeInt(second);
                out.writeInt(zone);
            }
        }
    }

}
