package com.jstarcraft.core.codec.hessian;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.jstarcraft.core.codec.ContentCodec;
import com.jstarcraft.core.codec.ContentCodecTestCase;
import com.jstarcraft.core.codec.specification.CodecDefinition;

public class HessianContentCodecTestCase extends ContentCodecTestCase {

    @Override
    protected ContentCodec getContentCodec(CodecDefinition protocolDefinition) {
        HessianContentCodec codec = new HessianContentCodec(protocolDefinition);
        return codec;
    }

    @Override
    public void testInstant() throws Exception {
        super.testInstant();

        LocalDate date = LocalDate.of(2020, 6, 15);
        LocalTime time = LocalTime.of(12, 0, 0);
        LocalDateTime dateTime = LocalDateTime.of(date, time);
        testConvert(LocalDateTime.class, dateTime);
    }

}
