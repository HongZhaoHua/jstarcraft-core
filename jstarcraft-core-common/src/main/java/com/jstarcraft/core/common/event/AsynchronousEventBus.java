package com.jstarcraft.core.common.event;

import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.jstarcraft.core.utility.NameThreadFactory;
import com.jstarcraft.core.utility.StringUtility;

/**
 * 异步事件总线
 * 
 * @author Birdy
 *
 */
public class AsynchronousEventBus extends LocalEventBus {

    /** 队列大小 */
    private int queueSize;

    /** 池大小 */
    private int poolSize;

    /** 池等待 */
    private int poolWait;

    private BlockingQueue<Runnable> queue;

    private ExecutorService pool;

    public AsynchronousEventBus(int queueSize, int poolSize, int poolWait) {
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
            Collection<EventMonitor> monitors = AsynchronousEventBus.this.topic2Monitors.get(event.getClass());
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

    @Override
    public void triggerEvent(Object event) {
        // 执行异步监控器
        pool.submit(new EventTask(event));
    }

}
