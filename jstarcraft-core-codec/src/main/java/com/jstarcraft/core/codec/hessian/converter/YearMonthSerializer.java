package com.jstarcraft.core.codec.hessian.converter;

import java.io.IOException;
import java.time.YearMonth;

import com.caucho.hessian.io.AbstractHessianOutput;
import com.caucho.hessian.io.AbstractSerializer;

public class YearMonthSerializer extends AbstractSerializer {

    @Override
    public void writeObject(Object object, AbstractHessianOutput out) throws IOException {
        if (object == null) {
            out.writeNull();
        } else if (out.addRef(object)) {
            return;
        } else {
            YearMonth yearMonth = (YearMonth) object;
            int year = yearMonth.getYear();
            int month = yearMonth.getMonthValue();
            int reference = out.writeObjectBegin(YearMonth.class.getName());
            if (reference < -1) {
                out.writeString("data");
                out.writeInt(year);
                out.writeInt(month);
                out.writeMapEnd();
            } else {
                if (reference == -1) {
                    out.writeInt(1);
                    out.writeString("data");
                    out.writeObjectBegin(YearMonth.class.getName());
                }
                out.writeInt(year);
                out.writeInt(month);
            }
        }
    }

}
