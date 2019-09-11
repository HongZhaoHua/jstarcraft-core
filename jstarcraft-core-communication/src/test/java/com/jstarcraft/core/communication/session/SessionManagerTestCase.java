package com.jstarcraft.core.communication.session;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import com.jstarcraft.core.communication.netty.NettySession;
import com.jstarcraft.core.communication.netty.NettySessionManager;

import io.netty.channel.Channel;
import io.netty.channel.embedded.EmbeddedChannel;

public class SessionManagerTestCase {

    @Test
    public void testManager() {
        String address = "127.0.0.1:9999";
        EmbeddedChannel channel = new EmbeddedChannel();
        NettySessionManager<Channel> nettySessionManager = new NettySessionManager<>();

        // 创建会话
        CommunicationSession<Channel> session = nettySessionManager.attachSession(address, channel);
        Assert.assertNotNull(session);
        Assert.assertThat(nettySessionManager.getSession(address), CoreMatchers.equalTo(session));
        Assert.assertThat(session.getKey(), CoreMatchers.equalTo(address));
        Assert.assertThat(session.getContext(), CoreMatchers.equalTo(channel));
        Assert.assertNull(nettySessionManager.attachSession(address, channel));

        // 删除会话
        Assert.assertTrue(nettySessionManager.detachSession(address));
        Assert.assertNull(nettySessionManager.getSession(address));
        Assert.assertFalse(nettySessionManager.detachSession(address));
    }

}
