package com.jstarcraft.core.common.instant;

import java.time.LocalDate;
import java.util.Objects;

/**
 * 阳历日期
 * 
 * @author Birdy
 *
 */
public class SolarDate implements CalendarDate {

    private LocalDate date;

    /**
     * 通过标准日期获取阳历日期
     * 
     * @param date
     */
    public SolarDate(LocalDate date) {
        this.date = date;
    }

    /**
     * 通过年,月,日获取阳历日期
     * 
     * @param year
     * @param month
     * @param day
     */
    public SolarDate(int year, int month, int day) {
        // 防止由于月份超过12导致的异常
        if (month > 12) {
            year += ((month - 1) / 12);
            month = month % 12;
            if (month == 0) {
                month = 12;
            }
        }
        this.date = LocalDate.of(year, month, day);
    }

    @Override
    public CalendarType getType() {
        return CalendarType.Solar;
    }

    /**
     * 获取阳历年
     * 
     * @return
     */
    @Override
    public int getYear() {
        return date.getYear();
    }

    /***
     * 获取阳历月
     * 
     * @return
     */
    @Override
    public int getMonth() {
        return date.getMonthValue();
    }

    /**
     * 获取阳历日
     * 
     * @return
     */
    @Override
    public int getDay() {
        return date.getDayOfMonth();
    }

    /**
     * 是否闰年
     * 
     * @return
     */
    @Override
    public boolean isLeap() {
        return date.isLeapYear();
    }

    @Override
    public LocalDate getDate() {
        return date;
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
        SolarDate that = (SolarDate) object;
        return Objects.equals(this.date, that.date);
    }

    @Override
    public String toString() {
        return "SolarDate [" + date.toString() + "]";
    }

}
