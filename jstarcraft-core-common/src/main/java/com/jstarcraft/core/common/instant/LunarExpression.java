package com.jstarcraft.core.common.instant;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.BitSet;

import com.jstarcraft.core.utility.StringUtility;

/**
 * 阴历表达式
 * 
 * @author Birdy
 *
 */
public class LunarExpression extends DateTimeExpression {

    /** 秒位图 */
    private final BitSet seconds;

    /** 分位图 */
    private final BitSet minutes;

    /** 时位图 */
    private final BitSet hours;

    /** 日位图(大月) */
    private final BitSet bigDays;

    /** 日位图(小月) */
    private final BitSet smallDays;

    /** 月位图 */
    private final BitSet months;

    /** 年位图 */
    private final BitSet years;

    public LunarExpression(String expression) {
        super(expression);

        this.seconds = new BitSet(60);
        this.minutes = new BitSet(60);
        this.hours = new BitSet(24);

        this.bigDays = new BitSet(31);
        this.smallDays = new BitSet(30);
        this.months = new BitSet(13);
        this.years = new BitSet(LunarDate.MAXIMUM_YEAR - LunarDate.MINIMUM_YEAR);

        String[] fields = expression.split(StringUtility.SPACE);
        if (fields.length != 5 && fields.length != 6) {
            throw new IllegalArgumentException();
        } else {
            this.setBits(this.seconds, fields[0], 0, 60, 0);
            this.setBits(this.minutes, fields[1], 0, 60, 0);
            this.setBits(this.hours, fields[2], 0, 24, 0);
            this.setBits(this.bigDays, fields[3], 1, 31, 0);
            this.setBits(this.smallDays, fields[3], 1, 30, 0);
            this.setBits(this.months, fields[4], 1, 13, 0);
            if (fields.length == 6) {
                this.setBits(this.years, fields[5], LunarDate.MINIMUM_YEAR, LunarDate.MAXIMUM_YEAR, LunarDate.MINIMUM_YEAR);
            } else {
                this.setBits(this.years, StringUtility.ASTERISK, LunarDate.MINIMUM_YEAR, LunarDate.MAXIMUM_YEAR, LunarDate.MINIMUM_YEAR);
            }
        }
    }

    private int[] getRange(String field, int from, int to) {
        int[] range = new int[2];
        if (field.contains(StringUtility.ASTERISK)) {
            // 处理星符
            range[0] = from;
            range[1] = to - 1;
        } else {
            // 处理连接符
            if (!field.contains(StringUtility.DASH)) {
                range[0] = range[1] = field.startsWith("L") ? -Integer.valueOf(field.substring(1)) : Integer.valueOf(field);
            } else {
                String[] split = field.split(StringUtility.DASH);
                if (split.length > 2) {
                    throw new IllegalArgumentException("Range has more than two fields: '" + field + "' in expression \"" + this.expression + "\"");
                }
                range[0] = split[0].startsWith("L") ? -Integer.valueOf(split[0].substring(1)) : Integer.valueOf(split[0]);
                range[1] = split[1].startsWith("L") ? -Integer.valueOf(split[1].substring(1)) : Integer.valueOf(split[1]);
            }
        }
        return range;
    }

