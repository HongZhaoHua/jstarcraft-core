package com.jstarcraft.core.codec.hessian.converter;

import java.io.IOException;
import java.time.YearMonth;

import com.caucho.hessian.io.AbstractDeserializer;
import com.caucho.hessian.io.AbstractHessianInput;

public class YearMonthDeserializer extends AbstractDeserializer {

    public Class<?> getType() {
        return YearMonth.class;
    }

    @Override
    public Object readObject(AbstractHessianInput in, Object[] fields) throws IOException {
        String[] names = (String[]) fields;
        int reference = in.addRef(null);
        int year = 0;
        int month = 0;
        for (int index = 0; index < names.length; index++) {
            if ("data".equals(names[index])) {
                year = in.readInt();
                month = in.readInt();
            } else {
                in.readObject();
            }
        }
        YearMonth yearMonth = YearMonth.of(year, month);
        in.setRef(reference, yearMonth);
        return yearMonth;
    }

}
