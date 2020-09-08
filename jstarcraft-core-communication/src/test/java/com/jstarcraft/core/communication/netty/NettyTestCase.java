package com.jstarcraft.core.communication.netty;

import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.jstarcraft.core.common.lifecycle.LifecycleState;
import com.jstarcraft.core.communication.annotation.CommunicationModule;
import com.jstarcraft.core.communication.annotation.CommunicationModule.ModuleSide;
import com.jstarcraft.core.communication.command.CommandDispatcher;
import com.jstarcraft.core.communication.command.CommandManager;
import com.jstarcraft.core.communication.command.MockClientClass;
import com.jstarcraft.core.communication.command.MockClientInterface;
import com.jstarcraft.core.communication.command.MockServerClass;
import com.jstarcraft.core.communication.command.MockServerInterface;
import com.jstarcraft.core.communication.command.UserObject;
import com.jstarcraft.core.communication.session.CommunicationSession;
import com.jstarcraft.core.communication.session.SessionManager;
import com.jstarcraft.core.utility.StringUtility;

public abstract class NettyTestCase<T> {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    /** 用于测试{@link CommunicationModule} */
    @Autowired
    @Qualifier("clientDispatcher")
    protected CommandDispatcher<T> clientDispatcher;
    @Autowired
    @Qualifier("serverDispatcher")
    protected CommandDispatcher<T> serverDispatcher;
    @Autowired
    @Qualifier("clientSessionManager")
    protected NettySessionManager<T> clientSessionManager;
    @Autowired
    @Qualifier("serverSessionManager")
    protected NettySessionManager<T> serverSessionManager;
    @Autowired
    @Qualifier("clientCommandManager")
    protected CommandManager clientCommandManager;
    @Autowired
    @Qualifier("serverCommandManager")
    protected CommandManager serverCommandManager;

    @Autowired
    @Qualifier("nettyClientConnector")
    protected NettyClientConnector<T> nettyClientConnector;
    @Autowired
    @Qualifier("nettyServerConnector")
    protected NettyServerConnector<T> nettyServerConnector;

    @Autowired
    protected MockServerClass mockServerClass;
    @Autowired
    protected MockClientClass mockClientClass;

    protected InetSocketAddress clientAddress = SessionManager.key2Address("127.0.0.1:6969");
    protected InetSocketAddress serverAddress = SessionManager.key2Address("0.0.0.0:6969");

    @Before
    public void before() {
        // 启动客户端与服务端的连接器
        nettyServerConnector.start();
        nettyClientConnector.start();
    }

    @After
    public void after() {
        // 关闭客户端与服务端的连接器
        nettyClientConnector.stop();
        nettyServerConnector.stop();
        Assert.assertThat(clientSessionManager.getSessions(null).size(), CoreMatchers.equalTo(0));
        Assert.assertThat(serverSessionManager.getSessions(null).size(), CoreMatchers.equalTo(0));
    }

    /**
     * 测试基于Spring的装配自动化
     */
    @Test
    public void testAssemblage() {
        // 保证@CommunicationModule注解的接口与类型能被自动装配
        Assert.assertThat(clientDispatcher, CoreMatchers.notNullValue());
        Assert.assertThat(clientDispatcher.getSide(), CoreMatchers.equalTo(ModuleSide.CLIENT));
        Assert.assertThat(serverDispatcher, CoreMatchers.notNullValue());
        Assert.assertThat(serverDispatcher.getSide(), CoreMatchers.equalTo(ModuleSide.SERVER));
        Assert.assertThat(clientCommandManager, CoreMatchers.notNullValue());
        Assert.assertThat(serverCommandManager, CoreMatchers.notNullValue());
        Assert.assertThat(nettyClientConnector, CoreMatchers.notNullValue());
        Assert.assertThat(nettyServerConnector, CoreMatchers.notNullValue());
    }

