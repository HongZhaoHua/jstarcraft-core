package com.jstarcraft.core.aspect.lock;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 自动锁切面
 * 
 * @author Birdy
 */
@Aspect
public class ChainLockAspect {

	private static final Logger LOGGER = LoggerFactory.getLogger(ChainLockAspect.class);

	/** 链映射 */
	private ConcurrentHashMap<Method, ChainLockManager> factories = new ConcurrentHashMap<Method, ChainLockManager>();
	/** 标记映射(用于非强制锁) */
	private ThreadLocal<Object> marks = new ThreadLocal<Object>();

	/** 锁方法拦截处理 */
	@Around("@annotation(lock4Method)")
	public Object execute(ProceedingJoinPoint point, Lock4Method lock4Method) throws Throwable {
		Signature signature = point.getSignature();
		if (lock4Method.value()) {
			// 强制锁处理
			return execute(point, signature);
		} else {
			// 非强制锁处理
			if (marks.get() == null) {
				marks.set(ChainLockAspect.class);
				try {
					Object result = execute(point, signature);
					return result;
				} finally {
					marks.remove();
				}
			} else {
				return point.proceed(point.getArgs());
			}
		}
	}

	private Object execute(ProceedingJoinPoint point, Signature signature) throws Throwable {
		Method method = ((MethodSignature) signature).getMethod();
		// 获取自动链
		ChainLockManager factory = null;
		synchronized (method) {
			factory = factories.get(method);
			if (factory == null) {
				factory = ChainLockManager.instanceOf(method);
				factories.put(method, factory);
			}
		}

		Object[] arguments = point.getArgs();
		try (ChainLock lock = factory.getLock(arguments)) {
			lock.open();
			return point.proceed(arguments);
		}
	}

}
