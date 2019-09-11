package com.jstarcraft.core.communication.netty;

import java.time.Instant;
import java.util.concurrent.LinkedBlockingDeque;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.jstarcraft.core.communication.message.CommunicationMessage;
import com.jstarcraft.core.communication.session.CommunicationSession;

/**
 * 基于Netty的会话
 * 
 * <pre>
 * 由于维护着接受和发送消息队列,可以有许多有趣的实现.例如持久Session或者游标Session
 * </pre>
 * 
 * @author Birdy
 *
 */
public class NettySession<T> implements CommunicationSession<T> {

    /** 索引键 */
    private String key;
    /** 通信通道 */
    private T context;
    /** 接收的消息队列 */
    private LinkedBlockingDeque<CommunicationMessage> receiveMessageQueue = new LinkedBlockingDeque<>();
    /** 发送的消息队列 */
    private LinkedBlockingDeque<CommunicationMessage> sendMessageQueue = new LinkedBlockingDeque<>();
    /** 变更时间 */
    private Instant updatedAt;

    private NettySession() {
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public T getContext() {
        return context;
    }

    @Override
    public boolean hasReceiveMessage() {
        return !receiveMessageQueue.isEmpty();
    }

    @Override
    public boolean hasSendMessage() {
        return !sendMessageQueue.isEmpty();
    }

    @Override
    public void pushReceiveMessage(CommunicationMessage message) {
        updatedAt = Instant.now();
        receiveMessageQueue.offerLast(message);
    }

    @Override
    public CommunicationMessage pullReceiveMessage() {
        return receiveMessageQueue.pollFirst();
    }

    @Override
    public void pushSendMessage(CommunicationMessage message) {
        sendMessageQueue.offerLast(message);
    }

    @Override
    public CommunicationMessage pullSendMessage() {
        updatedAt = Instant.now();
        return sendMessageQueue.pollFirst();
    }

    @Override
    public Instant getUpdatedAt() {
        return updatedAt;
    }

    static <T> NettySession<T> instanceOf(String key, T context) {
        NettySession<T> session = new NettySession<>();
        session.key = key;
        session.context = context;
        session.updatedAt = Instant.now();
        return session;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object)
            return true;
        if (object == null)
            return false;
        if (getClass() != object.getClass())
            return false;
        NettySession that = (NettySession) object;
        EqualsBuilder equal = new EqualsBuilder();
        equal.append(this.key, that.key);
        equal.append(this.context, that.context);
        return equal.isEquals();
    }

    @Override
    public int hashCode() {
        HashCodeBuilder hash = new HashCodeBuilder();
        hash.append(key);
        hash.append(context);
        return hash.toHashCode();
    }

    @Override
    public String toString() {
        ToStringBuilder string = new ToStringBuilder(this);
        string.append(key);
        string.append(updatedAt);
        return string.toString();
    }

}
