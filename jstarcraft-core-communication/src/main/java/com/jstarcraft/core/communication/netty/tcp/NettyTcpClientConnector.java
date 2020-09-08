package com.jstarcraft.core.communication.netty.tcp;

import java.net.InetSocketAddress;
import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
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
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * Netty客户端TCP连接器
 * 
 * <pre>
 * 用于帮助开启和关闭地址
 * </pre>
 * 
 * @author Birdy
 *
 */
@Sharable
public class NettyTcpClientConnector extends ChannelInboundHandlerAdapter implements NettyClientConnector<Channel>, SessionReceiver<Channel>, SessionSender<Channel> {

    private static final Logger LOGGER = LoggerFactory.getLogger(NettyTcpClientConnector.class);

    /** 到期时间间隔 */
    private static final int EXPIRE_TIME = 10000;
    /** 修复时间间隔 */
    private static final long FIX_TIME = 10000;

    /** 任务线程 */
    private static final ExecutorService EXECUTORS = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    /** Netty客户端 */
    private Bootstrap connector;
    /** Netty事件分组 */
    private EventLoopGroup eventLoopGroup;
    /** Netty选项 */
    private Map<String, Object> options;
    /** Netty通道映射 */
    private Map<InetSocketAddress, Channel> channels = new HashMap<>();
    /** 会话管理器 */
    private NettySessionManager<Channel> sessionManager;
    /** 已接收的会话队列 */
    private LinkedBlockingQueue<CommunicationSession<Channel>> receiveSessions = new LinkedBlockingQueue<>();
    /** 未发送的会话队列 */
    private LinkedBlockingQueue<CommunicationSession<Channel>> sendSessions = new LinkedBlockingQueue<>();

    /** 状态 */
    private AtomicReference<LifecycleState> state = new AtomicReference<>(LifecycleState.STOPPED);
    /** 定时队列 */
    private final SensitivityQueue<DelayElement<CommunicationSession<Channel>>> queue = new SensitivityQueue<>(FIX_TIME);
    /** 清理者 */
    private final Runnable cleaner = new Runnable() {

        public void run() {
            while (true) {
                try {
                    DelayElement<CommunicationSession<Channel>> element = queue.take();
                    CommunicationSession<Channel> content = element.getContent();
                    // 会话可能已经连接
                    if (!content.getContext().isActive()) {
                        sessionManager.detachSession(content.getKey());
                    }
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
                    CommunicationSession<Channel> session = sendSessions.take();
                    if (session == null) {
                        Thread.yield();
                    } else {
                        Channel channel = session.getContext();
                        if (channel == null) {
                            // TODO 此处可能存在Bug,按照现在的逻辑,如果通道不存在不应该再放到发送队列.
                            sendSessions.offer(session);
                            continue;
                        }
                        while (session.hasSendMessage()) {
                            if (channel.isWritable()) {
                                CommunicationMessage message = session.pullSendMessage();
                                // 注意:Netty4.0.9之后write()方法不会发送消息,修改为writeAndFlush()/或者write()+flush()
                                channel.writeAndFlush(message);
                                // LOGGER.error("client " +
                                // message.getHead().getCommand());
                                // LOGGER.error("client " + message.getBody());
                            } else {
                                // LOGGER.error("client " +
                                // session.hasSendMessage());
                                sendSessions.offer(session);
                                break;
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

    public NettyTcpClientConnector(Map<String, Object> options, NettySessionManager<Channel> sessionManager) {
        this.options = options;
        this.sessionManager = sessionManager;
    }

    @Override
    public void channelInactive(ChannelHandlerContext context) throws Exception {
        Channel channel = context.channel();
        InetSocketAddress address = InetSocketAddress.class.cast(channel.remoteAddress());
        CommunicationSession<Channel> session = sessionManager.getSession(address);
        if (session != null) {
            // TODO 将会话放到定时队列
            Instant now = Instant.now();
            Instant expire = now.plusMillis(EXPIRE_TIME);
            DelayElement<CommunicationSession<Channel>> element = new DelayElement<>(session, expire);
            queue.offer(element);
        }
        super.channelInactive(context);
    }

    @Override
    public void checkData(Channel channel, CommunicationMessage message) {
        synchronized (channel) {
            InetSocketAddress address = InetSocketAddress.class.cast(channel.remoteAddress());
            CommunicationSession<Channel> session = sessionManager.getSession(address);
            session.pushReceiveMessage(message);
            receiveSessions.offer(session);
        }
    }

    @Override
    public CommunicationSession<Channel> pullSession() {
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
    public void pushSession(CommunicationSession<Channel> session) {
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
        connector.channel(NioSocketChannel.class);
        connector.handler(new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel channel) throws Exception {
                ChannelPipeline pipeline = channel.pipeline();
                NettyTcpMessageDecoder decoder = new NettyTcpMessageDecoder(NettyTcpClientConnector.this);
                NettyTcpMessageEncoder encoder = new NettyTcpMessageEncoder();
                pipeline.addLast("decoder", decoder);
                pipeline.addLast("encoder", encoder);
                pipeline.addLast("connector", NettyTcpClientConnector.this);
            }
        });

        cleanThread = new Thread(cleaner);
        cleanThread.setDaemon(true);
        cleanThread.start();

        sendThread = new Thread(sender);
        sendThread.setDaemon(true);
        sendThread.start();
    }

    @Override
    public void stop() {
        if (!state.compareAndSet(LifecycleState.STARTED, LifecycleState.STOPPED)) {
            throw new CommunicationException();
        }
        for (InetSocketAddress key : channels.keySet()) {
            close(key);
        }
        eventLoopGroup.shutdownGracefully();
        cleanThread.interrupt();
        sendThread.interrupt();
        while (cleanThread.isAlive() || sendThread.isAlive()) {
            Thread.yield();
        }
        Collection<CommunicationSession<Channel>> sessions = sessionManager.getSessions(null);
        for (CommunicationSession<Channel> session : sessions) {
            sessionManager.detachSession(session.getKey());
        }
    }

    @Override
    public synchronized CommunicationSession<Channel> open(InetSocketAddress address, long wait) {
        if (sessionManager.getSession(address) != null) {
            throw new CommunicationException();
        }
        try {
            ChannelFuture future = connector.connect(address);
            future.sync();
            Channel channel = future.channel();
            channels.put(address, channel);
            return sessionManager.attachSession(address, channel);
        } catch (Throwable throwable) {
            String message = StringUtility.format("客户端异常");
            LOGGER.error(message, throwable);
            throw new CommunicationException();
        }
    }

    @Override
    public synchronized void close(InetSocketAddress address) {
        Channel channel = channels.remove(address);
        if (channel != null) {
            ChannelFuture channelFuture = channel.close();
            channelFuture.awaitUninterruptibly();
        }
    }

    @Override
    public Collection<InetSocketAddress> getAddresses() {
        return channels.keySet();
    }

}
