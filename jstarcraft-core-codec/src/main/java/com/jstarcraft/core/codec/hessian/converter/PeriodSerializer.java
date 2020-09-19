package com.jstarcraft.core.codec.hessian.converter;

import java.io.IOException;
import java.time.Period;

import com.caucho.hessian.io.AbstractHessianOutput;
import com.caucho.hessian.io.AbstractSerializer;

public class PeriodSerializer extends AbstractSerializer {

    @Override
    public void writeObject(Object object, AbstractHessianOutput out) throws IOException {
        if (object == null) {
            out.writeNull();
        } else if (out.addRef(object)) {
            return;
        } else {
            Period period = (Period) object;
            int years = period.getYears();
            int months = period.getMonths();
            int days = period.getDays();
            int reference = out.writeObjectBegin(Period.class.getName());
            if (reference < -1) {
                out.writeString("data");
                out.writeInt(years);
                out.writeInt(months);
                out.writeInt(days);
                out.writeMapEnd();
            } else {
                if (reference == -1) {
                    out.writeInt(1);
                    out.writeString("data");
                    out.writeObjectBegin(Period.class.getName());
                }
                out.writeInt(years);
                out.writeInt(months);
                out.writeInt(days);
            }
        }
    }

}
