package com.jstarcraft.core.codec.hessian;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.MonthDay;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

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
        ZoneOffset zone = ZoneOffset.UTC;
        ZonedDateTime dateTime = ZonedDateTime.of(date, time, zone);
        testConvert(LocalDate.class, date);
        testConvert(LocalTime.class, time);
        testConvert(ZoneOffset.class, zone);
        testConvert(LocalDateTime.class, dateTime.toLocalDateTime());
        testConvert(ZonedDateTime.class, dateTime);

        MonthDay monthDay = MonthDay.of(6, 15);
        testConvert(MonthDay.class, monthDay);
        YearMonth yearMonth = YearMonth.of(2020, 6);
        testConvert(YearMonth.class, yearMonth);
    }

}
