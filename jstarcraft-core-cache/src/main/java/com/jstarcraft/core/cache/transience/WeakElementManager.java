package com.jstarcraft.core.cache.transience;

import java.lang.ref.WeakReference;
import java.util.WeakHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.jstarcraft.core.cache.proxy.ProxyTransformer;
import com.jstarcraft.core.common.identification.IdentityObject;

/**
 * 弱引用元素管理器
 * 
 * @author Birdy
 */
public class WeakElementManager<K extends Comparable, T extends IdentityObject<K>> {

    /** 转换器 */
    private final ProxyTransformer transformer;
    /** 对象缓存 */
    private final WeakHashMap<TransienceElement, WeakReference<TransienceElement>> elements = new WeakHashMap<>();
    /** 读写锁 */
    private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    public WeakElementManager(ProxyTransformer transformer) {
        this.transformer = transformer;
    }

    /**
     * 根据指定缓存对象获取内存元素
     * 
     * @param object
     * @return
     */
    public TransienceElement getElement(T object) {
        Lock lock = readWriteLock.readLock();
        try {
            lock.lock();
            TransienceElement element = new TransienceElement(object);
            WeakReference<TransienceElement> reference = elements.get(element);
            if (reference != null) {
                return reference.get();
            }
            return null;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 放入内存元素
     * 
     * @param object
     * @return
     */
    public TransienceElement putElement(T object) {
        Lock lock = readWriteLock.writeLock();
        try {
            lock.lock();
            TransienceElement key = new TransienceElement(object);
            WeakReference<TransienceElement> value = elements.get(key);
            TransienceElement element;
            if (value != null) {
                element = value.get();
                if (element != null) {
                    return element;
                }
            }
            if (transformer != null) {
                object = transformer.transform(object);
            }
            element = new TransienceElement(object);
            elements.put(element, new WeakReference<TransienceElement>(element));
            return element;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 取出内存元素
     * 
     * @param element
     */
    public void takeElement(T object) {
        Lock lock = readWriteLock.writeLock();
        try {
            lock.lock();
            TransienceElement element = new TransienceElement(object);
            elements.remove(element);
        } finally {
            lock.unlock();
        }
    }

    /**
     * 获取所有内存元素数量
     * 
     * @return
     */
    public int getCount() {
        Lock lock = readWriteLock.readLock();
        try {
            lock.lock();
            return elements.size();
        } finally {
            lock.unlock();
        }
    }

}
