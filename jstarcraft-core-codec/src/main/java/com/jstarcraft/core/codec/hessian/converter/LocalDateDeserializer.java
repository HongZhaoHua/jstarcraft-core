package com.jstarcraft.core.codec.hessian.converter;

import java.io.IOException;
import java.time.LocalDate;

import com.caucho.hessian.io.AbstractDeserializer;
import com.caucho.hessian.io.AbstractHessianInput;

public class LocalDateDeserializer extends AbstractDeserializer {

    public Class<?> getType() {
        return LocalDate.class;
    }

    @Override
    public Object readObject(AbstractHessianInput in, Object[] fields) throws IOException {
        String[] names = (String[]) fields;
        int reference = in.addRef(null);
        int year = 0;
        int month = 0;
        int day = 0;
        for (int index = 0; index < names.length; index++) {
            if ("data".equals(names[index])) {
                year = in.readInt();
                month = in.readInt();
                day = in.readInt();
            } else {
                in.readObject();
            }
        }
        LocalDate date = LocalDate.of(year, month, day);
        in.setRef(reference, date);
        return date;
    }

}
