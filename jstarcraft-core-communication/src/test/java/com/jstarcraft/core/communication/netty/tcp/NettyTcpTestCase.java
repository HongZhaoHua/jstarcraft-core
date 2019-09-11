package com.jstarcraft.core.communication.netty.tcp;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.jstarcraft.core.communication.exception.CommunicationException;
import com.jstarcraft.core.communication.netty.NettyTestCase;

import io.netty.channel.Channel;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class NettyTcpTestCase extends NettyTestCase<Channel> {

    @Override
    public void testConnect() throws Exception {
        nettyClientConnector.open(clientAddress, 5000L);
        Thread.sleep(1000L);
        Assert.assertThat(serverSessionManager.getSessions(null).size(), CoreMatchers.equalTo(1));

        try {
            nettyClientConnector.open(clientAddress, 5000L);
            Assert.fail();
        } catch (CommunicationException exception) {
        }
        Assert.assertThat(serverSessionManager.getSessions(null).size(), CoreMatchers.equalTo(1));

        nettyClientConnector.close(clientAddress);
        // 此处依赖于连接器清理时间.
        Thread.sleep(5500L);
        Assert.assertThat(serverSessionManager.getSessions(null).size(), CoreMatchers.equalTo(0));
    }

}
