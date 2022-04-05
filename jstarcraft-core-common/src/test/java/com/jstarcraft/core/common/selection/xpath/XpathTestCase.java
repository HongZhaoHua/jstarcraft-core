package com.jstarcraft.core.common.selection.xpath;

import java.io.DataInputStream;
import java.io.File;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.jaxen.Navigator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Assert;
import org.junit.Test;
import org.seimicrawler.xpath.JXDocument;

import com.jstarcraft.core.common.selection.xpath.jsoup.HtmlElementNode;
import com.jstarcraft.core.common.selection.xpath.jsoup.HtmlNavigator;
import com.jstarcraft.core.utility.StringUtility;

public class XpathTestCase {

    @Test
    public void testJaxen() {
        try (InputStream stream = XpathTestCase.class.getResourceAsStream("xpath.html"); DataInputStream buffer = new DataInputStream(stream)) {
            Navigator navigator = HtmlNavigator.getInstance();
            Document document = Jsoup.parse(stream, StringUtility.CHARSET.name(), StringUtility.EMPTY);
            HtmlElementNode root = new HtmlElementNode(document);
            JaxenXpathSelector<HtmlElementNode> selector;

            selector = new JaxenXpathSelector<>("//@id", navigator);
            Assert.assertEquals(6, selector.selectMultiple(root).size());

            selector = new JaxenXpathSelector<>("//*[text()='title']", navigator);
            Assert.assertEquals(1, selector.selectMultiple(root).size());

            selector = new JaxenXpathSelector<>("//*[@id='container']", navigator);
            Assert.assertEquals(1, selector.selectMultiple(root).size());

            selector = new JaxenXpathSelector<>("//*[@id='container']/self::*", navigator);
            Assert.assertEquals(1, selector.selectMultiple(root).size());

            selector = new JaxenXpathSelector<>("//*[@id='container']/parent::*", navigator);
            Assert.assertEquals(1, selector.selectMultiple(root).size());

            // 此处包括document,html,body
            selector = new JaxenXpathSelector<>("//*[@id='container']/ancestor::*", navigator);
            Assert.assertEquals(3, selector.selectMultiple(root).size());

            selector = new JaxenXpathSelector<>("//*[@id='container']/child::*", navigator);
            Assert.assertEquals(5, selector.selectMultiple(root).size());

            selector = new JaxenXpathSelector<>("//*[@id='container']/descendant::*", navigator);
            Assert.assertEquals(5, selector.selectMultiple(root).size());
        } catch (Exception exception) {
            throw new IllegalArgumentException(exception);
        }
    }

    @Test
    public void testJsoupXpath() throws Exception {
        File file = new File(XpathTestCase.class.getResource("xpath.html").toURI());
        String html = FileUtils.readFileToString(file, StringUtility.CHARSET);
        Document document = Jsoup.parse(html);

        Assert.assertEquals(6, JXDocument.create(document).selN("//@id").size());

        Assert.assertEquals(1, JXDocument.create(document).selN("//*[text()='title']").size());

        Assert.assertEquals(1, JXDocument.create(document).selN("//*[@id='container']").size());

        Assert.assertEquals(1, JXDocument.create(document).selN("//*[@id='container']/self::*").size());

        Assert.assertEquals(1, JXDocument.create(document).selN("//*[@id='container']/parent::*").size());

        // 此处包括html,body
        Assert.assertEquals(2, JXDocument.create(document).selN("//*[@id='container']/ancestor::*").size());

        Assert.assertEquals(5, JXDocument.create(document).selN("//*[@id='container']/child::*").size());

        Assert.assertEquals(5, JXDocument.create(document).selN("//*[@id='container']/descendant::*").size());
    }

}
