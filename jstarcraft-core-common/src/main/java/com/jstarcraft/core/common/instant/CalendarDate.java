package com.jstarcraft.core.common.instant;

import java.time.LocalDate;

/**
 * 历法日期
 * 
 * @author Birdy
 *
 */
public interface CalendarDate extends Comparable<CalendarDate> {

    /**
     * 获取历法类型
     * 
     * @return
     */
    CalendarType getType();

    /**
     * 获取历法年
     * 
     * @return
     */
    int getYear();

    /***
     * 获取历法月
     * 
     * @return
     */
    int getMonth();

    /**
     * 获取历法日
     * 
     * @return
     */
    int getDay();

    /**
     * 是否闰
     * 
     * @return
     */
    boolean isLeap();

    /**
     * 获取日期
     * 
     * @return
     */
    LocalDate getDate();

    /**
     * 比较日期
     */
    default int compareTo(CalendarDate that) {
        return this.getDate().compareTo(that.getDate());
    }

}