    private void setBits(BitSet bits, String value, int from, int to, int shift) {
        if (value.contains(StringUtility.QUESTION)) {
            value = StringUtility.ASTERISK;
        }
        String[] fields = value.split(StringUtility.COMMA);
        for (String field : fields) {
            int[] range;
            int skip;
            if (!field.contains(StringUtility.FORWARD_SLASH)) {
                // Not an incrementer so it must be a range (possibly empty)
                range = getRange(field, from, to);
                skip = 1;
            } else {
                String[] split = field.split(StringUtility.FORWARD_SLASH);
                if (split.length > 2) {
                    throw new IllegalArgumentException("Incrementer has more than two fields: '" + field + "' in expression \"" + this.expression + "\"");
                }
                range = getRange(split[0], from, to);
                if (!split[0].contains(StringUtility.DASH)) {
                    range[1] = to - 1;
                }
                skip = Integer.parseInt(split[1]);
                if (skip <= 0) {
                    throw new IllegalArgumentException("Incrementer delta must be 1 or higher: '" + field + "' in expression \"" + this.expression + "\"");
                }
            }
            if (range[0] < 0) {
                range[0] = to + range[0];
            }
            if (range[1] < 0) {
                range[1] = to + range[1];
            }
            for (int index = range[0]; index <= range[1]; index += skip) {
                bits.set(index - shift);
            }
        }
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

    public BitSet getBigDays() {
        return bigDays;
    }

    public BitSet getSmallDays() {
        return smallDays;
    }

    public BitSet getMonths() {
        return months;
    }

    public BitSet getYears() {
        return years;
    }

    /**
     * 按照年月获取日位图
     * 
     * @param year
     * @param month
     * @return
     */
    private BitSet getDays(int year, int month) {
        int size = LunarDate.getDaySize(year, month);
        switch (size) {
        case 29: {
            return smallDays;
        }
        case 30: {
            return bigDays;
        }
        default: {
            throw new IllegalArgumentException();
        }
        }
    }

    @Override
    public ZonedDateTime getPreviousDateTime(ZonedDateTime dateTime) {
        LunarDate islamic = new LunarDate(dateTime.toLocalDate());
        LocalTime time = dateTime.toLocalTime();
        int year = islamic.getYear();
        int month = islamic.getMonth();
        int day = islamic.getDay();
        boolean change = false;
        if (!years.get(year - LunarDate.MINIMUM_YEAR)) {
            year = years.previousSetBit(year - LunarDate.MINIMUM_YEAR);
            if (year == -1) {
                return null;
            }
            year += LunarDate.MINIMUM_YEAR;
            month = months.previousSetBit(12);
            day = 30;
            time = LocalTime.MAX;
            change = true;
        } else if (!months.get(month)) {
            month = months.previousSetBit(month);
            if (month == -1) {
                month = months.previousSetBit(12);
                year--;
                year = years.previousSetBit(year - LunarDate.MINIMUM_YEAR);
                if (year == -1) {
                    return null;
                }
                year += LunarDate.MINIMUM_YEAR;
            }
            day = 30;
            time = LocalTime.MAX;
            change = true;
        }
        BitSet days = getDays(year, month);
        if (!days.get(day)) {
            day = days.previousSetBit(day);
            while (day == -1) {
                month--;
                if (!months.get(month)) {
                    month = months.previousSetBit(month);
                    if (month == -1) {
                        month = months.previousSetBit(12);
                        year--;
                        year = years.previousSetBit(year - LunarDate.MINIMUM_YEAR);
                        if (year == -1) {
                            return null;
                        }
                        year += LunarDate.MINIMUM_YEAR;
                    }
                }
                days = getDays(year, month);
                day = days.previousSetBit(30);
            }
            time = LocalTime.MAX;
            change = true;
        }
        int hour = time.getHour();
        int minute = time.getMinute();
        int second = time.getSecond();
        second = seconds.previousSetBit(second - (change ? 0 : 1));
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
            day--;
        }
        day = days.previousSetBit(day);
        if (day == -1) {
            second = seconds.previousSetBit(59);
            minute = minutes.previousSetBit(59);
            hour = hours.previousSetBit(23);
        }
        while (day == -1) {
            month--;
            if (!months.get(month)) {
                month = months.previousSetBit(month);
                if (month == -1) {
                    month = months.previousSetBit(12);
                    year--;
                    year = years.previousSetBit(year - LunarDate.MINIMUM_YEAR);
                    if (year == -1) {
                        return null;
                    }
                    year += LunarDate.MINIMUM_YEAR;
                }
            }
            days = getDays(year, month);
            day = days.previousSetBit(30);
        }
        if (!years.get(year - LunarDate.MINIMUM_YEAR)) {
            return null;
        }
        islamic = new LunarDate(year, month, day);
        LocalDate date = islamic.getDate();
        return ZonedDateTime.of(date, LocalTime.of(hour, minute, second), dateTime.getZone());
    }

