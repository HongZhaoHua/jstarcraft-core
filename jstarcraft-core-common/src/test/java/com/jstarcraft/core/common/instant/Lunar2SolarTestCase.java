package com.jstarcraft.core.common.instant;

import java.time.LocalDate;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import com.jstarcraft.core.common.instant.LunarDate;
import com.jstarcraft.core.common.instant.SolarDate;

public class Lunar2SolarTestCase {

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
    public void testLunar2Solar() {
        Assert.assertThat(new LunarDate(1900, false, 1, 1).getSolar(), CoreMatchers.equalTo(new SolarDate(1900, 1, 31)));
        Assert.assertThat(new LunarDate(2010, false, 1, 1).getSolar(), CoreMatchers.equalTo(new SolarDate(2010, 2, 14)));

        // 测试阴历闰月的情况
        Assert.assertThat(new LunarDate(2020, true, 4, 1).getSolar(), CoreMatchers.equalTo(new SolarDate(2020, 5, 23)));
        Assert.assertThat(new LunarDate(2020, false, 5, 1).getSolar(), CoreMatchers.equalTo(new SolarDate(2020, 6, 21)));

        Assert.assertThat(new SolarDate(2010, 2, 14).getLunar(), CoreMatchers.equalTo(new LunarDate(2010, false, 1, 1)));
        Assert.assertThat(new SolarDate(2011, 1, 4).getLunar(), CoreMatchers.equalTo(new LunarDate(2010, false, 12, 1)));
    }

}
