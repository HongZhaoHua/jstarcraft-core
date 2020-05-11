package com.jstarcraft.core.common.instant;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.BitSet;

import com.jstarcraft.core.utility.StringUtility;

/**
 * 节气表达式
 * 
 * @author Birdy
 *
 */
public class TermExpression extends DateTimeExpression {

    /** 秒位图 */
    private final BitSet seconds;

    /** 分位图 */
    private final BitSet minutes;

    /** 时位图 */
    private final BitSet hours;

    /** 节气位图 */
    private final BitSet terms;

    /** 年位图 */
    private final BitSet years;

    public TermExpression(String expression) {
        super(expression);

        this.seconds = new BitSet(60);
        this.minutes = new BitSet(60);
        this.hours = new BitSet(24);

        this.terms = new BitSet(24);
        this.years = new BitSet(200);

        String[] fields = expression.split(StringUtility.SPACE);
        if (fields.length != 4 && fields.length != 5) {
            throw new IllegalArgumentException();
        } else {
            this.setBits(this.seconds, fields[0], 0, 60, 0);
            this.setBits(this.minutes, fields[1], 0, 60, 0);
            this.setBits(this.hours, fields[2], 0, 24, 0);

            this.setBits(this.terms, this.replaceOrdinals(fields[3]), 0, 24, 0);
            if (fields.length == 5) {
                this.setBits(this.years, fields[4], 1900, 2100, 1900);
            } else {
                this.setBits(this.years, StringUtility.ASTERISK, 1900, 2100, 1900);
            }
        }
    }

    private String replaceOrdinals(String value) {
        for (TermType term : TermType.values()) {
            value = value.replace(term.name(), String.valueOf(term.ordinal()));
        }
        return value;
    }

    private void setBits(BitSet bits, String value, int from, int to, int shift) {
        if (value.contains(StringUtility.QUESTION)) {
            value = StringUtility.ASTERISK;
        }
        String[] fields = value.split(StringUtility.COMMA);
        for (String field : fields) {
            if (!field.contains(StringUtility.FORWARD_SLASH)) {
                // Not an incrementer so it must be a range (possibly empty)
                int[] range = getRange(field, from, to, shift);
                bits.set(range[0], range[1] + 1);
            } else {
                String[] split = field.split(StringUtility.FORWARD_SLASH);
                if (split.length > 2) {
                    throw new IllegalArgumentException("Incrementer has more than two fields: '" + field + "' in expression \"" + this.expression + "\"");
                }
                int[] range = getRange(split[0], from, to, shift);
                if (!split[0].contains(StringUtility.DASH)) {
                    range[1] = to - 1;
                }
                int skip = Integer.parseInt(split[1]);
                if (skip <= 0) {
                    throw new IllegalArgumentException("Incrementer delta must be 1 or higher: '" + field + "' in expression \"" + this.expression + "\"");
                }
                for (int index = range[0]; index <= range[1]; index += skip) {
                    bits.set(index);
                }
            }
        }
    }

    private int[] getRange(String field, int from, int to, int shift) {
        int[] range = new int[2];
        if (field.contains(StringUtility.ASTERISK)) {
            range[0] = from;
            range[1] = to - 1;
        } else {
            if (!field.contains(StringUtility.DASH)) {
                range[0] = range[1] = Integer.valueOf(field);
            } else {
                String[] split = field.split(StringUtility.DASH);
                if (split.length > 2) {
                    throw new IllegalArgumentException("Range has more than two fields: '" + field + "' in expression \"" + this.expression + "\"");
                }
                range[0] = Integer.valueOf(split[0]);
                range[1] = Integer.valueOf(split[1]);
            }
            if (range[0] >= to || range[1] >= to) {
                throw new IllegalArgumentException("Range exceeds maximum (" + to + "): '" + field + "' in expression \"" + this.expression + "\"");
            }
            if (range[0] < from || range[1] < from) {
                throw new IllegalArgumentException("Range less than minimum (" + from + "): '" + field + "' in expression \"" + this.expression + "\"");
            }
            if (range[0] > range[1]) {
                throw new IllegalArgumentException("Invalid inverted range: '" + field + "' in expression \"" + this.expression + "\"");
            }
        }

        range[0] = range[0] - shift;
        range[1] = range[1] - shift;
        return range;
    }

    public BitSet getSeconds() {
        return seconds;
    }

    public BitSet getMinutes() {
        return minutes;
    }

    public BitSet getHours() {
        return hours;
    }

    public BitSet getYears() {
        return years;
    }

