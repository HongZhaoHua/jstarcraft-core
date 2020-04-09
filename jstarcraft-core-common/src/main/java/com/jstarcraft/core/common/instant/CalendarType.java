package com.jstarcraft.core.common.instant;

import java.time.LocalDate;
import java.util.function.Function;

/**
 * 历法类型
 * 
 * @author Birdy
 *
 */
public enum CalendarType {

    Islamic(IslamicDate::new, new IslamicDate(IslamicDate.MINIMUM_YEAR, 1, 1), new IslamicDate(IslamicDate.MAXIMUM_YEAR, 12, 30)),

    Lunar(LunarDate::new, new LunarDate(LunarDate.MINIMUM_YEAR, false, 1, 1), new LunarDate(LunarDate.MAXIMUM_YEAR, false, 12, 30)),

    Solar(SolarDate::new, new SolarDate(LocalDate.MIN), new SolarDate(LocalDate.MAX));

    /** 历法最小日期 */
    private final CalendarDate minimumDate;

    /** 历法最大日期 */
    private final CalendarDate maximumDate;

    private Function<LocalDate, CalendarDate> converter;

    CalendarType(Function<LocalDate, CalendarDate> converter, CalendarDate minimumDate, CalendarDate maximumDate) {
        this.converter = converter;
        this.minimumDate = minimumDate;
        this.maximumDate = maximumDate;
    }

    public CalendarDate getCalendarDate(LocalDate date) {
        return converter.apply(date);
    }

    public CalendarDate getMinimumDate() {
        return minimumDate;
    }

    public CalendarDate getMaximumDate() {
        return maximumDate;
    }

}
