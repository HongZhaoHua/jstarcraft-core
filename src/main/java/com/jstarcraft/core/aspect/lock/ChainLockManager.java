package com.jstarcraft.core.aspect.lock;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jstarcraft.core.cache.annotation.Lock4Element;
import com.jstarcraft.core.cache.annotation.Lock4Parameter;

import it.unimi.dsi.fastutil.ints.Int2BooleanMap;
import it.unimi.dsi.fastutil.ints.Int2BooleanOpenHashMap;

/**
 * 链锁管理器
 * 
 * @author Birdy
 */
public class ChainLockManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(ChainLockManager.class);

	/**
	 * 参数位置与锁形式(true为锁参数;false为锁元素;)
	 */
	private Int2BooleanMap configurations = new Int2BooleanOpenHashMap();

	/**
	 * 获取指定的参数列表对应的链锁
	 * 
	 * @param arguments
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public ChainLock getLock(Object[] arguments) {
		LinkedList<Comparable> chain = new LinkedList<>();
		for (Int2BooleanMap.Entry keyValue : configurations.int2BooleanEntrySet()) {
			Object argument = arguments[keyValue.getIntKey()];
			if (argument == null) {
				continue;
			}
			if (keyValue.getBooleanValue()) {
				chain.add((Comparable) argument);
				continue;
			}
			if (argument.getClass().isArray()) {
				for (int index = 0; index < Array.getLength(argument); index++) {
					Object element = Array.get(argument, index);
					if (element == null) {
						continue;
					}
					chain.add((Comparable) element);
				}
				continue;
			}
			if (argument instanceof Collection) {
				for (Object element : (Collection) argument) {
					if (element == null) {
						continue;
					}
					chain.add((Comparable) element);
				}
				continue;
			}
			if (argument instanceof Map) {
				for (Object element : ((Map) argument).values()) {
					if (element == null) {
						continue;
					}
					chain.add((Comparable) element);
				}
				continue;
			}
			LOGGER.error("不支持的类型[{}]", argument.getClass().getName());
		}
		return ChainLock.instanceOf(chain.toArray(new Comparable[chain.size()]));
	}

	public static ChainLockManager instanceOf(Method method) {
		ChainLockManager instance = new ChainLockManager();
		Annotation[][] annotations = method.getParameterAnnotations();
		for (int index = 0; index < annotations.length; index++) {
			for (Annotation annotation : annotations[index]) {
				if (annotation instanceof Lock4Parameter) {
					instance.configurations.put(index, true);
					break;
				}
				if (annotation instanceof Lock4Element) {
					// TODO 是否要检查参数的类型为数组/集合/映射
					instance.configurations.put(index, false);
					break;
				}
			}
		}
		return instance;
	}

}
