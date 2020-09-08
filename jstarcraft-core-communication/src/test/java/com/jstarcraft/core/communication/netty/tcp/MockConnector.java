package com.jstarcraft.core.communication.netty.tcp;

import java.util.concurrent.atomic.AtomicInteger;

import com.jstarcraft.core.common.lifecycle.LifecycleState;
import com.jstarcraft.core.communication.message.CommunicationMessage;
import com.jstarcraft.core.communication.netty.NettyConnector;

import io.netty.channel.Channel;

public class MockConnector implements NettyConnector<Channel> {

    private AtomicInteger count = new AtomicInteger();

    private CommunicationMessage message;

    @Override
    public void checkData(Channel context, CommunicationMessage message) {
        count.incrementAndGet();
        this.message = message;
    }

    @Override
    public LifecycleState getState() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void start() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void stop() {
        throw new UnsupportedOperationException();
    }

    public int getCount() {
        return count.get();
    }

    public CommunicationMessage getMessage() {
        return message;
    }

}
