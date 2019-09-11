package com.jstarcraft.core.communication.command;

import java.util.concurrent.ConcurrentLinkedQueue;

import com.jstarcraft.core.communication.message.CommunicationMessage;
import com.jstarcraft.core.communication.netty.NettySessionManager;
import com.jstarcraft.core.communication.session.SessionReceiver;
import com.jstarcraft.core.communication.session.SessionSender;
import com.jstarcraft.core.communication.session.CommunicationSession;

import io.netty.channel.Channel;

public class MockSessionTransmitter implements SessionReceiver<Channel>, SessionSender<Channel> {

    private NettySessionManager sessionManager;

    private ConcurrentLinkedQueue<CommunicationSession<Channel>> sessions = new ConcurrentLinkedQueue<>();

    MockSessionTransmitter(NettySessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public CommunicationSession<Channel> pullSession() {
        return sessions.poll();
    }

    @Override
    public int getReceiveSize() {
        return sessions.size();
    }

    @Override
    public void pushSession(CommunicationSession<Channel> session) {
        CommunicationMessage message = session.pullSendMessage();
        if (message != null) {
            session = sessionManager.getSession(session.getKey());
            session.pushReceiveMessage(message);
        }
        sessions.offer(session);
    }

    @Override
    public int getSendSize() {
        return sessions.size();
    }

}
