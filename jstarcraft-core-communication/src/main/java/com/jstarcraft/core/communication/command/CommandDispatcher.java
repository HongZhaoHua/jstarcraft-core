package com.jstarcraft.core.communication.command;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jstarcraft.core.codec.ContentCodec;
import com.jstarcraft.core.codec.csv.CsvContentCodec;
import com.jstarcraft.core.codec.json.JsonContentCodec;
import com.jstarcraft.core.codec.kryo.KryoContentCodec;
import com.jstarcraft.core.codec.specification.CodecDefinition;
import com.jstarcraft.core.codec.standard.StandardContentCodec;
import com.jstarcraft.core.common.lifecycle.LifecycleState;
import com.jstarcraft.core.communication.annotation.CommunicationCommand;
import com.jstarcraft.core.communication.annotation.CommunicationModule;
import com.jstarcraft.core.communication.annotation.CommunicationModule.ModuleSide;
import com.jstarcraft.core.communication.exception.CommunicationConfigurationException;
import com.jstarcraft.core.communication.exception.CommunicationDefinitionException;
import com.jstarcraft.core.communication.exception.CommunicationStateException;
import com.jstarcraft.core.communication.exception.CommunicationWaitException;
import com.jstarcraft.core.communication.message.CommunicationMessage;
import com.jstarcraft.core.communication.message.MessageBody;
import com.jstarcraft.core.communication.message.MessageFormat;
import com.jstarcraft.core.communication.message.MessageHead;
import com.jstarcraft.core.communication.message.MessageTail;
import com.jstarcraft.core.communication.session.CommunicationSession;
import com.jstarcraft.core.communication.session.SessionReceiver;
import com.jstarcraft.core.communication.session.SessionSender;
import com.jstarcraft.core.utility.DelayInteger;
import com.jstarcraft.core.utility.NameThreadFactory;
import com.jstarcraft.core.utility.StringUtility;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

/**
 * 指令调度者
 * 
 * <pre>
 * 指令架构的核心,与{@link CommandWorker},{@link SessionReceiver},{@link SessionSender}协作,实现指令,消息与会话之间的调度与转换.
 * </pre>
 * 
 * @author Birdy
 *
 */
