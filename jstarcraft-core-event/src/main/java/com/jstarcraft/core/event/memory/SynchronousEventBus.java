package com.jstarcraft.core.event.memory;

import com.jstarcraft.core.event.AbstractEventBus;
import com.jstarcraft.core.event.EventManager;
import com.jstarcraft.core.event.EventMode;
import com.jstarcraft.core.event.EventMonitor;
import com.jstarcraft.core.utility.RandomUtility;
import com.jstarcraft.core.utility.StringUtility;

/**
 * 同步事件总线
 * 
 * @author Birdy
 *
 */
public class SynchronousEventBus extends AbstractEventBus {

    public SynchronousEventBus(EventMode mode) {
        super(mode);
    }

    @Override
    public void triggerEvent(Object event) {
        // 执行同步监控器
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
                        String message = StringUtility.format("监控器[{}]处理同步事件[{}]时异常", monitor.getClass(), event.getClass());
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

}
