package com.jstarcraft.core.communication.session;

/**
 * 会话匹配器
 * 
 * @author Birdy
 *
 * @param <K>
 * @param <T>
 */
public interface SessionMatcher<T> {

    /**
     * 指定的会话是否匹配
     * 
     * @param session
     * @return
     */
    boolean match(CommunicationSession<T> session);

}
