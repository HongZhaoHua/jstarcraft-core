package com.jstarcraft.core.common.lockable;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jstarcraft.core.common.reflection.ReflectionUtility;

/**
 * 锁切面
 * 
 * @author Birdy
 */
@Aspect
public class LockableAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(LockableAspect.class);

    /** 方法映射 */
    private ConcurrentHashMap<Method, LockableStrategy> strategies = new ConcurrentHashMap<>();
    /** 标记映射(用于非强制锁) */
    private ThreadLocal<Object> marks = new ThreadLocal<>();

    /** 锁方法拦截处理 */
    @Around("@annotation(lock4Method)")
    public Object execute(ProceedingJoinPoint point, LockableMethod lock4Method) throws Throwable {
        Signature signature = point.getSignature();
        if (lock4Method.value()) {
            // 强制锁处理
            return execute(lock4Method.strategy(), point, signature);
        } else {
            // 非强制锁处理
            if (marks.get() == null) {
                marks.set(LockableAspect.class);
                try {
                    Object result = execute(lock4Method.strategy(), point, signature);
                    return result;
                } finally {
                    marks.remove();
                }
            } else {
                return point.proceed(point.getArgs());
            }
        }
    }

    private Object execute(Class<? extends LockableStrategy> clazz, ProceedingJoinPoint point, Signature signature) throws Throwable {
        Method method = ((MethodSignature) signature).getMethod();
        // 获取锁策略
        LockableStrategy strategy = strategies.get(method);
        if (strategy == null) {
            synchronized (method) {
                if (strategy == null) {
                    strategy = ReflectionUtility.getInstance(clazz, method);
                    strategies.put(method, strategy);
                }
            }
        }
        Object[] arguments = point.getArgs();
        try (Lockable lock = strategy.getLock(arguments)) {
            lock.open();
            return point.proceed(arguments);
        }
    }

}
