package com.jstarcraft.core.communication.session;

import java.net.InetSocketAddress;
import java.util.List;

import com.jstarcraft.core.utility.StringUtility;

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

    static String address2Key(InetSocketAddress address) {
        String key = address.getAddress().getHostAddress() + StringUtility.COLON + address.getPort();
        return key;
    }

    static InetSocketAddress key2Address(String key) {
        InetSocketAddress address;
        int colonIndex = key.lastIndexOf(StringUtility.COLON);
        if (colonIndex > 0) {
            String host = key.substring(0, colonIndex);
            int port = Integer.parseInt(key.substring(colonIndex + 1));
            if (!StringUtility.ASTERISK.equals(host)) {
                address = new InetSocketAddress(host, port);
            } else {
                address = new InetSocketAddress(port);
            }
        } else {
            int port = Integer.parseInt(key.substring(colonIndex + 1));
            address = new InetSocketAddress(port);
        }
        return address;
    }

    /**
     * 创建会话
     * 
     * @param key
     * @param context
     * @return
     */
    CommunicationSession<T> attachSession(String key, T context);

    default CommunicationSession<T> attachSession(InetSocketAddress address, T context) {
        return attachSession(address2Key(address), context);
    }

    /**
     * 删除会话
     * 
     * @param key
     * @return
     */
    boolean detachSession(String key);

    default boolean detachSession(InetSocketAddress address) {
        return detachSession(address2Key(address));
    }

    /**
     * 获取会话
     * 
     * @param key
     * @return
     */
    CommunicationSession<T> getSession(String key);

    default CommunicationSession<T> getSession(InetSocketAddress address) {
        return getSession(address2Key(address));
    }

    /**
     * 获取所有匹配的会话
     * 
     * @param matcher
     * @return
     */
    List<CommunicationSession<T>> getSessions(SessionMatcher<T> matcher);

}