    /**
     * 测试服务
     * 
     * @throws Exception
     */
    @Test
    public void testService() throws Exception {
        // 启动客户端与服务端的会话
        CommunicationSession<T> clientSession = nettyClientConnector.open(clientAddress, 5000);
        Assert.assertThat(clientSession.getKey(), CoreMatchers.equalTo(SessionManager.address2Key(clientAddress)));
        // Thread.sleep(1000L);
        // Assert.assertThat(serverSessionManager.getSessions(null).size(),
        // CoreMatchers.equalTo(1));

        // 相当于业务流程
        long userId = 1;
        String userName = "birdy";
        MockServerInterface serverService = clientCommandManager.getProxy(MockServerInterface.class, SessionManager.address2Key(clientAddress), 10000);
        UserObject user = UserObject.instanceOf(userId, userName);

        int serverTimes = mockServerClass.getTimes();
        Assert.assertThat(serverService.createUser(user), CoreMatchers.equalTo(user));
        Assert.assertThat(serverService.getUser(userId), CoreMatchers.equalTo(user));
        Assert.assertNull(serverService.deleteUser(userId));
        Assert.assertThat(mockServerClass.getTimes() - serverTimes, CoreMatchers.equalTo(3));
        Assert.assertThat(serverService.addition(1), CoreMatchers.equalTo(2));
        Assert.assertThat(serverService.addition(1, 2), CoreMatchers.equalTo(3));
        String string = "eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOjEsImlzcyI6Imh0dHA6Ly9zdmxhZGEuY29tIiwidGltZSI6IjIwMTctMDYtMTQgMDk6MDE6MzQiLCJleHAiOjE0OTc0MDI5OTQsImlhdCI6MTQ5NzQwMjA5NH0.8fELQQ-iAwa6Ue_pGUi8qzTu7tHScAxPBzp92ZWJDII9I4eXftXAaHF2qb8UgGzrbTPa_baIa7MKFLwujfZ16Q,eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOjEsImlzcyI6Imh0dHA6Ly9zdmxhZGEuY29tIiwidGltZSI6IjIwMTctMDYtMTQgMDk6MDE6MzQiLCJzY29wZXMiOlsiUk9MRV9SRUZSRVNIX1RPS0VOIl0sImV4cCI6MTQ5NzQwNTY5NSwiaWF0IjoxNDk3NDAyMDk1LCJqdGkiOiIyNTFiYmViYy01NmNhLTQxOTUtOWY5OC0yMmU2ZDI3MWFkMmIifQ.2pu4DfpstAwRNVAH5mzzNg9fhNB-etuH9Zm6yc18C2LjDxzjCmUeZVVvntPvhcNAXNUma34L_t7CRtgSVCNc-Q";
        Assert.assertThat(serverService.md5(string), CoreMatchers.equalTo(string));

        int clientTimes = mockClientClass.getTimes();
        MockClientInterface clientService = serverCommandManager.getProxy(MockClientInterface.class, serverSessionManager.getSessions(null).get(0).getKey(), 10000);
        clientService.testExecute(user);
        Thread.sleep(1000L);
        Assert.assertThat(mockClientClass.getTimes() - clientTimes, CoreMatchers.equalTo(1));

        // 停止客户端与服务端的会话
        nettyClientConnector.close(clientAddress);
        // // TODO 此处依赖于连接器清理时间,可能需要重构.
        // Thread.sleep(15000L);
        // Assert.assertThat(serverSessionManager.getSessions(null).size(),
        // CoreMatchers.equalTo(0));
    }

    /**
     * 测试连接
     */
    @Test
    public abstract void testConnect() throws Exception;

    /**
     * 测试性能
     */
    @Test
    public void testPerformance() throws Exception {
        int count = 25; // 线程数
        int times = 1000; // 任务数
        nettyClientConnector.open(clientAddress, 5000);
        // TODO 是否等待服务端?
        ExecutorService executor = Executors.newFixedThreadPool(count);
        CountDownLatch latch = new CountDownLatch(count);
        for (int thread = 1; thread <= count; thread++) {
            long userId = thread;
            executor.submit(() -> {
                try {
                    MockServerInterface service = clientCommandManager.getProxy(MockServerInterface.class, SessionManager.address2Key(clientAddress), 10000);
                    String userName = "birdy";
                    UserObject user = UserObject.instanceOf(userId, userName);
                    for (int time = 0; time < times; time++) {
                        Assert.assertThat(service.createUser(user), CoreMatchers.equalTo(user));
                        Assert.assertThat(service.getUser(userId), CoreMatchers.equalTo(user));
                        Assert.assertNull(service.deleteUser(userId));
                    }
                    latch.countDown();
                } catch (Exception exception) {
                    logger.error(StringUtility.EMPTY, exception);
                }
            });
        }
        latch.await();
        nettyClientConnector.close(clientAddress);
    }

    /**
     * 测试状态
     */
    @Test
    public void testState() {
        // 在停止服务器和启动服务器过程中,绝对不允许有其它操作(读写锁).
        nettyClientConnector.stop();
        Assert.assertThat(nettyClientConnector.getState(), CoreMatchers.equalTo(LifecycleState.STOPPED));

        nettyServerConnector.stop();
        Assert.assertThat(nettyServerConnector.getState(), CoreMatchers.equalTo(LifecycleState.STOPPED));

        nettyServerConnector.start();
        Assert.assertThat(nettyServerConnector.getState(), CoreMatchers.equalTo(LifecycleState.STARTED));

        nettyClientConnector.start();
        Assert.assertThat(nettyClientConnector.getState(), CoreMatchers.equalTo(LifecycleState.STARTED));
    }

}
