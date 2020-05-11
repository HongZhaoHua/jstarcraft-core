package com.jstarcraft.core.common.instant;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import com.jstarcraft.core.utility.StringUtility;

/**
 * 间隔表达式
 * 
 * @author Birdy
 *
 */
public class IntervalExpression extends DateTimeExpression {

    /** 参考日期时间 */
    private LocalDateTime reference;

    /** 间隔(单位:秒) */
    private int iterval;

    public IntervalExpression(String expression) {
        super(expression);

        String[] fields = expression.split(StringUtility.SPACE);
        if (fields.length != 7) {
            throw new IllegalArgumentException();
        } else {
            int second = Integer.parseInt(fields[0]);
            int minute = Integer.parseInt(fields[1]);
            int hour = Integer.parseInt(fields[2]);
            int day = Integer.parseInt(fields[3]);
            int month = Integer.parseInt(fields[4]);
            int year = Integer.parseInt(fields[5]);
            this.reference = LocalDateTime.of(year, month, day, hour, minute, second);
            this.iterval = Integer.parseInt(fields[6]);
            assert iterval > 0;
        }
    }

    @Override
    public ZonedDateTime getPreviousDateTime(ZonedDateTime dateTime) {
        long duration = ChronoUnit.SECONDS.between(reference, dateTime.toLocalDateTime());
        if (duration > 0) {
            long shift = (duration - 1) / iterval * iterval;
            return reference.plusSeconds(shift).atZone(dateTime.getZone());
        } else {
            long shift = (duration / iterval - 1) * iterval;
            return reference.plusSeconds(shift).atZone(dateTime.getZone());
        }
    }

    @Override
    public ZonedDateTime getNextDateTime(ZonedDateTime dateTime) {
        long duration = ChronoUnit.SECONDS.between(reference, dateTime.toLocalDateTime());
        if (duration < 0) {
            long shift = (duration + 1) / iterval * iterval;
            return reference.plusSeconds(shift).atZone(dateTime.getZone());
        } else {
            long shift = (duration / iterval + 1) * iterval;
            return reference.plusSeconds(shift).atZone(dateTime.getZone());
        }
    }

    @Override
    public boolean isMatchDateTime(ZonedDateTime dateTime) {
        long duration = ChronoUnit.SECONDS.between(reference, dateTime.toLocalDateTime());
        return duration % iterval == 0;
    }

}
