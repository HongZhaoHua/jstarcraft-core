package com.jstarcraft.core.event;

import java.util.Collection;

/**
 * 事件路由器
 * 
 * @author Birdy
 *
 */
public interface EventRouter {

    void routeEvent(Object event, Collection<EventMonitor> monitors);

}
