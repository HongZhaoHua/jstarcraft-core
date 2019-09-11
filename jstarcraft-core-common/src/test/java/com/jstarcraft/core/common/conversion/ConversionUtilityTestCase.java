package com.jstarcraft.core.common.conversion;

import org.junit.Assert;
import org.junit.Test;

import com.jstarcraft.core.common.conversion.csv.ConversionUtility;

public class ConversionUtilityTestCase {

    @Test
    public void testConverter() {
        ConversionUtility.bindConverter(new String2ClassConverter());
        Class<?> clazz = ConversionUtility.convert("java.lang.String", Class.class);
        Assert.assertEquals(String.class, clazz);

        ConversionUtility.unbindConverter(String.class, Class.class);
        try {
            ConversionUtility.convert("java.lang.String", Class.class);
            Assert.fail();
        } catch (Exception exception) {
        }
    }

}
