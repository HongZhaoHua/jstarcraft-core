package com.jstarcraft.core.common.selection.xpath;

import java.io.DataInputStream;
import java.io.InputStream;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Assert;
import org.junit.Test;

import com.jstarcraft.core.common.selection.xpath.jsoup.HtmlElementNode;
import com.jstarcraft.core.utility.StringUtility;

public class XpathTestCase {

    @Test
    public void testXpath() {
        try (InputStream stream = XpathTestCase.class.getResourceAsStream("xpath.html"); DataInputStream buffer = new DataInputStream(stream)) {
            Document document = Jsoup.parse(stream, StringUtility.CHARSET.name(), StringUtility.EMPTY);
            HtmlElementNode root = new HtmlElementNode(document);
            JsoupXpathSelector selector;

            selector = new JsoupXpathSelector("//@id");
            Assert.assertEquals(6, selector.selectContent(root).size());

            selector = new JsoupXpathSelector("//*[text()='title']");
            Assert.assertEquals(1, selector.selectContent(root).size());
            
            selector = new JsoupXpathSelector("//*[@id='container']");
            Assert.assertEquals(1, selector.selectContent(root).size());
            
            selector = new JsoupXpathSelector("//*[@id='container']/self::*");
            Assert.assertEquals(1, selector.selectContent(root).size());
            
            selector = new JsoupXpathSelector("//*[@id='container']/parent::*");
            Assert.assertEquals(1, selector.selectContent(root).size());
            
            selector = new JsoupXpathSelector("//*[@id='container']/ancestor::*");
            Assert.assertEquals(3, selector.selectContent(root).size());
            
            selector = new JsoupXpathSelector("//*[@id='container']/child::*");
            Assert.assertEquals(5, selector.selectContent(root).size());
            
            selector = new JsoupXpathSelector("//*[@id='container']/descendant::*");
            Assert.assertEquals(5, selector.selectContent(root).size());
        } catch (Exception exception) {
            throw new IllegalArgumentException(exception);
        }
    }

}
