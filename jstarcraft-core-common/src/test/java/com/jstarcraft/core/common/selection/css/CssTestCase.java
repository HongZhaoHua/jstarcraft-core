package com.jstarcraft.core.common.selection.css;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;

import jodd.jerry.Jerry;
import jodd.lagarto.dom.Node;

public class CssTestCase {

    private String html = "<head><title>test &amp; title</title><body><div>test &amp; <b>body</b></div></body>";

    @Test
    public void testJodd() {
        Node document = Jerry.of(html).get(0);
        {
            JoddCssSelector selector = new JoddCssSelector("title");
            List<Node> nodes = selector.selectMultiple(document);
            assertEquals(1, nodes.size());
            assertEquals("test &amp; title", nodes.get(0).getInnerHtml());
            assertEquals("test & title", nodes.get(0).getTextContent());
        }
        {
            JoddCssSelector selector = new JoddCssSelector("div");
            List<Node> nodes = selector.selectMultiple(document);
            assertEquals(1, nodes.size());
            assertEquals("test &amp; <b>body</b>", nodes.get(0).getInnerHtml());
            assertEquals("test & body", nodes.get(0).getTextContent());
        }
    }

    @Test
    public void testJsoup() {
        Document document = Jsoup.parse(html);
        {
            JsoupCssSelector selector = new JsoupCssSelector("title");
            List<Element> elements = selector.selectMultiple(document);
            assertEquals(1, elements.size());
            assertEquals("test &amp; title", elements.get(0).html());
            assertEquals("test & title", elements.get(0).text());
        }
        {
            JsoupCssSelector selector = new JsoupCssSelector("div");
            List<Element> elements = selector.selectMultiple(document);
            assertEquals(1, elements.size());
            assertEquals("test &amp; <b>body</b>", elements.get(0).html());
            assertEquals("test & body", elements.get(0).text());
        }
    }

}
