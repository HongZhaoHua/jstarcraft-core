package com.jstarcraft.core.communication.netty;

import com.jstarcraft.core.communication.CommunicationState;
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

	CommunicationState getState();

	void start();

	void stop();

}
