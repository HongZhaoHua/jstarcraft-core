package com.jstarcraft.core.communication.netty.udp;

import java.net.InetSocketAddress;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.jstarcraft.core.communication.command.MockServerInterface;
import com.jstarcraft.core.communication.exception.CommunicationException;
import com.jstarcraft.core.communication.netty.NettyTestCase;
import com.jstarcraft.core.communication.session.SessionManager;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class NettyUdpTestCase extends NettyTestCase<InetSocketAddress> {

    @Override
    public void testConnect() throws Exception {
        nettyClientConnector.open(clientAddress, 1000L);
        try {
            nettyClientConnector.open(clientAddress, 1000L);
            Assert.fail();
        } catch (CommunicationException exception) {
        }
        Assert.assertThat(serverSessionManager.getSessions(null).size(), CoreMatchers.equalTo(0));

        // UDP服务端在第一次收到消息才会创建会话
        MockServerInterface service = clientCommandManager.getProxy(MockServerInterface.class, SessionManager.address2Key(clientAddress), 10000);

        Assert.assertNull(service.getUser(0L));
        Assert.assertThat(serverSessionManager.getSessions(null).size(), CoreMatchers.equalTo(1));

        // 此处依赖于连接器清理时间.
        Thread.sleep(5500L);
        Assert.assertThat(serverSessionManager.getSessions(null).size(), CoreMatchers.equalTo(0));
    }

}
