package com.jstarcraft.core.communication.command;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jstarcraft.core.communication.annotation.CommunicationModule.ModuleSide;
import com.jstarcraft.core.communication.message.MessageBody;
import com.jstarcraft.core.communication.netty.NettySessionManager;
import com.jstarcraft.core.communication.session.CommunicationSession;
import com.jstarcraft.core.utility.RandomUtility;
import com.jstarcraft.core.utility.StringUtility;

import io.netty.channel.Channel;
import io.netty.channel.embedded.EmbeddedChannel;

/**
 * 指令调度器测试
 * 
 * @author Birdy
 *
 */
public class CommandDispatcherTestCase {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Test
    public void test() throws InterruptedException {
        // 会话管理器,接收者,发送者
        NettySessionManager<Channel> clientManager = new NettySessionManager<>();
        NettySessionManager<Channel> serverManager = new NettySessionManager<>();
        MockSessionTransmitter client2Server = new MockSessionTransmitter(serverManager);
        MockSessionTransmitter server2Client = new MockSessionTransmitter(clientManager);

        // 客户端与服务端的调度器
        Map<String, CommandDefinition> clientDefinitions = new HashMap<>();
        for (Method method : MockClientInterface.class.getMethods()) {
            CommandDefinition definition = CommandDefinition.instanceOf(method);
            clientDefinitions.put(method.getName(), definition);
        }
        Map<String, CommandDefinition> serverDefinitions = new HashMap<>();
        for (Method method : MockServerInterface.class.getMethods()) {
            CommandDefinition definition = CommandDefinition.instanceOf(method);
            serverDefinitions.put(method.getName(), definition);
        }
        Collection<CommandDefinition> definitions = new HashSet<>();
        definitions.addAll(clientDefinitions.values());
        definitions.addAll(serverDefinitions.values());
        Map<String, CommandStrategy> strategies = new HashMap<>();
        strategies.put(StringUtility.EMPTY, new MockStrategy());
        CommandDispatcher<Channel> clientDispatcher = new CommandDispatcher<>(ModuleSide.CLIENT, definitions, server2Client, client2Server, strategies, 1000L);
        CommandDispatcher<Channel> serverDispatcher = new CommandDispatcher<>(ModuleSide.SERVER, definitions, client2Server, server2Client, strategies, 1000L);
        MockClientClass socketClientObject = new MockClientClass();
        Collection<Object> clientObjects = Arrays.asList(socketClientObject);
        clientDispatcher.start(clientObjects, 1, 5);
        MockServerClass socketServerObject = new MockServerClass();
        Collection<Object> serverObjects = Arrays.asList(socketServerObject);
        serverDispatcher.start(serverObjects, 1, 5);

        // 操作计数
        CommandDefinition createDefinition = serverDefinitions.get("createUser");
        CommandDefinition updateDefinition = serverDefinitions.get("updateUser");
        CommandDefinition deleteDefinition = serverDefinitions.get("deleteUser");
        CommandDefinition getDefinition = serverDefinitions.get("getUser");
        CommandDefinition testDefinition = clientDefinitions.get("testExecute");
        AtomicInteger createTimes = new AtomicInteger();
        AtomicInteger updateTimes = new AtomicInteger();
        AtomicInteger deleteTimes = new AtomicInteger();
        AtomicInteger getTimes = new AtomicInteger();

        // 创建数据
        long begin = System.currentTimeMillis();
        int DATA_SIZE = 100;
        for (long id = 0; id < DATA_SIZE; id++) {
            String name = "birdy" + id;
            UserObject content = UserObject.instanceOf(id, name);
            Channel channel = new EmbeddedChannel();
            CommunicationSession<Channel> serverSession = serverManager.attachSession("127.0.0.1:" + id, channel);
            CommunicationSession<Channel> clientSession = clientManager.attachSession("127.0.0.1:" + id, channel);
            MessageBody body = createDefinition.getInputDefinition().getMessageBody(clientDispatcher.getCodecs(), new Object[] { content });
            clientDispatcher.sendRequest(createDefinition, clientSession, body);
            createTimes.incrementAndGet();
        }

        // 多线程并发读写操作
        int threadSize = 10;
        AtomicBoolean run = new AtomicBoolean(true);
        for (int index = 0; index < threadSize; index++) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (run.get()) {
                        try {
                            // 写操作
                            long updateId = RandomUtility.randomInteger(0, DATA_SIZE);
                            CommunicationSession<Channel> updateSession = clientManager.getSession("127.0.0.1:" + updateId);
                            UUID uuid = UUID.randomUUID();
                            MessageBody updateBody = updateDefinition.getInputDefinition().getMessageBody(clientDispatcher.getCodecs(), new Object[] { updateId, uuid.toString() });
                            clientDispatcher.sendRequest(updateDefinition, updateSession, updateBody);
                            updateTimes.incrementAndGet();
                            // 读操作
                            long getId = RandomUtility.randomInteger(0, DATA_SIZE);
                            CommunicationSession<Channel> getSession = clientManager.getSession("127.0.0.1:" + getId);
                            MessageBody getBody = getDefinition.getInputDefinition().getMessageBody(clientDispatcher.getCodecs(), new Object[] { getId });
                            clientDispatcher.sendRequest(getDefinition, getSession, getBody);
                            getTimes.incrementAndGet();
                            // 通知
                            long testId = RandomUtility.randomInteger(0, DATA_SIZE);
                            CommunicationSession<Channel> testSession = serverManager.getSession("127.0.0.1:" + testId);
                            MessageBody testBody = testDefinition.getInputDefinition().getMessageBody(serverDispatcher.getCodecs(), new Object[] { null });
                            serverDispatcher.sendRequest(testDefinition, testSession, testBody);
                        } catch (Exception exception) {
                            Thread thread = Thread.currentThread();
                            String message = StringUtility.format("线程[{}]并发读写操作", thread.getName());
                            logger.info(message, exception);
                        }
                    }
                }
            });
            thread.setDaemon(true);
            thread.start();
        }
        int EXPIRE_SECONDS = 5;
        Thread.sleep(TimeUnit.MILLISECONDS.convert(EXPIRE_SECONDS, TimeUnit.SECONDS));
        run.set(false);

        int times = 0;
        do {
            times = createTimes.get() + updateTimes.get() + deleteTimes.get() + getTimes.get();
            String message = StringUtility.format("已执行操作[{}],未执行操作[{}]", socketServerObject.getTimes(), times - socketServerObject.getTimes());
            logger.info(message);
        } while (socketServerObject.getTimes() != times);

        // 删除数据
        for (long id = 0; id < DATA_SIZE; id++) {
            CommunicationSession<Channel> session = clientManager.getSession("127.0.0.1:" + id);
            MessageBody body = deleteDefinition.getInputDefinition().getMessageBody(clientDispatcher.getCodecs(), new Object[] { id });
            clientDispatcher.sendRequest(deleteDefinition, session, body);
            deleteTimes.incrementAndGet();
        }
        do {
            times = createTimes.get() + updateTimes.get() + deleteTimes.get() + getTimes.get();
            String message = StringUtility.format("已执行操作[{}],未执行操作[{}]", socketServerObject.getTimes(), times - socketServerObject.getTimes());
            logger.info(message);
        } while (socketServerObject.getTimes() != times);

        long end = System.currentTimeMillis();
        String message = StringUtility.format("[{}]条线程在[{}]毫秒内执行[{}]次服务端读写操作,[{}]次客户端管道操作", threadSize, end - begin, socketServerObject.getTimes(), socketClientObject.getTimes());
        logger.info(message);
        message = StringUtility.format("[{}]操作[{}]次", "createUser", socketServerObject.createTimes.get());
        logger.info(message);
        message = StringUtility.format("[{}]操作[{}]次", "updateUser", socketServerObject.updateTimes.get());
        logger.info(message);
        message = StringUtility.format("[{}]操作[{}]次", "deleteUser", socketServerObject.deleteTimes.get());
        logger.info(message);
        message = StringUtility.format("[{}]操作[{}]次", "getUser", socketServerObject.getTimes.get());
        logger.info(message);

        Assert.assertThat(socketServerObject.updateTimes.get(), CoreMatchers.equalTo(socketClientObject.getTimes()));
        Assert.assertThat(socketServerObject.getTimes.get(), CoreMatchers.equalTo(socketClientObject.getTimes()));

        clientDispatcher.stop();
        serverDispatcher.stop();
    }

}
