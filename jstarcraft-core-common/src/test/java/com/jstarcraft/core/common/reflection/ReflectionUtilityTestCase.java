package com.jstarcraft.core.common.reflection;

import org.junit.Assert;
import org.junit.Test;

import com.jstarcraft.core.utility.ReflectionUtility;

public class ReflectionUtilityTestCase {

    @Test
    public void testCopyInstance() {
        FromClass from = new FromClass("Birdy");
        Assert.assertEquals("Birdy", from.getId());
        ToClass to = new ToClass(null);
        Assert.assertNull(to.getId());
        ReflectionUtility.copyInstance(from, to);
        Assert.assertEquals(from.getId(), to.getId());
    }

}
