package com.jstarcraft.core.codec.hessian.converter;

import java.io.IOException;
import java.time.LocalTime;

import com.caucho.hessian.io.AbstractHessianOutput;
import com.caucho.hessian.io.AbstractSerializer;

public class LocalTimeSerializer extends AbstractSerializer {

    @Override
    public void writeObject(Object object, AbstractHessianOutput out) throws IOException {
        if (object == null) {
            out.writeNull();
        } else if (out.addRef(object)) {
            return;
        } else {
            LocalTime yearMonth = (LocalTime) object;
            int hour = yearMonth.getHour();
            int minute = yearMonth.getMinute();
            int second = yearMonth.getSecond();
            int reference = out.writeObjectBegin(LocalTime.class.getName());
            if (reference < -1) {
                out.writeString("data");
                out.writeInt(hour);
                out.writeInt(minute);
                out.writeInt(second);
                out.writeMapEnd();
            } else {
                if (reference == -1) {
                    out.writeInt(1);
                    out.writeString("data");
                    out.writeObjectBegin(LocalTime.class.getName());
                }
                out.writeInt(hour);
                out.writeInt(minute);
                out.writeInt(second);
            }
        }
    }

}
