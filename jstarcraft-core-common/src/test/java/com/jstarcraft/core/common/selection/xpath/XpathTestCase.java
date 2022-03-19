package com.jstarcraft.core.common.selection.xpath;

import java.io.DataInputStream;
import java.io.InputStream;

import org.jaxen.Navigator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Assert;
import org.junit.Test;

import com.jstarcraft.core.common.selection.xpath.jsoup.HtmlElementNode;
import com.jstarcraft.core.common.selection.xpath.jsoup.HtmlNavigator;
import com.jstarcraft.core.utility.StringUtility;

public class XpathTestCase {

    @Test
    public void testXpath() {
        try (InputStream stream = XpathTestCase.class.getResourceAsStream("xpath.html"); DataInputStream buffer = new DataInputStream(stream)) {
            Navigator navigator = HtmlNavigator.getInstance();
            Document document = Jsoup.parse(stream, StringUtility.CHARSET.name(), StringUtility.EMPTY);
            HtmlElementNode root = new HtmlElementNode(document);
            JaxenXpathSelector selector;

            selector = new JaxenXpathSelector("//@id", navigator);
            Assert.assertEquals(6, selector.selectMultiple(root).size());

            selector = new JaxenXpathSelector("//*[text()='title']", navigator);
            Assert.assertEquals(1, selector.selectMultiple(root).size());

            selector = new JaxenXpathSelector("//*[@id='container']", navigator);
            Assert.assertEquals(1, selector.selectMultiple(root).size());

            selector = new JaxenXpathSelector("//*[@id='container']/self::*", navigator);
            Assert.assertEquals(1, selector.selectMultiple(root).size());

            selector = new JaxenXpathSelector("//*[@id='container']/parent::*", navigator);
            Assert.assertEquals(1, selector.selectMultiple(root).size());

            selector = new JaxenXpathSelector("//*[@id='container']/ancestor::*", navigator);
            Assert.assertEquals(3, selector.selectMultiple(root).size());

            selector = new JaxenXpathSelector("//*[@id='container']/child::*", navigator);
            Assert.assertEquals(5, selector.selectMultiple(root).size());

            selector = new JaxenXpathSelector("//*[@id='container']/descendant::*", navigator);
            Assert.assertEquals(5, selector.selectMultiple(root).size());
        } catch (Exception exception) {
            throw new IllegalArgumentException(exception);
        }
    }

}
