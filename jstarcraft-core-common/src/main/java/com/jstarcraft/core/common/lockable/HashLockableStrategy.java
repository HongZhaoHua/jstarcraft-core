package com.jstarcraft.core.common.lockable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.unimi.dsi.fastutil.ints.Int2BooleanMap;
import it.unimi.dsi.fastutil.ints.Int2BooleanOpenHashMap;

/**
 * 哈希锁管理器
 * 
 * @author Birdy
 */
public class HashLockableStrategy implements LockableStrategy {

    private static final Logger logger = LoggerFactory.getLogger(HashLockableStrategy.class);

    private static final int size = 1000;

    private HashLockable[] lockables;

    /** 参数位置与锁形式(true为锁参数;false为锁元素;) */
    private Int2BooleanMap configurations;

    public HashLockableStrategy(Method method) {
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
        this.lockables = new HashLockable[size];
        for (int index = 0; index < size; index++) {
            this.lockables[index] = new HashLockable();
        }
    }

    /**
     * 获取指定的参数列表对应的链锁
     * 
     * @param arguments
     * @return
     */
    @Override
    public HashLockable getLock(Object... arguments) {
        int hash = 0;
        for (Int2BooleanMap.Entry keyValue : configurations.int2BooleanEntrySet()) {
            Object argument = arguments[keyValue.getIntKey()];
            if (argument == null) {
                continue;
            }
            if (keyValue.getBooleanValue()) {
                hash += argument.hashCode();
                continue;
            }
            if (argument.getClass().isArray()) {
                for (int index = 0; index < Array.getLength(argument); index++) {
                    Object element = Array.get(argument, index);
                    if (element == null) {
                        continue;
                    }
                    hash += element.hashCode();
                }
                continue;
            }
            if (argument instanceof Collection) {
                for (Object element : (Collection) argument) {
                    if (element == null) {
                        continue;
                    }
                    hash += element.hashCode();
                }
                continue;
            }
            if (argument instanceof Map) {
                for (Object element : ((Map) argument).values()) {
                    if (element == null) {
                        continue;
                    }
                    hash += element.hashCode();
                }
                continue;
            }
            logger.error("不支持的类型[{}]", argument.getClass().getName());
        }
        hash = Math.abs(hash % size);
        return lockables[hash];
    }

}
