package com.jstarcraft.core.common.instant;

import java.time.LocalDate;

import org.junit.Assert;
import org.junit.Test;

public class LunisolarDateTestCase {

    @Test
    public void testLeap() {
        LunisolarDate.getDaySize(1900, true, 8);
        try {
            LunisolarDate.getDaySize(2010, true, 8);
            Assert.fail();
        } catch (IllegalArgumentException exception) {
        }

        // 1944年的闰4月有30天
        Assert.assertEquals(LocalDate.of(1944, 6, 20), new LunisolarDate(1944, true, 4, 30).getDate());
    }

    @Test
    public void testGetDaySize() {
        Assert.assertEquals(30, LunisolarDate.getDaySize(2010, false, 1));
        Assert.assertEquals(29, LunisolarDate.getDaySize(2010, false, 2));
        Assert.assertEquals(30, LunisolarDate.getDaySize(2020, false, 4));
        Assert.assertEquals(29, LunisolarDate.getDaySize(2020, true, 4));
        Assert.assertEquals(30, LunisolarDate.getDaySize(2020, false, 5));
    }

    @Test
    public void testLunisolar() {
        Assert.assertEquals(LocalDate.of(1900, 1, 31), new LunisolarDate(1900, false, 1, 1).getDate());
        Assert.assertEquals(LocalDate.of(2010, 2, 14), new LunisolarDate(2010, false, 1, 1).getDate());

        // 测试阴历闰月的情况
        Assert.assertEquals(LocalDate.of(2020, 5, 23), new LunisolarDate(2020, true, 4, 1).getDate());
        Assert.assertEquals(LocalDate.of(2020, 6, 21), new LunisolarDate(2020, false, 5, 1).getDate());

        Assert.assertEquals(LocalDate.of(2010, 2, 14), new LunisolarDate(2010, false, 1, 1).getDate());
        Assert.assertEquals(LocalDate.of(2011, 1, 4), new LunisolarDate(2010, false, 12, 1).getDate());

        // 测试阴历索引的情况
        Assert.assertEquals(LocalDate.of(2020, 4, 23), new LunisolarDate(2020, 4, 1).getDate());
        Assert.assertEquals(LocalDate.of(2020, 5, 23), new LunisolarDate(2020, 5, 1).getDate());
        Assert.assertEquals(LocalDate.of(2020, 6, 21), new LunisolarDate(2020, 6, 1).getDate());
        Assert.assertEquals(4, new LunisolarDate(2020, 4, 1).getIndex());
        Assert.assertEquals(5, new LunisolarDate(2020, 5, 1).getIndex());
        Assert.assertEquals(6, new LunisolarDate(2020, 6, 1).getIndex());
    }

}
