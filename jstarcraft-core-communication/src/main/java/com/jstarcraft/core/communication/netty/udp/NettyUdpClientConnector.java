package com.jstarcraft.core.communication.netty.udp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jstarcraft.core.common.lifecycle.LifecycleState;
import com.jstarcraft.core.communication.exception.CommunicationException;
import com.jstarcraft.core.communication.message.CommunicationMessage;
import com.jstarcraft.core.communication.netty.NettyBufferInputStream;
import com.jstarcraft.core.communication.netty.NettyBufferOutputStream;
import com.jstarcraft.core.communication.netty.NettyClientConnector;
import com.jstarcraft.core.communication.netty.NettySessionManager;
import com.jstarcraft.core.communication.session.CommunicationSession;
import com.jstarcraft.core.communication.session.SessionReceiver;
import com.jstarcraft.core.communication.session.SessionSender;
import com.jstarcraft.core.utility.DelayElement;
import com.jstarcraft.core.utility.NameThreadFactory;
import com.jstarcraft.core.utility.SensitivityQueue;
import com.jstarcraft.core.utility.StringUtility;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.handler.codec.MessageToMessageDecoder;

/**
 * Netty客户端UDP连接器
 * 
 * <pre>
 * 用于帮助开启和关闭地址
 * </pre>
 * 
 * @author Birdy
 *
 */
@Sharable
public class NettyUdpClientConnector extends MessageToMessageDecoder<DatagramPacket> implements NettyClientConnector<InetSocketAddress>, SessionReceiver<InetSocketAddress>, SessionSender<InetSocketAddress> {

    private static final Logger LOGGER = LoggerFactory.getLogger(NettyUdpClientConnector.class);

    /** 到期时间间隔 */
    private static final int EXPIRE_TIME = 10000;
    /** 修复时间间隔 */
    private static final long FIX_TIME = 10000;

    /** 最大尝试次数 */
    private static final int MAXIMUM_TRY_TIMES = 10;

    /** 任务线程 */
    private static final ExecutorService EXECUTORS = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    /** Netty客户端 */
    private Bootstrap connector;
    /** Netty事件分组 */
    private EventLoopGroup eventLoopGroup;
    /** Netty选项 */
    private Map<String, Object> options;
    /** Netty通道 */
    private Channel channel;
    /** 会话管理器 */
    private NettySessionManager<InetSocketAddress> sessionManager;
    /** 已接收的会话队列 */
    private LinkedBlockingQueue<CommunicationSession<InetSocketAddress>> receiveSessions = new LinkedBlockingQueue<>();
    /** 未发送的会话队列 */
    private LinkedBlockingQueue<CommunicationSession<InetSocketAddress>> sendSessions = new LinkedBlockingQueue<>();

    /** 状态 */
    private AtomicReference<LifecycleState> state = new AtomicReference<>(LifecycleState.STOPPED);
    /** 定时队列 */
    private final SensitivityQueue<DelayElement<CommunicationSession<InetSocketAddress>>> queue = new SensitivityQueue<>(FIX_TIME);
    /** 清理者 */
    private final Runnable cleaner = new Runnable() {

        public void run() {
            while (true) {
                try {
                    DelayElement<CommunicationSession<InetSocketAddress>> element = queue.take();
                    CommunicationSession<InetSocketAddress> content = element.getContent();
                    sessionManager.detachSession(content.getKey());
                } catch (InterruptedException exception) {
                    if (state.get() == LifecycleState.STARTED) {
                        LOGGER.error("清理者异常", exception);
                    } else {
                        // 中断
                        queue.clear();
                        return;
                    }
                } catch (Exception exception) {
                    // TODO 需要考虑异常处理
                    LOGGER.error("清理会话异常", exception);
                }
            }
        }

    };
    /** 发送者 */
    private Runnable sender = new Runnable() {

        @Override
        public void run() {
            while (true) {
                try {
                    CommunicationSession<InetSocketAddress> session = sendSessions.take();
                    if (session == null) {
                        Thread.yield();
                    } else {
                        InetSocketAddress address = session.getContext();
                        if (address == null) {
                            // TODO 此处可能存在Bug,按照现在的逻辑,如果通道不存在不应该再放到发送队列.
                            sendSessions.offer(session);
                            continue;
                        }
                        while (session.hasSendMessage()) {
                            try {
                                CommunicationMessage message = session.pullSendMessage();
                                ByteBuf buffer = channel.alloc().buffer();
                                NettyBufferOutputStream outputBuffer = new NettyBufferOutputStream(buffer);
                                DataOutputStream dataOutputStream = new DataOutputStream(outputBuffer);
                                CommunicationMessage.writeTo(dataOutputStream, message);
                                if (LOGGER.isDebugEnabled()) {
                                    int length = buffer.readableBytes();
                                    byte[] bytes = new byte[length];
                                    buffer.getBytes(0, bytes);
                                    LOGGER.debug("编码消息:长度{},内容{}", new Object[] { length, bytes });
                                }
                                // 注意:Netty4.0.9之后write()方法不会发送消息,修改为writeAndFlush()/或者write()+flush()
                                channel.writeAndFlush(new DatagramPacket(buffer, address));
                            } catch (Throwable exception) {
                                LOGGER.error("编码消息异常", exception);
                                throw new CommunicationException(exception);
                            }
                        }
                    }
                } catch (InterruptedException exception) {
                    if (state.get() == LifecycleState.STARTED) {
                        LOGGER.error("发送者异常", exception);
                    } else {
                        return;
                    }
                } catch (Exception exception) {
                    // TODO 需要考虑异常处理
                    LOGGER.error("发送消息异常", exception);
                }
            }
        }

    };

