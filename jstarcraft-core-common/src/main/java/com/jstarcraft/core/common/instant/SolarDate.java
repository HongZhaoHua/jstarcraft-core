package com.jstarcraft.core.common.instant;

import java.time.LocalDate;

/**
 * 阳历日期
 * 
 * @author Birdy
 *
 */
public class SolarDate implements CalendarDate {

    private LocalDate date;

    public SolarDate(LocalDate date) {
        this.date = date;
    }

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

    public LocalDate getDate() {
        return date;
    }

    /**
     * 阳历转阴历
     * 
     * @return
     */
    public LunarDate getLunar() {
        return new LunarDate(date);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int hash = 1;
        hash = prime * hash + ((date == null) ? 0 : date.hashCode());
        return hash;
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
        if (this.date == null) {
            if (that.date != null)
                return false;
        } else if (!this.date.equals(that.date))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "SolarDate [" + date.toString() + "]";
    }

}
