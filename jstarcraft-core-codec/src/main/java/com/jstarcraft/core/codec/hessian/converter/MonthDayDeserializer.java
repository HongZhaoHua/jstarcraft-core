package com.jstarcraft.core.codec.hessian.converter;

import java.io.IOException;
import java.time.MonthDay;

import com.caucho.hessian.io.AbstractDeserializer;
import com.caucho.hessian.io.AbstractHessianInput;

public class MonthDayDeserializer extends AbstractDeserializer {

    public Class<?> getType() {
        return MonthDay.class;
    }

    @Override
    public Object readObject(AbstractHessianInput in, Object[] fields) throws IOException {
        String[] names = (String[]) fields;
        int reference = in.addRef(null);
        int month = 0;
        int day = 0;
        for (int index = 0; index < names.length; index++) {
            if ("data".equals(names[index])) {
                month = in.readInt();
                day = in.readInt();
            } else {
                in.readObject();
            }
        }
        MonthDay yearMonth = MonthDay.of(month, day);
        in.setRef(reference, yearMonth);
        return yearMonth;
    }

}
