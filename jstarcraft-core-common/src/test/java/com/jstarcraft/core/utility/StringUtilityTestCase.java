package com.jstarcraft.core.utility;

import java.util.HashMap;

import org.junit.Assert;
import org.junit.Test;

public class StringUtilityTestCase {

    @Test
    public void testFormat() throws Exception {
        String template = "${province}${city}";
        HashMap<String, String> paramters = new HashMap<>();
        paramters.put("province", "广东省");
        paramters.put("city", "广州市");
        Assert.assertEquals("广东省广州市", StringUtility.format(template, paramters));
    }

}
