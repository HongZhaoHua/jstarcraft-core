package com.jstarcraft.core.common.instant;

import java.time.LocalDate;

import org.junit.Assert;
import org.junit.Test;

public class IslamicDateTestCase {

    @Test
    public void testLeap() {
        Assert.assertTrue(new IslamicDate(1441, 1, 1).isLeap());
        Assert.assertFalse(new IslamicDate(1442, 1, 1).isLeap());
    }

    @Test
    public void testGetDaySize() {
        // 测试年份天数
        Assert.assertEquals(355, IslamicDate.getDaySize(1441));
        Assert.assertEquals(354, IslamicDate.getDaySize(1442));
        // 测试月份天数
        Assert.assertEquals(30, IslamicDate.getDaySize(1441, 1));
        Assert.assertEquals(29, IslamicDate.getDaySize(1442, 1));
    }

    @Test
    public void testIslamic() {
        Assert.assertEquals(LocalDate.of(2019, 8, 31), new IslamicDate(1441, 1, 1).getDate());
        Assert.assertEquals(LocalDate.of(2019, 9, 29), new IslamicDate(1441, 1, 30).getDate());
        Assert.assertEquals(LocalDate.of(2020, 8, 20), new IslamicDate(1442, 1, 1).getDate());
        Assert.assertEquals(LocalDate.of(2020, 9, 17), new IslamicDate(1442, 1, 29).getDate());
    }

}
