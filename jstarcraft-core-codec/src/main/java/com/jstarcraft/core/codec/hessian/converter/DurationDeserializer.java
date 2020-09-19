package com.jstarcraft.core.codec.hessian.converter;

import java.io.IOException;
import java.time.Duration;

import com.caucho.hessian.io.AbstractDeserializer;
import com.caucho.hessian.io.AbstractHessianInput;

public class DurationDeserializer extends AbstractDeserializer {

    public Class<?> getType() {
        return Duration.class;
    }

    @Override
    public Object readObject(AbstractHessianInput in, Object[] fields) throws IOException {
        String[] names = (String[]) fields;
        int reference = in.addRef(null);
        long seconds = 0L;
        for (int index = 0; index < names.length; index++) {
            if ("data".equals(names[index])) {
                seconds = in.readLong();
            } else {
                in.readObject();
            }
        }
        Duration duration = Duration.ofSeconds(seconds);
        in.setRef(reference, duration);
        return duration;
    }

}
