package com.jstarcraft.core.event.amqp;

import org.apache.qpid.server.exchange.topic.TopicMatcherResult;

public class MockMatcher implements TopicMatcherResult {

    private String path;

    MockMatcher(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + path.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object)
            return true;
        if (object == null)
            return false;
        if (getClass() != object.getClass())
            return false;
        MockMatcher that = (MockMatcher) object;
        if (!this.path.equals(that.path))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return path;
    }

}
