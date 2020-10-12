package com.jstarcraft.core.common.instant;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.util.BitSet;

import com.jstarcraft.core.utility.StringUtility;

import it.unimi.dsi.fastutil.ints.IntRBTreeSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSortedSet;

/**
 * 阳历表达式
 * 
 * @author Birdy
 *
 */
public class SolarExpression extends DateTimeExpression {

    /**
     * 支持的最小年份
     */
    public final static int MINIMUM_YEAR = 1850;

    /**
     * 支持的最大年份
     */
    public final static int MAXIMUM_YEAR = 2150;

    /** 秒位图 */
    private final BitSet seconds;

    /** 分位图 */
    private final BitSet minutes;

    /** 时位图 */
    private final BitSet hours;

    /** 日位图(28天) */
    private final BitSet days28;

    /** 日位图(29天) */
    private final BitSet days29;

    /** 日位图(30天) */
    private final BitSet days30;

    /** 日位图(31天) */
    private final BitSet days31;

    /** 周位图 */
    private final IntSortedSet weeks;

    /** 月位图 */
    private final BitSet months;

    /** 年位图 */
    private final BitSet years;

    public SolarExpression(String expression) {
        super(expression);

        this.seconds = new BitSet(60);
        this.minutes = new BitSet(60);
        this.hours = new BitSet(24);

        this.days28 = new BitSet(29);
        this.days29 = new BitSet(30);
        this.days30 = new BitSet(31);
        this.days31 = new BitSet(32);
        this.months = new BitSet(13);
        this.weeks = new IntRBTreeSet();
        this.years = new BitSet(MAXIMUM_YEAR - MINIMUM_YEAR);

        String[] fields = expression.split(StringUtility.SPACE);
        if (fields.length != 6 && fields.length != 7) {
            throw new IllegalArgumentException();
        } else {
            this.setBits(this.seconds, fields[0], 0, 60, 0);
            this.setBits(this.minutes, fields[1], 0, 60, 0);
            this.setBits(this.hours, fields[2], 0, 24, 0);
            this.setBits(this.months, fields[4], 1, 13, 0);
            this.setWeeks(this.weeks, fields[5]);
            if (this.weeks.isEmpty()) {
                this.setBits(this.days28, fields[3], 1, 29, 0);
                this.setBits(this.days29, fields[3], 1, 30, 0);
                this.setBits(this.days30, fields[3], 1, 31, 0);
                this.setBits(this.days31, fields[3], 1, 32, 0);
            }
            if (fields.length == 7) {
                this.setBits(this.years, fields[6], MINIMUM_YEAR, MAXIMUM_YEAR, MINIMUM_YEAR);
            } else {
                this.setBits(this.years, StringUtility.ASTERISK, MINIMUM_YEAR, MAXIMUM_YEAR, MINIMUM_YEAR);
            }
        }
    }

