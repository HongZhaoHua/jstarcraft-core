package com.jstarcraft.core.storage.berkeley.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.jstarcraft.core.storage.berkeley.BerkeleyAccessor;
import com.jstarcraft.core.storage.berkeley.annotation.BerkeleyTransaction;

/**
 * 事务方法拦截切面
 * 
 * @author Birdy
 */
@Aspect
public class TransactionAspect {

    private static final Logger logger = LoggerFactory.getLogger(TransactionAspect.class);

    @Autowired
    private BerkeleyAccessor accessor;

    /** 事务上下文集合 */
    private final ThreadLocal<TransactionContext> contexts = new ThreadLocal<>();

    /** 事务方法拦截处理 */
    @Around("@annotation(berkeleyTransaction)")
    public Object execute(ProceedingJoinPoint point, BerkeleyTransaction transaction) throws Throwable {
        TransactionContext context = contexts.get();
        if (context == null) {
            context = new TransactionContext(accessor, transaction);
            contexts.set(context);
        }
        Throwable exception = null;
        do {
            try {
                // 每次循环都设置为null
                exception = null;
                context.increase(); // 计数器+1
                Object value = point.proceed();
                return value;
            } catch (Throwable throwable) {
                exception = throwable;
            } finally {
                context.decrease(exception); // 计数器-1
            }
        } while (context.retry());
        if (context.getCount() == 0) {
            contexts.remove();
        }
        throw exception;
    }

}
