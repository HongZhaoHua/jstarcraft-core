package com.jstarcraft.core.common.lockable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.unimi.dsi.fastutil.ints.Int2BooleanMap;
import it.unimi.dsi.fastutil.ints.Int2BooleanOpenHashMap;

/**
 * 链锁管理器
 * 
 * @author Birdy
 */
public class ChainLockableStrategy implements LockableStrategy {

    private static final Logger logger = LoggerFactory.getLogger(ChainLockableStrategy.class);

    /** 参数位置与锁形式(true为锁参数;false为锁元素;) */
    private Int2BooleanMap configurations;

    public ChainLockableStrategy(Method method) {
        this.configurations = new Int2BooleanOpenHashMap();
        Annotation[][] annotations = method.getParameterAnnotations();
        for (int index = 0; index < annotations.length; index++) {
            for (Annotation annotation : annotations[index]) {
                if (annotation instanceof LockableParameter) {
                    this.configurations.put(index, true);
                    break;
                }
                if (annotation instanceof LockableElement) {
                    // TODO 是否要检查参数的类型为数组/集合/映射
                    this.configurations.put(index, false);
                    break;
                }
            }
        }
    }

    /**
     * 获取指定的参数列表对应的链锁
     * 
     * @param arguments
     * @return
     */
    @Override
    public ChainLockable getLock(Object... arguments) {
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
            logger.error("不支持的类型[{}]", argument.getClass().getName());
        }
        return ChainLockable.instanceOf(chain.toArray(new Comparable[chain.size()]));
    }

}
