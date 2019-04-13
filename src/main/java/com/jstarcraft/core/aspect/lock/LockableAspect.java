package com.jstarcraft.core.aspect.lock;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jstarcraft.core.utility.ReflectionUtility;

/**
 * 锁切面
 * 
 * @author Birdy
 */
public class LockableAspect {

	private static final Logger LOGGER = LoggerFactory.getLogger(LockableAspect.class);

	private Class<? extends LockableStrategy> strategyClazz;

	/** 方法映射 */
	private ConcurrentHashMap<Method, LockableStrategy> factories = new ConcurrentHashMap<>();
	/** 标记映射(用于非强制锁) */
	private ThreadLocal<Object> marks = new ThreadLocal<>();

	public LockableAspect(Class<? extends LockableStrategy> strategyClazz) {
		this.strategyClazz = strategyClazz;
	}

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
				marks.set(LockableAspect.class);
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
		LockableStrategy factory = factories.get(method);
		if (factory == null) {
			synchronized (method) {
				if (factory == null) {
					factory = ReflectionUtility.getInstance(strategyClazz, method);
					factories.put(method, factory);
				}
			}
		}
		Object[] arguments = point.getArgs();
		try (Lockable lock = factory.getLock(arguments)) {
			lock.open();
			return point.proceed(arguments);
		}
	}

}