    @Override
    public ZonedDateTime getNextDateTime(ZonedDateTime dateTime) {
        LunarDate islamic = new LunarDate(dateTime.toLocalDate());
        LocalTime time = dateTime.toLocalTime();
        int year = islamic.getYear();
        int month = islamic.getMonth();
        int day = islamic.getDay();
        boolean change = false;
        if (!years.get(year - LunarDate.MINIMUM_YEAR)) {
            year = years.nextSetBit(year - LunarDate.MINIMUM_YEAR);
            if (year == -1) {
                return null;
            }
            year += LunarDate.MINIMUM_YEAR;
            month = months.nextSetBit(1);
            day = 1;
            time = LocalTime.MIN;
            change = true;
        } else if (!months.get(month)) {
            month = months.nextSetBit(month);
            if (month == -1) {
                month = months.nextSetBit(1);
                year++;
            }
            year = years.nextSetBit(year - LunarDate.MINIMUM_YEAR);
            if (year == -1) {
                return null;
            }
            year += LunarDate.MINIMUM_YEAR;
            day = 1;
            time = LocalTime.MIN;
            change = true;
        }
        BitSet days = getDays(year, month);
        if (!days.get(day)) {
            day = days.nextSetBit(day);
            while (day == -1) {
                month++;
                if (!months.get(month)) {
                    month = months.nextSetBit(month);
                    if (month == -1) {
                        month = months.nextSetBit(1);
                        year++;
                    }
                    year = years.nextSetBit(year - LunarDate.MINIMUM_YEAR);
                    if (year == -1) {
                        return null;
                    }
                    year += LunarDate.MINIMUM_YEAR;
                }
                days = getDays(year, month);
                day = days.nextSetBit(1);
            }
            time = LocalTime.MIN;
            change = true;
        }
        int hour = time.getHour();
        int minute = time.getMinute();
        int second = time.getSecond();
        second = seconds.nextSetBit(second + (change ? 0 : 1));
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
            day++;
        }
        day = days.nextSetBit(day);
        if (day == -1) {
            second = seconds.nextSetBit(0);
            minute = minutes.nextSetBit(0);
            hour = hours.nextSetBit(0);
        }
        while (day == -1) {
            month++;
            if (!months.get(month)) {
                month = months.nextSetBit(month);
                if (month == -1) {
                    month = months.nextSetBit(1);
                    year++;
                }
                year = years.nextSetBit(year - LunarDate.MINIMUM_YEAR);
                if (year == -1) {
                    return null;
                }
                year += LunarDate.MINIMUM_YEAR;
            }
            days = getDays(year, month);
            day = days.nextSetBit(1);
        }
        if (!years.get(year - LunarDate.MINIMUM_YEAR)) {
            return null;
        }
        islamic = new LunarDate(year, month, day);
        LocalDate date = islamic.getDate();
        return ZonedDateTime.of(date, LocalTime.of(hour, minute, second), dateTime.getZone());
    }

    @Override
    public boolean isMatchDateTime(ZonedDateTime dateTime) {
        LunarDate islamic = new LunarDate(dateTime.toLocalDate());
        int year = islamic.getYear();
        int month = islamic.getMonth();
        int day = islamic.getDay();
        BitSet days = getDays(year, month);
        LocalTime time = dateTime.toLocalTime();
        int hour = time.getHour();
        int minute = time.getMinute();
        int second = time.getSecond();
        if (seconds.get(second) && minutes.get(minute) && hours.get(hour)) {
            if (days.get(day) && months.get(month) && years.get(year - LunarDate.MINIMUM_YEAR)) {
                return true;
            }
        }
        return false;
    }

}
