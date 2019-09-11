package com.jstarcraft.core.communication.session;

/**
 * 会话接收者
 * 
 * @author Birdy
 *
 * @param <T>
 */
public interface SessionReceiver<T> {

    /**
     * 拉出会话(配合调度器使用)
     * 
     * @return
     */
    CommunicationSession<T> pullSession();

    /**
     * 等待处理的会话数量
     * 
     * @return
     */
    int getReceiveSize();

}
