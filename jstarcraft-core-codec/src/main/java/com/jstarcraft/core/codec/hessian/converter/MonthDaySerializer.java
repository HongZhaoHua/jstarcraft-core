package com.jstarcraft.core.codec.hessian.converter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.MonthDay;

import com.caucho.hessian.io.AbstractHessianOutput;
import com.caucho.hessian.io.AbstractSerializer;

public class MonthDaySerializer extends AbstractSerializer {

    @Override
    public void writeObject(Object object, AbstractHessianOutput out) throws IOException {
        if (object == null) {
            out.writeNull();
        } else if (out.addRef(object)) {
            return;
        } else {
            MonthDay monthDay = (MonthDay) object;
            int month = monthDay.getMonthValue();
            int day = monthDay.getDayOfMonth();
            int reference = out.writeObjectBegin(LocalDateTime.class.getName());
            if (reference < -1) {
                out.writeString("data");
                out.writeInt(month);
                out.writeInt(day);
                out.writeMapEnd();
            } else {
                if (reference == -1) {
                    out.writeInt(1);
                    out.writeString("data");
                    out.writeObjectBegin(LocalDateTime.class.getName());
                }
                out.writeInt(month);
                out.writeInt(day);
            }
        }
    }

}
