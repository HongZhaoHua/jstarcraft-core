package com.jstarcraft.core.common.instant;

import java.time.DateTimeException;

import org.junit.Assert;
import org.junit.Test;

public class ZodiacTypeTestCase {

    @Test
    public void testGetZodiac() {
        ZodiacType left = ZodiacType.getZodiac(7);
        ZodiacType right = ZodiacType.getZodiac(10, 23);
        Assert.assertEquals(left, right);
    }

    @Test
    public void testException() {
        try {
            ZodiacType.getZodiac(0);
            Assert.fail();
        } catch (IllegalArgumentException exception) {
        }
        try {
            ZodiacType.getZodiac(13);
            Assert.fail();
        } catch (IllegalArgumentException exception) {
        }
        ZodiacType.getZodiac(2, 29);
        try {
            ZodiacType.getZodiac(2, 30);
            Assert.fail();
        } catch (DateTimeException exception) {
        }
    }

}
