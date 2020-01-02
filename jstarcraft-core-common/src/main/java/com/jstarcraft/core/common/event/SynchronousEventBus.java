package com.jstarcraft.core.common.event;

import java.util.Collection;

import com.jstarcraft.core.utility.StringUtility;

/**
 * 同步事件总线
 * 
 * @author Birdy
 *
 */
public class SynchronousEventBus extends LocalEventBus {

    public SynchronousEventBus() {
    }

    @Override
    public void triggerEvent(Object event) {
        // 执行同步监控器
        Collection<EventMonitor> monitors = this.topic2Monitors.get(event.getClass());
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
