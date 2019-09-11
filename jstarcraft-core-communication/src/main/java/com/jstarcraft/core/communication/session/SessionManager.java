package com.jstarcraft.core.communication.session;

import java.util.List;

/**
 * 会话管理器
 * 
 * <pre>
 * 会话管理器管理{@link CommunicationSession}的生命周期.
 * 每个地址对应一个会话.
 * </pre>
 * 
 * @author Birdy
 */
public interface SessionManager<T> {

    /**
     * 创建会话
     * 
     * @param session
     * @return
     */
    CommunicationSession<T> attachSession(String key, T context);

    /**
     * 删除会话
     * 
     * @param key
     * @return
     */
    boolean detachSession(String key);

    /**
     * 获取会话
     * 
     * @param key
     * @return
     */
    CommunicationSession<T> getSession(String key);

    /**
     * 获取所有匹配的会话
     * 
     * @param matcher
     * @return
     */
    List<CommunicationSession<T>> getSessions(SessionMatcher<T> matcher);

}