    private Thread cleanThread;
    private Thread sendThread;

    public NettyUdpClientConnector(Map<String, Object> options, NettySessionManager<InetSocketAddress> sessionManager) {
        this.options = options;
        this.sessionManager = sessionManager;
    }

    @Override
    protected void decode(ChannelHandlerContext context, DatagramPacket packet, List<Object> decode) throws Exception {
        try {
            ByteBuf buffer = packet.content();
            NettyBufferInputStream inputBuffer = new NettyBufferInputStream(buffer);
            DataInputStream dataInputStream = new DataInputStream(inputBuffer);
            CommunicationMessage message = CommunicationMessage.readFrom(dataInputStream);
            checkData(packet.sender(), message);
        } catch (Exception exception) {
            LOGGER.error("解码消息异常", exception);
            throw new CommunicationException(exception);
        }
    }

    @Override
    public void checkData(InetSocketAddress address, CommunicationMessage message) {
        CommunicationSession<InetSocketAddress> session = sessionManager.getSession(address);
        session.pushReceiveMessage(message);
        receiveSessions.offer(session);
    }

    @Override
    public CommunicationSession<InetSocketAddress> pullSession() {
        try {
            return receiveSessions.take();
        } catch (Exception exception) {
            throw new CommunicationException(exception);
        }
    }

    @Override
    public int getReceiveSize() {
        return receiveSessions.size();
    }

    @Override
    public void pushSession(CommunicationSession<InetSocketAddress> session) {
        sendSessions.offer(session);
    }

    @Override
    public int getSendSize() {
        return sendSessions.size();
    }

    @Override
    public LifecycleState getState() {
        return state.get();
    }

    @Override
    public void start() {
        if (!state.compareAndSet(LifecycleState.STOPPED, LifecycleState.STARTED)) {
            throw new CommunicationException();
        }
        connector = new Bootstrap();
        for (Entry<String, Object> keyValue : options.entrySet()) {
            ChannelOption<Object> key = ChannelOption.valueOf(keyValue.getKey());
            Object value = keyValue.getValue();
            connector.option(key, value);
        }
        eventLoopGroup = new NioEventLoopGroup(1, new NameThreadFactory("客户端EventLoop线程"));
        connector.group(eventLoopGroup);
        connector.channel(NioDatagramChannel.class);
        connector.handler(new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel channel) throws Exception {
                ChannelPipeline pipeline = channel.pipeline();
                pipeline.addLast("connector", NettyUdpClientConnector.this);
            }
        });
        for (int tryTimes = 0; tryTimes < MAXIMUM_TRY_TIMES; tryTimes++) {
            try {
                tryTimes++;
                // connector.remoteAddress("127.0.0.1", 0);
                ChannelFuture bind = connector.bind(0);
                channel = bind.sync().channel();

                cleanThread = new Thread(cleaner);
                cleanThread.setDaemon(true);
                cleanThread.start();

                sendThread = new Thread(sender);
                sendThread.setDaemon(true);
                sendThread.start();
                return;
            } catch (Throwable throwable) {
                String message = StringUtility.format("客户端异常");
                LOGGER.error(message, throwable);
            }
        }
        throw new CommunicationException("客户端异常");
    }

    @Override
    public void stop() {
        if (!state.compareAndSet(LifecycleState.STARTED, LifecycleState.STOPPED)) {
            throw new CommunicationException();
        }
        if (channel != null) {
            channel.close().awaitUninterruptibly();
        }
        eventLoopGroup.shutdownGracefully();
        cleanThread.interrupt();
        sendThread.interrupt();
        while (cleanThread.isAlive() || sendThread.isAlive()) {
            Thread.yield();
        }
        Collection<CommunicationSession<InetSocketAddress>> sessions = sessionManager.getSessions(null);
        for (CommunicationSession<InetSocketAddress> session : sessions) {
            sessionManager.detachSession(session.getKey());
        }
    }

    @Override
    public synchronized CommunicationSession<InetSocketAddress> open(InetSocketAddress address, long wait) {
        if (sessionManager.getSession(address) != null) {
            throw new CommunicationException();
        }
        return sessionManager.attachSession(address, address);
    }

    @Override
    public synchronized void close(InetSocketAddress address) {
        sessionManager.detachSession(address);
    }

    @Override
    public synchronized Collection<InetSocketAddress> getAddresses() {
        // TODO 此处需要重构.
        HashSet<InetSocketAddress> addresses = new HashSet<>();
        // for (InetSocketAddress address : addressSessions.keySet()) {
        // addresses.add(address.toString());
        // }
        return addresses;
    }

}
