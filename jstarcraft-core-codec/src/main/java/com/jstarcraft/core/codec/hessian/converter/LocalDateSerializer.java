package com.jstarcraft.core.codec.hessian.converter;

import java.io.IOException;
import java.time.LocalDate;

import com.caucho.hessian.io.AbstractHessianOutput;
import com.caucho.hessian.io.AbstractSerializer;

public class LocalDateSerializer extends AbstractSerializer {

    @Override
    public void writeObject(Object object, AbstractHessianOutput out) throws IOException {
        if (object == null) {
            out.writeNull();
        } else if (out.addRef(object)) {
            return;
        } else {
            LocalDate date = (LocalDate) object;
            int year = date.getYear();
            int month = date.getMonthValue();
            int day = date.getDayOfMonth();
            int reference = out.writeObjectBegin(LocalDate.class.getName());
            if (reference < -1) {
                out.writeString("data");
                out.writeInt(year);
                out.writeInt(month);
                out.writeInt(day);
                out.writeMapEnd();
            } else {
                if (reference == -1) {
                    out.writeInt(1);
                    out.writeString("data");
                    out.writeObjectBegin(LocalDate.class.getName());
                }
                out.writeInt(year);
                out.writeInt(month);
                out.writeInt(day);
            }
        }
    }

}
