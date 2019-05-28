package com.jstarcraft.core.cache;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jstarcraft.core.cache.annotation.CacheChange;
import com.jstarcraft.core.cache.annotation.CacheConfiguration;
import com.jstarcraft.core.cache.annotation.CacheConfiguration.Unit;
import com.jstarcraft.core.cache.exception.CacheConfigurationException;
import com.jstarcraft.core.cache.exception.CacheException;
import com.jstarcraft.core.cache.proxy.ProxyObject;
import com.jstarcraft.core.common.conversion.json.JsonUtility;
import com.jstarcraft.core.utility.ClassUtility;
import com.jstarcraft.core.utility.IdentityObject;
import com.jstarcraft.core.utility.ReflectionUtility;
import com.jstarcraft.core.utility.StringUtility;

/**
 * 缓存信息
 * 
 * @author Birdy
 */
public class CacheInformation {

    private static final Logger LOGGER = LoggerFactory.getLogger(EntityCacheManager.class);

    public final static HashSet<Method> OBJECT_METHODS = new HashSet<Method>();

    static {
        for (Method method : Object.class.getDeclaredMethods()) {
            OBJECT_METHODS.add(method);
        }
    }

    /** 缓存类型 */
    private Class<? extends IdentityObject> cacheClass;
    /** 缓存配置 */
    private CacheConfiguration cacheConfiguration;
    /** 索引信息 */
    private Map<String, Field> indexInformations;
    /** 变更信息 */
    private Map<Method, Integer> methodIds;

    private List<HashSet<Object>> methodChanges;

    private CacheInformation() {
    }

    /**
     * 是否存在索引
     * 
     * @return
     */
    public boolean hasIndexes() {
        if (!indexInformations.isEmpty()) {
            return true;
        }
        return false;
    }

    /**
     * 获取索引的名称集合
     * 
     * @return
     */
    public Collection<String> getIndexNames() {
        HashSet<String> result = new HashSet<String>();
        for (String name : indexInformations.keySet()) {
            result.add(name);
        }
        return result;
    }

    /**
     * 获取实体的索引键=值
     * 
     * @param entity
     * @return
     */
    public Comparable getIndexValue(IdentityObject entity, String name) {
        Field information = indexInformations.get(name);
        try {
            Object value = information.get(entity);
            return (Comparable) value;
        } catch (Exception exception) {
            String message = StringUtility.format("获取缓存[{}]的索引[{}]异常", cacheClass.getName(), name);
            LOGGER.error(message, exception);
            throw new CacheException(message, exception);
        }
    }

    /**
     * 获取实体的索引键=值
     * 
     * @param entity
     * @return
     */
    public Map<String, Comparable> getIndexValues(IdentityObject entity) {
        Map<String, Comparable> values = new HashMap<>();
        for (Entry<String, Field> keyValue : indexInformations.entrySet()) {
            try {
                Object value = keyValue.getValue().get(entity);
                values.put(keyValue.getKey(), (Comparable) value);
            } catch (Exception exception) {
                String message = StringUtility.format("获取缓存[{}]的索引[{}]异常", cacheClass.getName(), keyValue.getKey());
                LOGGER.error(message, exception);
                throw new CacheException(message, exception);
            }
        }
        return values;
    }

    /**
     * 是否存在指定名称的索引
     * 
     * @param name
     * @return
     */
    public boolean hasIndex(String name) {
        if (indexInformations == null) {
            return false;
        }
        if (indexInformations.containsKey(name)) {
            return true;
        }
        return false;
    }

    /**
     * 获取缓存类型
     * 
     * @return
     */
    public Class<? extends IdentityObject> getCacheClass() {
        return cacheClass;
    }

    /**
     * 获取缓存单位
     * 
     * @return
     */
    public Unit getCacheUnit() {
        return cacheConfiguration.unit();
    }

    /**
     * 获取缓存配置
     * 
     * @return
     */
    public CacheConfiguration getCacheConfiguration() {
        return cacheConfiguration;
    }

    public int getMethodId(Method method) {
        return methodIds.get(method);
    }

    public HashSet<Object> getMethodChanges(Integer methodId) {
        return methodChanges.get(methodId);
    }

    /**
     * 检查指定类型是否为缓存类型
     * 
     * @param clazz
     * @return
     */
    public static boolean checkCacheClass(Class<?> clazz) {
        if (Modifier.isAbstract(clazz.getModifiers())) {
            return false;
        }
        if (Modifier.isInterface(clazz.getModifiers())) {
            return false;
        }
        if (!IdentityObject.class.isAssignableFrom(clazz)) {
            return false;
        }
        if (!clazz.isAnnotationPresent(CacheConfiguration.class)) {
            return false;
        }
        return true;
    }

    /**
     * 
     * @param clazz
     * @param memoryStrategies
     * @return
     */
    public static CacheInformation instanceOf(Class<? extends IdentityObject> clazz) {
        CacheInformation instance = new CacheInformation();
        instance.cacheClass = clazz;
        instance.cacheConfiguration = clazz.getAnnotation(CacheConfiguration.class);
        // 索引信息
        HashMap<String, Field> indexInformations = new HashMap<>();
        if (instance.cacheConfiguration.unit().equals(Unit.REGION) && instance.cacheConfiguration.indexes().length != 1) {
            String message = StringUtility.format("类型[{}]的缓存配置必须有且只有一个索引", clazz.getName());
            LOGGER.error(message);
            throw new CacheConfigurationException(message);
        }
        for (String index : instance.cacheConfiguration.indexes()) {
            Field field = null;
            try {
                field = ReflectionUtility.getField(clazz, index);
            } catch (Exception exception) {
                String message = StringUtility.format("类型[{}]的缓存配置指定的索引[{}]不存在", clazz.getName(), index);
                LOGGER.error(message, exception);
                throw new CacheConfigurationException(message);
            }
            if (!Comparable.class.isAssignableFrom(ClassUtility.primitiveToWrapper(field.getType()))) {
                String message = StringUtility.format("类型[{}]的标识/索引[{}]必须为Comparable类型", clazz.getName(), field.getName());
                LOGGER.error(message);
                throw new CacheConfigurationException(message);
            }
            ReflectionUtility.makeAccessible(field);
            indexInformations.put(field.getName(), field);
        }
        instance.indexInformations = indexInformations;
        // 方法信息
        HashMap<Method, Integer> methodIds = new HashMap<>();
        List<HashSet<Object>> methodChanges = new LinkedList<>();
        ReflectionUtility.doWithMethods(clazz, (method) -> {
            Integer methodId = methodIds.get(method);
            if (methodId == null) {
                methodId = methodIds.size();
                methodIds.put(method, methodId);
            }
            HashSet<Object> cacheValues = new HashSet<>();
            CacheChange cacheChange = method.getAnnotation(CacheChange.class);
            if (cacheChange != null) {
                for (String json : cacheChange.values()) {
                    cacheValues.add(JsonUtility.string2Object(json, method.getGenericReturnType()));
                }
            }
            methodChanges.add(cacheValues);
        }, (method) -> {
            if (OBJECT_METHODS.contains(method)) {
                return false;
            }
            if (Modifier.isFinal(method.getModifiers()) || Modifier.isStatic(method.getModifiers()) || Modifier.isPrivate(method.getModifiers())) {
                return false;
            }
            return true;
        });
        instance.methodIds = methodIds;
        instance.methodChanges = new ArrayList<>(methodChanges);
        return instance;
    }

}
