package com.jstarcraft.core.event.amqp;

import org.apache.qpid.server.exchange.topic.TopicParser;
import org.junit.Assert;
import org.junit.Test;

public class TopicParserTestCase {

    @Test
    public void testRoute() {
        TopicParser parser = new TopicParser();
        parser.addBinding("left.middle.right", new MockMatcher("left.middle.right"));
        parser.addBinding("*.middle.*", new MockMatcher("*.middle.*"));
        parser.addBinding("left.*.*", new MockMatcher("left.*.*"));
        parser.addBinding("*.*.right", new MockMatcher("*.*.right"));
        parser.addBinding("#.middle.#", new MockMatcher("#.middle.#"));
        Assert.assertEquals(5, parser.parse("left.middle.right").size());
        Assert.assertEquals(1, parser.parse("first.left.middle.right.last").size());
        Assert.assertEquals(2, parser.parse("first.middle.last").size());
    }

}
