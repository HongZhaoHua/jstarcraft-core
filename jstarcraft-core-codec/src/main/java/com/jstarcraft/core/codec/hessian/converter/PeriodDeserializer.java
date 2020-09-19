package com.jstarcraft.core.codec.hessian.converter;

import java.io.IOException;
import java.time.Period;

import com.caucho.hessian.io.AbstractDeserializer;
import com.caucho.hessian.io.AbstractHessianInput;

public class PeriodDeserializer extends AbstractDeserializer {

    public Class<?> getType() {
        return Period.class;
    }

    @Override
    public Object readObject(AbstractHessianInput in, Object[] fields) throws IOException {
        String[] names = (String[]) fields;
        int reference = in.addRef(null);
        int years = 0;
        int months = 0;
        int days = 0;
        for (int index = 0; index < names.length; index++) {
            if ("data".equals(names[index])) {
                years = in.readInt();
                months = in.readInt();
                days = in.readInt();
            } else {
                in.readObject();
            }
        }
        Period period = Period.of(years, months, days);
        in.setRef(reference, period);
        return period;
    }

}
