package com.jstarcraft.core.cache.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.event.ContextRefreshedEvent;

/**
 * 缓存服务启动
 * 
 * <pre>
 * 用于代替{@link @PostConstruct}.
 * 保证在缓存服务启动后以及其它{@link @PostConstruct}方法后执行.
 * 本质基于Spring的容器事件:{@link ContextRefreshedEvent}.
 * </pre>
 * 
 * @author Birdy
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AfterCacheStarted {

}
