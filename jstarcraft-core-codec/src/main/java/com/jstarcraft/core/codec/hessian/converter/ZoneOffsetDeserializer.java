package com.jstarcraft.core.codec.hessian.converter;

import java.io.IOException;
import java.time.ZoneOffset;

import com.caucho.hessian.io.AbstractDeserializer;
import com.caucho.hessian.io.AbstractHessianInput;

public class ZoneOffsetDeserializer extends AbstractDeserializer {

    public Class<?> getType() {
        return ZoneOffset.class;
    }

    @Override
    public Object readObject(AbstractHessianInput in, Object[] fields) throws IOException {
        String[] names = (String[]) fields;
        int reference = in.addRef(null);
        int data = 0;
        for (int index = 0; index < names.length; index++) {
            if ("data".equals(names[index])) {
                data = in.readInt();
            } else {
                in.readObject();
            }
        }
        ZoneOffset zoneOffset = ZoneOffset.ofTotalSeconds(data);
        in.setRef(reference, zoneOffset);
        return zoneOffset;
    }

}
