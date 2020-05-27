package com.jstarcraft.core.communication.netty;

import java.net.InetSocketAddress;
import java.util.Collection;

import com.jstarcraft.core.communication.session.CommunicationSession;

/**
 * Netty客户端连接器
 * 
 * @author Birdy
 *
 * @param <K>
 * @param <T>
 */
public interface NettyClientConnector<T> extends NettyConnector<T> {

    /**
     * 打开会话
     * 
     * @param key
     * @param address
     * @param wait
     * @return
     */
    CommunicationSession<T> open(InetSocketAddress address, long wait);

    /**
     * 关闭会话
     * 
     * @param key
     * @return
     */
    void close(InetSocketAddress address);

    /**
     * 获取通道地址
     * 
     * @return
     */
    Collection<InetSocketAddress> getAddresses();

}
