package com.jstarcraft.core.monitor.route.balance;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.jstarcraft.core.common.hash.StringHashFunction;
import com.jstarcraft.core.monitor.route.exception.RouteException;

/**
 * 哈希环
 * 
 * @author Birdy
 *
 */
public class HashCycle<T> {

    /** 哈希函数 */
    private StringHashFunction function;

    /** 从实际到虚拟节点的映射 */
    private HashMap<String, Integer> actual2Virtual = new HashMap<>();

    /** 从虚拟到实际节点的映射 */
    private TreeMap<Integer, T> virtual2Actual = new TreeMap<>();

    public HashCycle(StringHashFunction hashFunction) {
        this.function = hashFunction;
    }

    /**
     * 使用指定键创建哈希节点
     * 
     * @param key
     */
    public synchronized void createNode(String key, T value) {
        int hash = function.hash(key);
        if (virtual2Actual.containsKey(hash)) {
            // 节点已存在或者哈希冲突
            throw new RouteException("哈希环节点已存在或者哈希冲突");
        }
        actual2Virtual.put(key, hash);
        virtual2Actual.put(hash, value);
    }

    /**
     * 使用指定键删除哈希节点
     * 
     * @param key
     */
    public synchronized void deleteNode(String key) {
        Integer hash = actual2Virtual.remove(key);
        if (hash == null) {
            // 节点不存在
            throw new RouteException("哈希环节点不存在");
        }
        virtual2Actual.remove(hash);
    }

    /**
     * 使用指定键选择哈希节点(方向:顺时针)
     * 
     * @param key
     * @return
     */
    public synchronized T selectNode(String key) {
        Integer hash = function.hash(key);
        Entry<Integer, T> keyValue = virtual2Actual.higherEntry(hash);
        if (keyValue == null) {
            keyValue = virtual2Actual.firstEntry();
        }
        return keyValue.getValue();
    }

    public synchronized int getSize() {
        return virtual2Actual.size();
    }

}
