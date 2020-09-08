package com.jstarcraft.core.communication.netty;

import com.jstarcraft.core.common.lifecycle.LifecycleState;
import com.jstarcraft.core.communication.message.CommunicationMessage;

/**
 * Netty连接器
 * 
 * @author Birdy
 *
 * @param <T>
 */
public interface NettyConnector<T> {

    void checkData(T context, CommunicationMessage message);

    LifecycleState getState();

    void start();

    void stop();

}