    @Override
    public ZonedDateTime getPreviousDateTime(ZonedDateTime dateTime) {
        int year = dateTime.getYear();
        int month = dateTime.getMonthValue();
        int term = (month - 1) * 2 + 1;
        LocalDate date;
        term++;
        do {
            term--;
            term = terms.previousSetBit(term);
            if (term == -1) {
                term = terms.previousSetBit(23);
                year--;
            }
            date = TermType.values()[term].getDate(year);
        } while (date.isAfter(dateTime.toLocalDate()));
        if (date.isBefore(dateTime.toLocalDate())) {
            int second = seconds.previousSetBit(59);
            int minute = minutes.previousSetBit(59);
            int hour = hours.previousSetBit(23);
            return ZonedDateTime.of(date, LocalTime.of(hour, minute, second), dateTime.getZone());
        }
        // 日期相同才需要执行
        int hour = dateTime.getHour();
        int minute = dateTime.getMinute();
        int second = dateTime.getSecond();
        second = seconds.previousSetBit(second - 1);
        if (second == -1) {
            second = seconds.previousSetBit(59);
            minute--;
        }
        minute = minutes.previousSetBit(minute);
        if (minute == -1) {
            second = seconds.previousSetBit(59);
            minute = minutes.previousSetBit(59);
            hour--;
        }
        hour = hours.previousSetBit(hour);
        if (hour == -1) {
            second = seconds.previousSetBit(59);
            minute = minutes.previousSetBit(59);
            hour = hours.previousSetBit(23);
            term--;
        }
        term = terms.previousSetBit(term);
        if (term == -1) {
            second = seconds.previousSetBit(59);
            minute = minutes.previousSetBit(59);
            hour = hours.previousSetBit(23);
            term = terms.previousSetBit(23);
            // 防止连续跨年
            if (year == dateTime.getYear()) {
                year--;
            }
        }
        year = years.previousSetBit(year - TermType.MINIMUM_YEAR);
        if (year == -1) {
            return null;
        }
        year += TermType.MINIMUM_YEAR;
        return ZonedDateTime.of(TermType.values()[term].getDate(year), LocalTime.of(hour, minute, second), dateTime.getZone());
    }

    @Override
    public ZonedDateTime getNextDateTime(ZonedDateTime dateTime) {
        int year = dateTime.getYear();
        int month = dateTime.getMonthValue();
        int term = (month - 1) * 2;
        LocalDate date;
        term--;
        do {
            term++;
            term = terms.nextSetBit(term);
            if (term == -1) {
                term = terms.nextSetBit(0);
                year++;
            }
            date = TermType.values()[term].getDate(year);
        } while (date.isBefore(dateTime.toLocalDate()));
        if (date.isAfter(dateTime.toLocalDate())) {
            int second = seconds.nextSetBit(0);
            int minute = minutes.nextSetBit(0);
            int hour = hours.nextSetBit(0);
            return ZonedDateTime.of(date, LocalTime.of(hour, minute, second), dateTime.getZone());
        }
        // 日期相同才需要执行
        int hour = dateTime.getHour();
        int minute = dateTime.getMinute();
        int second = dateTime.getSecond();
        second = seconds.nextSetBit(second + 1);
        if (second == -1) {
            second = seconds.nextSetBit(0);
            minute++;
        }
        minute = minutes.nextSetBit(minute);
        if (minute == -1) {
            second = seconds.nextSetBit(0);
            minute = minutes.nextSetBit(0);
            hour++;
        }
        hour = hours.nextSetBit(hour);
        if (hour == -1) {
            second = seconds.nextSetBit(0);
            minute = minutes.nextSetBit(0);
            hour = hours.nextSetBit(0);
            term++;
        }
        term = terms.nextSetBit(term);
        if (term == -1) {
            second = seconds.nextSetBit(0);
            minute = minutes.nextSetBit(0);
            hour = hours.nextSetBit(0);
            term = terms.nextSetBit(0);
            // 防止连续跨年
            if (year == dateTime.getYear()) {
                year++;
            }
        }
        year = years.nextSetBit(year - TermType.MINIMUM_YEAR);
        if (year == -1) {
            return null;
        }
        year += TermType.MINIMUM_YEAR;
        return ZonedDateTime.of(TermType.values()[term].getDate(year), LocalTime.of(hour, minute, second), dateTime.getZone());
    }

    @Override
    public boolean isMatchDateTime(ZonedDateTime dateTime) {
        int year = dateTime.getYear();
        int month = dateTime.getMonthValue();
        int day = dateTime.getDayOfMonth();
        int term;
        int hour = dateTime.getHour();
        int minute = dateTime.getMinute();
        int second = dateTime.getSecond();
        if (seconds.get(second) && minutes.get(minute) && hours.get(hour)) {
            term = (month - 1) * 2;
            if (TermType.values()[term].getDate(year).getDayOfMonth() == day) {
                return true;
            }
            term++;
            if (TermType.values()[term].getDate(year).getDayOfMonth() == day) {
                return true;
            }
        }
        return false;
    }

}
