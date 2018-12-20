package com.jstarcraft.core.cache.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.event.ContextClosedEvent;

/**
 * 缓存服务停止
 * 
 * <pre>
 * 用于代替{@link @PreDestroy}.
 * 保证在缓存服务停止前以及其它{@link @PreDestroy}方法前执行.
 * 本质基于Spring的容器事件:{@link ContextClosedEvent}.
 * </pre>
 * 
 * @author Birdy
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface BeforeCacheStoped {

}
