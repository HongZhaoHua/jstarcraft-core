package com.jstarcraft.core.common.instant;

import java.time.LocalDate;
import java.time.chrono.HijrahDate;
import java.time.temporal.ChronoField;
import java.util.Objects;

/**
 * 伊斯兰历日期
 * 
 * @author Birdy
 *
 */
public class IslamicDate implements CalendarDate {

    /**
     * 支持的最小年份
     */
    public final static int MINIMUM_YEAR = 1300;

    /**
     * 支持的最大年份
     */
    public final static int MAXIMUM_YEAR = 1600;

    private HijrahDate date;

    /**
     * 通过标准日期获取伊斯兰历日期
     * 
     * @param date
     */
    public IslamicDate(LocalDate date) {
        this.date = HijrahDate.from(date);
    }

    /**
     * 通过年,月,日获取伊斯兰历日期
     * 
     * @param date
     */
    public IslamicDate(int year, int month, int day) {
        this.date = HijrahDate.of(year, month, day);
    }

    @Override
    public CalendarType getType() {
        return CalendarType.Islamic;
    }

    @Override
    public int getYear() {
        return date.get(ChronoField.YEAR);
    }

    @Override
    public int getMonth() {
        return date.get(ChronoField.MONTH_OF_YEAR);
    }

    @Override
    public int getDay() {
        return date.get(ChronoField.DAY_OF_MONTH);
    }

    @Override
    public boolean isLeap() {
        return date.isLeapYear();
    }

    @Override
    public LocalDate getDate() {
        return LocalDate.ofEpochDay(date.toEpochDay());
    }

    @Override
    public int hashCode() {
        return Objects.hash(date);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object)
            return true;
        if (object == null)
            return false;
        if (getClass() != object.getClass())
            return false;
        IslamicDate that = (IslamicDate) object;
        return Objects.equals(this.date, that.date);
    }

    @Override
    public String toString() {
        return "IslamicDate [" + date.toString() + "]";
    }

    /**
     * 获取指定年份的天数
     * 
     * @param year
     * @return
     */
    public static int getDaySize(int year) {
        HijrahDate date = HijrahDate.of(year, 1, 1);
        return date.lengthOfYear();
    }

    /**
     * 获取指定月份的天数
     * 
     * @param year
     * @param month
     * @return
     */
    public static int getDaySize(int year, int month) {
        HijrahDate date = HijrahDate.of(year, month, 1);
        return date.lengthOfMonth();
    }

}
