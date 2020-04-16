package com.jstarcraft.core.common.instant;

import java.time.LocalDate;

import org.junit.Assert;
import org.junit.Test;

public class LunarDateTestCase {

    @Test
    public void testLeap() {
        LunarDate.getDaySize(1900, true, 8);
        try {
            LunarDate.getDaySize(2010, true, 8);
            Assert.fail();
        } catch (IllegalArgumentException exception) {
        }
    }

    @Test
    public void testGetDaySize() {
        Assert.assertEquals(30, LunarDate.getDaySize(2010, false, 1));
        Assert.assertEquals(29, LunarDate.getDaySize(2010, false, 2));
        Assert.assertEquals(30, LunarDate.getDaySize(2020, false, 4));
        Assert.assertEquals(29, LunarDate.getDaySize(2020, true, 4));
        Assert.assertEquals(30, LunarDate.getDaySize(2020, false, 5));
    }

    @Test
    public void testLunar() {
        Assert.assertEquals(LocalDate.of(1900, 1, 31), new LunarDate(1900, false, 1, 1).getDate());
        Assert.assertEquals(LocalDate.of(2010, 2, 14), new LunarDate(2010, false, 1, 1).getDate());

        // 测试阴历闰月的情况
        Assert.assertEquals(LocalDate.of(2020, 5, 23), new LunarDate(2020, true, 4, 1).getDate());
        Assert.assertEquals(LocalDate.of(2020, 6, 21), new LunarDate(2020, false, 5, 1).getDate());

        Assert.assertEquals(LocalDate.of(2010, 2, 14), new LunarDate(2010, false, 1, 1).getDate());
        Assert.assertEquals(LocalDate.of(2011, 1, 4), new LunarDate(2010, false, 12, 1).getDate());

        // 测试阴历索引的情况
        Assert.assertEquals(LocalDate.of(2020, 4, 23), new LunarDate(2020, 4, 1).getDate());
        Assert.assertEquals(LocalDate.of(2020, 5, 23), new LunarDate(2020, 5, 1).getDate());
        Assert.assertEquals(LocalDate.of(2020, 6, 21), new LunarDate(2020, 6, 1).getDate());
        Assert.assertEquals(4, new LunarDate(2020, 4, 1).getIndex());
        Assert.assertEquals(5, new LunarDate(2020, 5, 1).getIndex());
        Assert.assertEquals(6, new LunarDate(2020, 6, 1).getIndex());
    }

}
