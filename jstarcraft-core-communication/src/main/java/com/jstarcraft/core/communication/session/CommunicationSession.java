package com.jstarcraft.core.communication.session;

import java.time.Instant;

import com.jstarcraft.core.communication.command.CommandDispatcher;
import com.jstarcraft.core.communication.message.CommunicationMessage;

/**
 * 通讯会话
 * 
 * @author Birdy
 */
public interface CommunicationSession<T> {

    /**
     * 获取会话键
     * 
     * @return
     */
    String getKey();

    /**
     * 获取会话上下文
     * 
     * @return
     */
    T getContext();

    /**
     * 获取会话变更时间
     * 
     * @return
     */
    Instant getUpdatedAt();

    /**
     * 是否存在接收的消息({@link CommandDispatcher})
     * 
     * @return
     */
    boolean hasReceiveMessage();

    /**
     * 推入接收的信息({@link SessionReceiver})
     * 
     * @param message
     */
    void pushReceiveMessage(CommunicationMessage message);

    /**
     * 拉出接收的信息({@link CommandDispatcher})
     * 
     * @param message
     */
    CommunicationMessage pullReceiveMessage();

    /**
     * 是否存在发送的消息({@link CommandDispatcher})
     * 
     * @return
     */
    boolean hasSendMessage();

    /**
     * 推入发送的信息(@link CommandDispatcher)
     * 
     * @param message
     */
    void pushSendMessage(CommunicationMessage message);

    /**
     * 拉出发送的信息({@link SessionSender})
     * 
     * @param message
     */
    CommunicationMessage pullSendMessage();

}
