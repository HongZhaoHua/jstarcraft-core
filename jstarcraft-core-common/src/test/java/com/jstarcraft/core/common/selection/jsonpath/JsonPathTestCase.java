package com.jstarcraft.core.common.selection.jsonpath;

import java.io.DataInputStream;
import java.io.InputStream;

import org.junit.Assert;
import org.junit.Test;
import org.noear.snack.ONode;

import com.jstarcraft.core.common.io.IoUtility;
import com.jstarcraft.core.utility.StringUtility;

public class JsonPathTestCase {

    @Test
    public void testSnack3() {
        try (InputStream stream = JsonPathTestCase.class.getResourceAsStream("jsonpath.json"); DataInputStream buffer = new DataInputStream(stream)) {
            String json = IoUtility.toString(stream, StringUtility.CHARSET);
            ONode root = ONode.load(json);
            SnackJsonPathSelector selector;

            selector = new SnackJsonPathSelector("$[0]");
            Assert.assertEquals(1, selector.selectContent(root).size());

            selector = new SnackJsonPathSelector("$[0:3]");
            Assert.assertEquals(3, selector.selectContent(root).size());

            selector = new SnackJsonPathSelector("$[-3:0]");
            Assert.assertEquals(3, selector.selectContent(root).size());

            selector = new SnackJsonPathSelector("$..name");
            Assert.assertEquals(3, selector.selectContent(root).size());

            selector = new SnackJsonPathSelector("$[?(age > 10)]");
            Assert.assertEquals(2, selector.selectContent(root).size());

            selector = new SnackJsonPathSelector("$[?(age < 10)]");
            Assert.assertEquals(1, selector.selectContent(root).size());

            selector = new SnackJsonPathSelector("$[?(sex == 'true')]");
            Assert.assertEquals(2, selector.selectContent(root).size());

            selector = new SnackJsonPathSelector("$[?(sex == 'false')]");
            Assert.assertEquals(1, selector.selectContent(root).size());
        } catch (Exception exception) {
            throw new IllegalArgumentException(exception);
        }
    }

}
