package com.jstarcraft.core.common.selection.css;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import jodd.jerry.Jerry;

public class JerryTestCase {

    @Test
    public void test() {
        String html = "<head><title>test &amp; title</title><body><div>test &amp; <b>body</b></div></body>";
        Jerry doc = Jerry.of(html);
        Jerry title = doc.find("title");
        assertEquals("test &amp; title", title.eq(0).html());
        assertEquals("test & title", title.eq(0).text());
        Jerry div = doc.find("div");
        assertEquals("test &amp; <b>body</b>", div.eq(0).html());
        assertEquals("test & body", div.eq(0).text());
    }

}
