package com.jstarcraft.core.event.memory;

import java.util.concurrent.ExecutorService;

import com.jstarcraft.core.event.AbstractEventBus;
import com.jstarcraft.core.event.EventManager;
import com.jstarcraft.core.event.EventMode;
import com.jstarcraft.core.event.EventMonitor;
import com.jstarcraft.core.utility.RandomUtility;
import com.jstarcraft.core.utility.StringUtility;

/**
 * 异步事件总线
 * 
 * @author Birdy
 *
 */
public class AsynchronousEventBus extends AbstractEventBus {

    private ExecutorService pool;

    public AsynchronousEventBus(EventMode mode, ExecutorService pool) {
        super(mode);
        this.pool = pool;
    }

    private class EventTask implements Runnable {

        private Object event;

        private EventTask(Object event) {
            this.event = event;
        }

        @Override
        public void run() {
            EventManager manager = address2Managers.get(event.getClass());
            if (manager != null) {
                synchronized (manager) {
                    switch (mode) {
                    case QUEUE: {
                        int size = manager.getSize();
                        int index = RandomUtility.randomInteger(size);
                        EventMonitor monitor = manager.getMonitor(index);
                        try {
                            monitor.onEvent(event);
                        } catch (Exception exception) {
                            // 记录日志
                            String message = StringUtility.format("监控器[{}]处理异步事件[{}]时异常", monitor.getClass(), event.getClass());
                            logger.error(message, exception);
                        }
                        break;
                    }
                    case TOPIC: {
                        for (EventMonitor monitor : manager) {
                            try {
                                monitor.onEvent(event);
                            } catch (Exception exception) {
                                // 记录日志
                                String message = StringUtility.format("监控器[{}]处理异步事件[{}]时异常", monitor.getClass(), event.getClass());
                                logger.error(message, exception);
                            }
                        }
                        break;
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

    @Override
    public void triggerEvent(Object event) {
        // 执行异步监控器
        pool.submit(new EventTask(event));
    }

}
