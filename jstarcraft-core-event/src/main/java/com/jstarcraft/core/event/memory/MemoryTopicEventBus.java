package com.jstarcraft.core.event.memory;

import java.util.concurrent.ExecutorService;

import com.jstarcraft.core.event.AbstractEventBus;
import com.jstarcraft.core.event.EventManager;
import com.jstarcraft.core.event.EventMode;
import com.jstarcraft.core.event.EventMonitor;
import com.jstarcraft.core.utility.StringUtility;

public class MemoryTopicEventBus extends AbstractEventBus {

    private ExecutorService pool;

    public MemoryTopicEventBus(String name, ExecutorService pool) {
        super(EventMode.TOPIC, name);
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
                    for (EventMonitor monitor : manager) {
                        try {
                            monitor.onEvent(event);
                        } catch (Exception exception) {
                            // 记录日志
                            String message = StringUtility.format("监控器[{}]处理内存事件[{}]时异常", monitor.getClass(), event.getClass());
                            logger.error(message, exception);
                        }
                    }
                }
            }
        }

    };

    @Override
    public void triggerEvent(Object event) {
        // 执行监控器
        pool.submit(new EventTask(event));
    }

}
