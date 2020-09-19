package com.jstarcraft.core.codec.hessian;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.MonthDay;
import java.time.OffsetDateTime;
import java.time.Period;
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

        Duration duration = Duration.ofSeconds(1000L);
        Period period = Period.of(10, 10, 10);
        LocalDate date = LocalDate.of(2020, 6, 15);
        LocalTime time = LocalTime.of(12, 0, 0);
        ZoneOffset zone = ZoneOffset.UTC;
        testConvert(Duration.class, duration);
        testConvert(Period.class, period);
        testConvert(LocalDate.class, date);
        testConvert(LocalTime.class, time);
        testConvert(ZoneOffset.class, zone);
        testConvert(LocalDateTime.class, LocalDateTime.of(date, time));
        testConvert(OffsetDateTime.class, OffsetDateTime.of(date, time, zone));
        testConvert(ZonedDateTime.class, ZonedDateTime.of(date, time, zone));

        MonthDay monthDay = MonthDay.of(6, 15);
        testConvert(MonthDay.class, monthDay);
        YearMonth yearMonth = YearMonth.of(2020, 6);
        testConvert(YearMonth.class, yearMonth);
    }

}
