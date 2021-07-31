package com.jstarcraft.core.common.option;

import java.io.InputStream;
import java.util.Iterator;

import org.junit.Assert;
import org.junit.Test;

import com.jstarcraft.core.common.io.IoUtility;
import com.jstarcraft.core.utility.StringUtility;

public class XmlOptionTestCase {

    @Test
    public void test() throws Exception {
        try (InputStream stream = XmlOptionTestCase.class.getResourceAsStream("Biology.xml")) {
            String xml = IoUtility.toString(stream, StringUtility.CHARSET);
            XmlOption option = new XmlOption(xml);
            int index = 0;
            Iterator<String> keys = option.getKeys();
            while (keys.hasNext()) {
                keys.next();
                index++;
            }
            Assert.assertEquals(9, index);

            Assert.assertEquals("0", option.getString("biology.id"));
            Assert.assertEquals("1", option.getString("biology.zoology.biology[0].id"));
            Assert.assertEquals("Cat", option.getString("biology.zoology.biology[0].name"));
            Assert.assertEquals("2", option.getString("biology.zoology.biology[1].id"));
            Assert.assertEquals("Dog", option.getString("biology.zoology.biology[1].name"));
            Assert.assertEquals("3", option.getString("biology.phytology.biology[0].id"));
            Assert.assertEquals("Tree", option.getString("biology.phytology.biology[0].name"));
            Assert.assertEquals("4", option.getString("biology.phytology.biology[1].id"));
            Assert.assertEquals("Shrub", option.getString("biology.phytology.biology[1].name"));
        }
    }

}
