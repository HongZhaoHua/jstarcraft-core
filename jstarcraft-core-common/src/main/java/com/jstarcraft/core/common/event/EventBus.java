package com.jstarcraft.core.common.event;

import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jstarcraft.core.utility.NameThreadFactory;
import com.jstarcraft.core.utility.StringUtility;

public class EventBus {

    private static final Logger logger = LoggerFactory.getLogger(EventBus.class);

    /** 队列大小 */
    private int queueSize;

    /** 池大小 */
    private int poolSize;

    /** 池等待 */
    private int poolWait;

    private BlockingQueue<Runnable> queue;

    private ExecutorService pool;

    private ConcurrentMap<Class<?>, Collection<EventMonitor>> asynchronousMonitors = new ConcurrentHashMap<>();

    private ConcurrentMap<Class<?>, Collection<EventMonitor>> synchronousMonitors = new ConcurrentHashMap<>();

    EventBus() {
    }

    public EventBus(int queueSize, int poolSize, int poolWait) {
        this.queueSize = queueSize;
        this.poolSize = poolSize;
        this.poolWait = poolWait;
    }

    private class EventTask implements Runnable {

        private Object event;

        private EventTask(Object event) {
            this.event = event;
        }

        @Override
        public void run() {
            Collection<EventMonitor> monitors = EventBus.this.asynchronousMonitors.get(event.getClass());
            if (monitors != null) {
                synchronized (monitors) {
                    for (EventMonitor monitor : monitors) {
                        try {
                            monitor.onEvent(event);
                        } catch (Exception exception) {
                            // 记录日志
                            String message = StringUtility.format("监控器[{}]处理异步事件[{}]时异常", monitor.getClass(), event.getClass());
                            logger.error(message, exception);
                        }
                    }
                }
            } else {
                // 事件没有任何监控器
                String message = StringUtility.format("事件[{}]没有任何监控器", event.getClass());
                logger.warn(message);
            }
        }

    };

    /**
     * 启动事件总线
     */
    public void start() {
        NameThreadFactory factory = new NameThreadFactory("EventBus");
        queue = new LinkedBlockingQueue<>(queueSize);
        pool = new ThreadPoolExecutor(poolSize, poolSize, 0, TimeUnit.SECONDS, queue, factory);
        logger.info("事件总线已启动");
    }

    /**
     * 停止事件总线
     */
    public void stop() {
        // 等待事件处理完毕
        while (true) {
            if (queue.isEmpty()) {
                break;
            }
            Thread.yield();
        }
        pool.shutdown();
        try {
            if (!pool.awaitTermination(poolWait, TimeUnit.SECONDS)) {
                logger.error("事件总线无法在等待时间内完成,可能存在部分事件丢失");
                pool.shutdownNow();
            }
        } catch (InterruptedException exception) {
            logger.error("事件总线由于在等待时间内中断,可能存在部分事件丢失");
            pool.shutdownNow();
        }
        logger.info("事件总已停止");
    }

    /**
     * 注册事件监控者
     * 
     * @param topic
     * @param monitor
     * @param synchronous
     */
    public void registerMonitor(Class<?> topic, EventMonitor monitor, boolean synchronous) {
        if (synchronous) {
            synchronized (this.synchronousMonitors) {
                Collection<EventMonitor> monitors = this.synchronousMonitors.get(topic);
                if (monitors == null) {
                    monitors = new HashSet<>();
                    this.synchronousMonitors.put(topic, monitors);
                }
                synchronized (monitors) {
                    monitors.add(monitor);
                }
            }
        } else {
            synchronized (this.asynchronousMonitors) {
                Collection<EventMonitor> monitors = this.asynchronousMonitors.get(topic);
                if (monitors == null) {
                    monitors = new HashSet<>();
                    this.asynchronousMonitors.put(topic, monitors);
                }
                synchronized (monitors) {
                    monitors.add(monitor);
                }
            }
        }
    }

    /**
     * 注销事件监控者
     * 
     * @param topic
     * @param monitor
     */
    public void unregisterMonitor(Class<?> topic, EventMonitor monitor) {
        synchronized (this.synchronousMonitors) {
            Collection<EventMonitor> monitors = this.synchronousMonitors.get(topic);
            if (monitors != null) {
                synchronized (monitors) {
                    monitors.remove(monitor);
                }
                if (monitors.isEmpty()) {
                    this.synchronousMonitors.remove(topic);
                }
            }
        }
        synchronized (this.asynchronousMonitors) {
            Collection<EventMonitor> monitors = this.asynchronousMonitors.get(topic);
            if (monitors != null) {
                synchronized (monitors) {
                    monitors.remove(monitor);
                }
                if (monitors.isEmpty()) {
                    this.asynchronousMonitors.remove(topic);
                }
            }
        }
    }

    /**
     * 触发事件
     * 
     * @param event
     */
    public void triggerEvent(Object event) {
        // 执行异步监控器
        pool.submit(new EventTask(event));

        // 执行同步监控器
        Collection<EventMonitor> monitors = this.synchronousMonitors.get(event.getClass());
        if (monitors == null) {
            return;
        }
        for (EventMonitor monitor : monitors) {
            try {
                monitor.onEvent(event);
            } catch (Exception exception) {
                // 记录日志
                String message = StringUtility.format("监控器[{}]处理同步事件[{}]时异常", monitor.getClass(), event.getClass());
                logger.error(message, exception);
            }
        }
    }

}
