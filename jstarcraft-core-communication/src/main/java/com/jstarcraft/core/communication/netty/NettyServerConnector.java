package com.jstarcraft.core.communication.netty;

/**
 * Netty服务端连接器
 * 
 * @author Birdy
 *
 * @param <T>
 */
public interface NettyServerConnector<T> extends NettyConnector<T> {

    /**
     * 获取通道地址
     * 
     * @return
     */
    String getAddress();

}