public class CommandDispatcher<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommandDispatcher.class);

    /** 指令端 */
    private ModuleSide side;
    /** 指令定义 */
    private Map<String, Map<Byte, CommandDefinition>> definitions = new HashMap<>();
    /** 指令对象 */
    private Map<CommandDefinition, Object> objects = new HashMap<>();
    /** 指令编解码 */
    private Map<Byte, ContentCodec> codecs;
    /** 指令策略 */
    private Map<String, CommandStrategy> strategies;
    /** 指令上下文(覆盖{@link CommandContext}的hashCode和equals行为) */
    private Int2ObjectMap<CommandContext> contexts = new Int2ObjectOpenHashMap<>();
    /** 过期任务 */
    private long wait;
    private DelayQueue<DelayInteger> queue = new DelayQueue<>();
    private Runnable expireTask = new Runnable() {
        @Override
        public void run() {
            try {
                DelayInteger element = queue.take();
                int sequence = element.getContent();
                synchronized (contexts) {
                    CommandContext context = contexts.remove(sequence);
                    if (context != null) {
                        context.setException(new CommunicationWaitException());
                    }
                }
            } catch (Throwable exception) {
                String string = StringUtility.format("过期线程[{}]循环时异常", Thread.currentThread().getName());
                LOGGER.error(string, exception);
            }
        }
    };

    /** 指令标识管理器(TODO 考虑与标识管理器整合) */
    private AtomicInteger sequenceManager = new AtomicInteger();

    /** 接收者 */
    private SessionReceiver<T> receiver;
    /** 发送者 */
    private SessionSender<T> sender;

    /** 状态 */
    private AtomicReference<LifecycleState> state = new AtomicReference<>(null);

    /** 调度工厂 */
    private NameThreadFactory dispatchFactory = new NameThreadFactory("CommandDispatcher");
    /** 调度任务 */
    private Runnable dispatchTask = new Runnable() {
        @Override
        public void run() {
            while (LifecycleState.STARTED.equals(state.get())) {
                try {
                    CommunicationSession<T> session = receiver.pullSession();
                    if (session != null) {
                        while (session.hasReceiveMessage()) {
                            CommunicationMessage message = session.pullReceiveMessage();
                            try {
                                MessageHead head = message.getHead();
                                byte[] module = head.getModule();
                                byte command = head.getCommand();
                                CommandDefinition definition = getDefinition(command, module);
                                Object object = objects.get(definition);
                                // 判断是请求还是响应
                                if (object != null) {
                                    receiveRequest(definition, message, session, object);
                                } else {
                                    receiveResponse(definition, message, session);
                                }
                            } catch (Throwable exception) {
                                String string = StringUtility.format("调度线程[{}]处理会话[{}]的消息[{}]时异常", Thread.currentThread().getName(), session, message);
                                LOGGER.error(string, exception);
                            }
                        }
                    }
                } catch (Throwable exception) {
                    String string = StringUtility.format("调度线程[{}]循环时异常", Thread.currentThread().getName());
                    LOGGER.error(string, exception);
                }
            }
        }
    };

    public CommandDispatcher(ModuleSide side, Collection<CommandDefinition> definitions, SessionReceiver<T> receiver, SessionSender<T> sender, Map<String, CommandStrategy> strategies, long wait) {
        this.side = side;
        Collection<Type> types = new HashSet<>();
        for (CommandDefinition definition : definitions) {
            String module = Arrays.toString(definition.getModule());
            Map<Byte, CommandDefinition> commands = this.definitions.get(module);
            if (commands == null) {
                commands = new HashMap<>();
                this.definitions.put(module, commands);
            }
            byte command = definition.getCommand();
            if (commands.containsKey(command)) {
                CommandDefinition newDefinition = definition;
                CommandDefinition oldDefinition = commands.get(command);
                String string = StringUtility.format("新指令定义[{}]与旧指令定义[{}]冲突", newDefinition, oldDefinition);
                throw new CommunicationDefinitionException(string);
            }
            commands.put(command, definition);
            types.add(definition.getInputDefinition().getContentType());
            types.add(definition.getInputDefinition().getInputType());
            types.add(definition.getOutputDefinition().getContentType());
            types.add(definition.getOutputDefinition().getOutputType());
        }
        this.receiver = receiver;
        this.sender = sender;
        this.strategies = strategies;

        CodecDefinition codecDefinition = CodecDefinition.instanceOf(types);
        Map<Byte, ContentCodec> codecs = new HashMap<>();
        codecs.put(MessageFormat.CSV.getMark(), new CsvContentCodec(codecDefinition));
        codecs.put(MessageFormat.JSON.getMark(), new JsonContentCodec(codecDefinition));
        codecs.put(MessageFormat.KRYO.getMark(), new KryoContentCodec(codecDefinition));
        codecs.put(MessageFormat.STANDARD.getMark(), new StandardContentCodec(codecDefinition));
        this.codecs = codecs;

        this.wait = wait;
        Thread expireThread = new Thread(expireTask);
        expireThread.setDaemon(true);
        expireThread.start();
    }

    /**
     * 启动调度器
     * 
     * @param objects
     */
    public void start(Collection<Object> objects, int threadSize, int contextWait) {
        if (threadSize <= 0) {
            throw new CommunicationConfigurationException();
        }
        if (contextWait <= 0) {
            throw new CommunicationConfigurationException();
        }
        if (!state.compareAndSet(null, LifecycleState.STARTED)) {
            throw new CommunicationStateException();
        }
        for (Object object : objects) {
            for (Map<Byte, CommandDefinition> definitions : definitions.values()) {
                for (CommandDefinition definition : definitions.values()) {
                    if (definition.getSide().equals(side) && definition.getClazz().isInstance(object)) {
                        if (objects.contains(definition)) {
                            Object newObject = object;
                            Object oldObject = this.objects.get(definition);
                            String string = StringUtility.format("新指令对象[{}]与旧指令对象[{}]冲突", newObject, oldObject);
                            throw new CommunicationDefinitionException(string);
                        }
                        this.objects.put(definition, object);
                    }
                }
            }
        }
        for (int index = 0; index < threadSize; index++) {
            Thread dispatchThread = dispatchFactory.newThread(dispatchTask);
            dispatchThread.setDaemon(true);
            dispatchThread.start();
        }
    }

    /**
     * 停止调度器
     */
    public void stop() {
        if (!state.compareAndSet(LifecycleState.STARTED, LifecycleState.STOPPED)) {
            throw new CommunicationStateException();
        }
    }

    public ModuleSide getSide() {
        return side;
    }

    /**
     * 获取调度器状态
     * 
     * @return
     */
    public LifecycleState getState() {
        return state.get();
    }

    // Dispatcher调用
    private void receiveRequest(CommandDefinition definition, CommunicationMessage message, CommunicationSession<?> session, Object object) {
        CommandStrategy strategy = strategies.get(definition.getStrategy());
        CommandWorker worker = new CommandWorker(this, definition, message, session, object);
        strategy.execute(worker);
    }

    // Dispatcher调用
    private void receiveResponse(CommandDefinition definition, CommunicationMessage message, CommunicationSession<?> session) {
        // 服务端不可能接收到响应
        // 客户端可能接收到响应(同步模式指令)
        MessageHead head = message.getHead();
        int sequence = head.getSequence();
        synchronized (contexts) {
            CommandContext context = contexts.remove(sequence);
            if (context != null) {
                OutputDefinition outputDefinition = definition.getOutputDefinition();
                Object value = outputDefinition.getOutputValue(codecs, message, session);
                context.setValue(value);
            }
        }
    }

    // Worker或者CommandManager调用
    CommandContext sendRequest(CommandDefinition definition, CommunicationSession<T> session, MessageBody body) {
        if (!LifecycleState.STARTED.equals(state.get())) {
            throw new CommunicationStateException();
        }
        int sequence = sequenceManager.incrementAndGet();
        MessageHead head = MessageHead.instanceOf(sequence, definition.getCommand(), definition.getModule());
        // TODO 消息尾
        MessageTail tail = null;
        CommunicationMessage message = CommunicationMessage.instanceOf(head, body, tail);
        CommandContext context = null;
        if (ModuleSide.CLIENT.equals(side)) {
            context = new CommandContext(definition, sequence);
            synchronized (contexts) {
                contexts.put(sequence, context);
            }
            // TODO 配置有效期
            queue.put(new DelayInteger(sequence, Instant.ofEpochMilli(System.currentTimeMillis() + wait)));
        }
        // TODO 改为由接收者,发送者负责维护会话,调度者只负责调度会话与发布事件
        session.pushSendMessage(message);
        sender.pushSession(session);
        return context;
    }

    public int getContextSize() {
        return contexts.size();
    }

    // Worker调用
    void sendResponse(CommandDefinition definition, CommunicationSession<T> session, int sequence, MessageBody body) {
        if (!LifecycleState.STARTED.equals(state.get())) {
            throw new CommunicationStateException();
        }
        if (ModuleSide.SERVER.equals(side)) {
            MessageHead head = MessageHead.instanceOf(sequence, definition.getCommand(), definition.getModule());
            // TODO 消息尾
            MessageTail tail = null;
            CommunicationMessage message = CommunicationMessage.instanceOf(head, body, tail);
            // TODO 改为由接收者,发送者负责维护会话,调度者只负责调度会话与发布事件
            session.pushSendMessage(message);
            sender.pushSession(session);
        }
    }

    Map<Byte, ContentCodec> getCodecs() {
        return codecs;
    }

    public CommandDefinition getDefinition(byte command, byte... module) {
        Map<Byte, CommandDefinition> definitions = this.definitions.get(Arrays.toString(module));
        CommandDefinition definition = definitions.get(command);
        return definition;
    }

    public CommandDefinition getDefinition(Method method) {
        Class<?> clazz = method.getDeclaringClass();
        CommunicationModule socketModule = clazz.getAnnotation(CommunicationModule.class);
        CommunicationCommand socketInterface = method.getAnnotation(CommunicationCommand.class);
        return getDefinition(socketInterface.code(), socketModule.code());
    }

}
