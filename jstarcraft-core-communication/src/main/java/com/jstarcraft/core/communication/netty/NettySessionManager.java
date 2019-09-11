package com.jstarcraft.core.communication.netty;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jstarcraft.core.communication.session.CommunicationSession;
import com.jstarcraft.core.communication.session.SessionManager;
import com.jstarcraft.core.communication.session.SessionMatcher;

/**
 * 基于Netty的会话管理器
 * 
 * @author Birdy
 */
public class NettySessionManager<T> implements SessionManager<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(NettySessionManager.class);

    /** 会话映射 */
    private final ConcurrentHashMap<String, CommunicationSession<T>> sessions = new ConcurrentHashMap<>();

    @Override
    public CommunicationSession<T> attachSession(String key, T context) {
        CommunicationSession<T> session = NettySession.instanceOf(key, context);
        return (sessions.putIfAbsent(key, session) == null) ? session : null;
    }

    @Override
    public boolean detachSession(String key) {
        return sessions.remove(key) != null;
    }

    @Override
    public CommunicationSession<T> getSession(String key) {
        CommunicationSession<T> session = sessions.get(key);
        return session;
    }

    @Override
    public List<CommunicationSession<T>> getSessions(SessionMatcher<T> matcher) {
        if (matcher == null) {
            return new ArrayList<>(sessions.values());
        }
        LinkedList<CommunicationSession<T>> values = new LinkedList<>();
        for (CommunicationSession<T> session : sessions.values()) {
            if (matcher.match(session)) {
                values.addLast(session);
            }
        }
        return values;
    }

}
