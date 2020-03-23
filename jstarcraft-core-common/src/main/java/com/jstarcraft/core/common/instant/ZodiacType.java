package com.jstarcraft.core.common.instant;

import java.time.MonthDay;

/**
 * 星座类型
 * 
 * @author Birdy
 *
 */
public enum ZodiacType {

    /** 摩羯座 */
    Capricornus(10, MonthDay.of(12, 22), MonthDay.of(1, 19)),

    /** 水瓶座 */
    Aguarius(11, MonthDay.of(1, 20), MonthDay.of(2, 18)),

    /** 双鱼座 */
    Pisces(12, MonthDay.of(2, 19), MonthDay.of(3, 20)),

    /** 白羊座 */
    Aries(1, MonthDay.of(3, 21), MonthDay.of(4, 19)),

    /** 金牛座 */
    Taurus(2, MonthDay.of(4, 20), MonthDay.of(5, 20)),

    /** 双子座 */
    Genimi(3, MonthDay.of(5, 21), MonthDay.of(6, 21)),

    /** 巨蟹座 */
    Cancer(4, MonthDay.of(6, 22), MonthDay.of(7, 22)),

    /** 狮子座 */
    Leonis(5, MonthDay.of(7, 23), MonthDay.of(8, 22)),

    /** 处女座 */
    Virgo(6, MonthDay.of(8, 23), MonthDay.of(9, 22)),

    /** 天秤座 */
    Libra(7, MonthDay.of(9, 23), MonthDay.of(10, 23)),

    /** 天蝎座 */
    Scorpius(8, MonthDay.of(10, 24), MonthDay.of(11, 22)),

    /** 射手座 */
    Sagittarius(9, MonthDay.of(11, 23), MonthDay.of(12, 21));

    /** 第几宫 */
    private final int number;

    /** 开始月日(包含) */
    private final MonthDay from;

    /** 结束月日(包含) */
    private final MonthDay to;

    private ZodiacType(int number, MonthDay from, MonthDay to) {
        this.number = number;
        this.from = from;
        this.to = to;
    }

    public int getNumber() {
        return number;
    }

    public MonthDay getFrom() {
        return from;
    }

    public MonthDay getTo() {
        return to;
    }

    public static ZodiacType getZodiac(int month, int day) {
        return getZodiac(MonthDay.of(month, day));
    }

    public static ZodiacType getZodiac(MonthDay monthDay) {
        int month = monthDay.getMonthValue();
        int day = monthDay.getDayOfMonth();
        ZodiacType zodiac = values()[month - 1];
        return day <= zodiac.to.getDayOfMonth() ? zodiac : values()[month % 12];
    }

    public static ZodiacType getZodiac(int number) {
        if (number < 1 || number > 12) {
            throw new IllegalArgumentException();
        }
        ZodiacType zodiac = values()[(number - 2) % 12];
        return zodiac;
    }

}