    private int getNumber(String field) {
        int index = field.indexOf("L") + 1;
        int number = index > 0 ? -1 : 0;
        if (field.length() > index) {
            number += Integer.valueOf(field.substring(index));
        }
        return number;
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
                range[0] = range[1] = getNumber(field);
            } else {
                String[] split = field.split(StringUtility.DASH);
                if (split.length > 2) {
                    throw new IllegalArgumentException("Range has more than two fields: '" + field + "' in expression \"" + this.expression + "\"");
                }
                range[0] = getNumber(split[0]);
                range[1] = getNumber(split[1]);
            }
        }
        return range;
    }

    private void setBits(BitSet bits, String value, int from, int to, int shift) {
        if (value.contains(StringUtility.QUESTION)) {
            return;
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
                if (index >= from && index < to) {
                    bits.set(index - shift);
                }
            }
        }
    }

    private String replaceOrdinals(String value) {
        for (DayOfWeek term : DayOfWeek.values()) {
            // 从1代表周一转换到1代表周日
            int ordinal = term.getValue() % 7 + 1;
            value = value.replace(term.name().subSequence(0, 3), String.valueOf(ordinal));
        }
        return value;
    }

    private void setWeeks(IntSet weeks, String value) {
        if (value.contains(StringUtility.QUESTION)) {
            return;
        }
        String[] fields = value.split(StringUtility.COMMA);
        for (String field : fields) {
            field = replaceOrdinals(field);
            if (field.endsWith("L")) {
                int nth = 1;
                int week = Integer.parseInt(field.substring(0, field.length() - 1));
                // 从1代表周日转换到0代表周一
                int index = (week + 5) % 7;
                index = nth * 7 + index;
                weeks.add(-index);
            } else if (field.contains(StringUtility.HASH)) {
                String[] split = field.split(StringUtility.HASH);
                int nth = Integer.parseInt(split[1]);
                int week = Integer.parseInt(split[0]);
                // 从1代表周日转换到0代表周一
                int index = (week + 5) % 7;
                index = nth * 7 + index;
                weeks.add(index);
            } else {
                int[] range;
                int skip;
                if (!field.contains(StringUtility.FORWARD_SLASH)) {
                    // Not an incrementer so it must be a range (possibly empty)
                    range = getRange(field, 1, 8);
                    skip = 1;
                } else {
                    String[] split = field.split(StringUtility.FORWARD_SLASH);
                    if (split.length > 2) {
                        throw new IllegalArgumentException("Incrementer has more than two fields: '" + field + "' in expression \"" + this.expression + "\"");
                    }
                    range = getRange(split[0], 1, 8);
                    if (!split[0].contains(StringUtility.DASH)) {
                        range[1] = 8 - 1;
                    }
                    skip = Integer.parseInt(split[1]);
                    if (skip <= 0) {
                        throw new IllegalArgumentException("Incrementer delta must be 1 or higher: '" + field + "' in expression \"" + this.expression + "\"");
                    }
                }
                if (range[0] < 0) {
                    range[0] = 8 + range[0];
                }
                if (range[1] < 0) {
                    range[1] = 8 + range[1];
                }
                for (int week = range[0]; week <= range[1]; week += skip) {
                    // 从1代表周日转换到0代表周一
                    int index = (week + 5) % 7;
                    weeks.add(index);
                }
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

    public BitSet getDays28() {
        return days28;
    }

    public BitSet getDays29() {
        return days29;
    }

    public BitSet getDays30() {
        return days30;
    }

    public BitSet getDays31() {
        return days31;
    }

    public BitSet getMonths() {
        return months;
    }

    public IntSortedSet getWeeks() {
        return weeks;
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
        BitSet days;
        YearMonth yearMonth = YearMonth.of(year, month);
        int size = yearMonth.lengthOfMonth();
        switch (size) {
        case 28: {
            days = days28;
            break;
        }
        case 29: {
            days = days29;
            break;
        }
        case 30: {
            days = days30;
            break;
        }
        case 31: {
            days = days31;
            break;
        }
        default: {
            throw new IllegalArgumentException();
        }
        }
        if (!weeks.isEmpty()) {
            // 参考TemporalAdjusters.dayOfWeekInMonth代码
            int first = yearMonth.atDay(1).get(ChronoField.DAY_OF_WEEK);
            int last = yearMonth.atDay(size).get(ChronoField.DAY_OF_WEEK);
            days = new BitSet(days.size());
            for (int index : weeks) {
                if (index < 0) {
                    // 处理负数的情况
                    index = -index;
                    int nth = index / 7;
                    // 从0代表周一转换到1代表周一
                    int week = index % 7 + 1;
                    int offset = week - last;
                    offset = (offset == 0 ? 0 : (offset > 0 ? offset - 7 : offset));
                    offset -= (nth - 1) * 7;
                    int day = size + offset;
                    if (day < 1 || day > size) {
                        continue;
                    }
                    days.set(day, true);
                } else if (index < 7) {
                    // 从0代表周一转换到1代表周一
                    int week = index % 7 + 1;
                    int offset = (week - first + 7) % 7;
                    for (int day = 1 + offset; day <= size; day += 7) {
                        if (day < 1 || day > size) {
                            continue;
                        }
                        days.set(day, true);
                    }
                } else {
                    // 处理正数的情况
                    int nth = index / 7;
                    // 从0代表周一转换到1代表周一
                    int week = index % 7 + 1;
                    int offset = (week - first + 7) % 7;
                    offset += (nth - 1) * 7;
                    int day = 1 + offset;
                    if (day < 1 || day > size) {
                        continue;
                    }
                    days.set(day, true);
                }
            }
        }
        return days;
    }

    @Override
    public ZonedDateTime getPreviousDateTime(ZonedDateTime dateTime) {
        SolarDate sloar = new SolarDate(dateTime.toLocalDate());
        LocalTime time = dateTime.toLocalTime();
        int year = sloar.getYear();
        int month = sloar.getMonth();
        int day = sloar.getDay();
        boolean change = false;
        if (!years.get(year - MINIMUM_YEAR)) {
            year = years.previousSetBit(year - MINIMUM_YEAR);
            if (year == -1) {
                return null;
            }
            year += MINIMUM_YEAR;
            month = months.previousSetBit(12);
            day = 31;
            time = LocalTime.MAX;
            change = true;
        } else if (!months.get(month)) {
            month = months.previousSetBit(month);
            if (month == -1) {
                month = months.previousSetBit(12);
                year--;
                year = years.previousSetBit(year - MINIMUM_YEAR);
                if (year == -1) {
                    return null;
                }
                year += MINIMUM_YEAR;
            }
            day = 31;
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
                        year = years.previousSetBit(year - MINIMUM_YEAR);
                        if (year == -1) {
                            return null;
                        }
                        year += MINIMUM_YEAR;
                    }
                }
                days = getDays(year, month);
                day = days.previousSetBit(31);
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
                    year = years.previousSetBit(year - MINIMUM_YEAR);
                    if (year == -1) {
                        return null;
                    }
                    year += MINIMUM_YEAR;
                }
            }
            days = getDays(year, month);
            day = days.previousSetBit(31);
        }
        if (!years.get(year - MINIMUM_YEAR)) {
            return null;
        }
        sloar = new SolarDate(year, month, day);
        LocalDate date = sloar.getDate();
        return ZonedDateTime.of(date, LocalTime.of(hour, minute, second), dateTime.getZone());
    }

    @Override
    public ZonedDateTime getNextDateTime(ZonedDateTime dateTime) {
        SolarDate sloar = new SolarDate(dateTime.toLocalDate());
        LocalTime time = dateTime.toLocalTime();
        int year = sloar.getYear();
        int month = sloar.getMonth();
        int day = sloar.getDay();
        boolean change = false;
        if (!years.get(year - MINIMUM_YEAR)) {
            year = years.nextSetBit(year - MINIMUM_YEAR);
            if (year == -1) {
                return null;
            }
            year += MINIMUM_YEAR;
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
            year = years.nextSetBit(year - MINIMUM_YEAR);
            if (year == -1) {
                return null;
            }
            year += MINIMUM_YEAR;
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
                    year = years.nextSetBit(year - MINIMUM_YEAR);
                    if (year == -1) {
                        return null;
                    }
                    year += MINIMUM_YEAR;
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
                year = years.nextSetBit(year - MINIMUM_YEAR);
                if (year == -1) {
                    return null;
                }
                year += MINIMUM_YEAR;
            }
            days = getDays(year, month);
            day = days.nextSetBit(1);
        }
        if (!years.get(year - MINIMUM_YEAR)) {
            return null;
        }
        sloar = new SolarDate(year, month, day);
        LocalDate date = sloar.getDate();
        return ZonedDateTime.of(date, LocalTime.of(hour, minute, second), dateTime.getZone());
    }

    @Override
    public boolean isMatchDateTime(ZonedDateTime dateTime) {
        SolarDate solar = new SolarDate(dateTime.toLocalDate());
        int year = solar.getYear();
        int month = solar.getMonth();
        int day = solar.getDay();
        BitSet days = getDays(year, month);
        LocalTime time = dateTime.toLocalTime();
        int hour = time.getHour();
        int minute = time.getMinute();
        int second = time.getSecond();
        if (seconds.get(second) && minutes.get(minute) && hours.get(hour)) {
            if (days.get(day) && months.get(month) && years.get(year - MINIMUM_YEAR)) {
                return true;
            }
        }
        return false;
    }

}
