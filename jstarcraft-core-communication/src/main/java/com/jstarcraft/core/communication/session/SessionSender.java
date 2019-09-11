package com.jstarcraft.core.communication.session;

/**
 * 会话发送者
 * 
 * @author Birdy
 *
 * @param <T>
 */
public interface SessionSender<T> {

    /**
     * 推入会话(配合调度器使用)
     * 
     * @return
     */
    void pushSession(CommunicationSession<T> session);

    /**
     * 等待处理的会话数量
     * 
     * @return
     */
    int getSendSize();

}
