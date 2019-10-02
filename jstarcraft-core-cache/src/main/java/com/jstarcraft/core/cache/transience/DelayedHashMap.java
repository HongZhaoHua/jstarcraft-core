package com.jstarcraft.core.cache.transience;

import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.jstarcraft.core.utility.DelayElement;
import com.jstarcraft.core.utility.SensitivityQueue;

/**
 * 到期哈希映射
 * 
 * <pre>
 * 将数据分成多个桶,锁的粒度小,只要O(1)的复杂度就能删掉到期数据.大部分时间都可以进行get和put操作;
 * </pre>
 * 
 * @author Birdy
 *
 * @param <K>
 * @param <V>
 */
public class DelayedHashMap<K, V> implements Map<K, V> {

    /** 修复时间间隔 */
    private static final long FIX_TIME = 1000;
    /** 定时队列 */
    private static final SensitivityQueue<DelayElement<DelayedHashMap<?, ?>>> QUEUE = new SensitivityQueue<>(FIX_TIME);
    /** 任务线程 */
    private static final ExecutorService EXECUTORS = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    /** 清理线程 */
    private static final Thread CLEANER = new Thread(new Runnable() {

        public void run() {
            try {
                while (true) {
                    DelayElement<DelayedHashMap<?, ?>> element = QUEUE.take();
                    DelayedHashMap<?, ?> content = element.getContent();
                    content.clean();
                    Instant now = Instant.now();
                    Instant expire = now.plusMillis(content.waitTime);
                    element = new DelayElement<>(content, expire);
                    QUEUE.put(element);
                }
            } catch (InterruptedException exception) {
            }
        }

    });

    static {
        CLEANER.setDaemon(true);
        CLEANER.start();
    }

    // 将数据分成多个桶,用链表是因为在头尾的增减操作时O(1)
    private ConcurrentLinkedDeque<ConcurrentHashMap<K, V>> segments;

    private TransienceMonitor monitor;

    private final int waitTime;

    private DelayedHashMap(int expire, int segment, TransienceMonitor monitor) {
        if (segment < 2) {
            throw new IllegalArgumentException("segment must be >= 2");
        }
        this.segments = new ConcurrentLinkedDeque<ConcurrentHashMap<K, V>>();
        for (int index = 0; index < segment; index++) {
            this.segments.addLast(new ConcurrentHashMap<K, V>());
        }
        this.monitor = monitor;
        this.waitTime = expire * 1000 / segment;
    }

    private void clean() {
        // 从尾部删除一个桶,到头部加一个桶.最后一个桶的数据是最旧的;
        final Map<K, V> clean = segments.removeLast();
        segments.addFirst(new ConcurrentHashMap<K, V>());

        if (monitor != null) {
            EXECUTORS.submit(new Runnable() {

                @Override
                public void run() {
                    for (Entry<K, V> keyValue : clean.entrySet()) {
                        try {
                            // TODO 考虑此处可能会阻塞清理线程
                            monitor.notifyExchanged(keyValue.getKey(), keyValue.getValue());
                        } catch (Throwable throwable) {
                            // TODO 异常需要记录
                        }
                    }
                }

            });
        }
    }

    @Override
    public boolean containsKey(Object key) {
        for (ConcurrentHashMap<K, V> segment : segments) {
            if (segment.containsKey(key)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public V get(Object key) {
        for (ConcurrentHashMap<K, V> segment : segments) {
            if (segment.containsKey(key)) {
                return segment.get(key);
            }
        }
        return null;
    }

    @Override
    public V put(K key, V value) {
        Iterator<ConcurrentHashMap<K, V>> iterator = segments.iterator();
        ConcurrentHashMap<K, V> segment = iterator.next();
        segment.put(key, value);
        while (iterator.hasNext()) {
            segment = iterator.next();
            segment.remove(key);
        }
        return null;
    }

    @Override
    public V remove(Object key) {
        for (ConcurrentHashMap<K, V> segment : segments) {
            if (segment.containsKey(key)) {
                return segment.remove(key);
            }
        }
        return null;
    }

    @Override
    public int size() {
        int size = 0;
        for (ConcurrentHashMap<K, V> segment : segments) {
            size += segment.size();
        }
        return size;
    }

    @Override
    public boolean isEmpty() {
        for (ConcurrentHashMap<K, V> segment : segments) {
            if (!segment.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean containsValue(Object value) {
        for (ConcurrentHashMap<K, V> segment : segments) {
            if (segment.containsValue(value)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map) {
        for (Entry<? extends K, ? extends V> keyValue : map.entrySet()) {
            put(keyValue.getKey(), keyValue.getValue());
        }
    }

    @Override
    public void clear() {
        for (ConcurrentHashMap<K, V> segment : segments) {
            segment.clear();
        }
    }

    @Override
    public Set<K> keySet() {
        HashSet<K> keySet = new HashSet<>();
        for (ConcurrentHashMap<K, V> segment : segments) {
            keySet.addAll(segment.keySet());
        }
        return keySet;
    }

    @Override
    public Collection<V> values() {
        Set<K> keySet = keySet();
        HashSet<V> valueSet = new HashSet<>();
        for (K key : keySet) {
            valueSet.add(get(key));
        }
        return valueSet;
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        HashMap<K, V> map = new HashMap<>();
        Set<K> keySet = keySet();
        for (K key : keySet) {
            map.put(key, get(key));
        }
        return map.entrySet();
    }

    public static DelayedHashMap instanceOf(int expire, int segment, TransienceMonitor listener) {
        DelayedHashMap<?, ?> instance = new DelayedHashMap<>(expire, segment, listener);
        Instant instant = Instant.now().plusMillis(instance.waitTime);
        DelayElement<DelayedHashMap<?, ?>> element = new DelayElement(instance, instant);
        QUEUE.put(element);
        return instance;
    }

}
