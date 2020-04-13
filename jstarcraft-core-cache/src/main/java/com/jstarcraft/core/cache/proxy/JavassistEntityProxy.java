package com.jstarcraft.core.cache.proxy;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;

import com.jstarcraft.core.cache.CacheInformation;
import com.jstarcraft.core.cache.annotation.CacheChange;
import com.jstarcraft.core.common.conversion.ConversionUtility;
import com.jstarcraft.core.utility.StringUtility;

import javassist.CtClass;
import javassist.CtMethod;

/**
 * 实体代理
 * 
 * @author Birdy
 *
 */
public class JavassistEntityProxy extends JavassistProxy {

    public JavassistEntityProxy(ProxyManager proxyManager, CacheInformation cacheInformation) {
        super(proxyManager, cacheInformation);
    }

    /**
     * 代理方法
     * 
     * <pre>
     * // TODO 索引变更部分
     * CacheInformation cacheInformation = _manager.getCacheInformation();
     * String[] values = _indexChange != null ? _indexChange.values() : new String[] {};
     * TreeSet<String> indexNames = new TreeSet(Arrays.asList(values));
     * LinkedList<Lock> locks = new LinkedList<Lock>();
     * for (String name : indexNames) {
     *     locks.addLast(cacheInformation.getIndexWriteLock(name));
     * }
     * Object value = null;
     * try {
     *     for (Lock lock : locks) {
     *         lock.lock();
     *     }
     *     Map<String, Object> oldIndexValues = cacheInformation.getIndexValues(_object, indexNames);
     *     value = method.invoke(_object);
     *     Map<String, Object> newIndexValues = cacheInformation.getIndexValues(_object, indexNames);
     *     boolean result = true;
     *     for (Entry<String, Object> entry : newIndexValues.entrySet()) {
     *         if (_manager.hasUnique(entry.getKey(), entry.getValue())) {
     *             result = false;
     *             break;
     *         }
     *     }
     *     if (result) {
     *         for (Entry<String, Object> entry : newIndexValues.entrySet()) {
     *             _manager.modifyUnique(_object.getId(), entry.getKey(), entry.getValue());
     *         }
     *     } else {
     *         cacheInformation.setIndexValues(_object, oldIndexValues);
     *     }
     * } finally {
     *     for (Lock lock : locks) {
     *         lock.unlock();
     *     }
     * }
     * // TODO 数据变更部分
     * if (_dataChange != null) {
     *     if (returnType == void.class) {
     *         _manager.modifyDatas(_object.getId(), _object);
     *     } else {
     *         TreeSet<String> resultValues = new TreeSet(Arrays.asList(_dataChange.values()));
     *         if (resultValues.contains(value.toString())) {
     *             _manager.modifyDatas(_object.getId(), _object);
     *         }
     *     }
     * }
     * // TODO 返回值部分
     * if (returnType != void.class) {
     *     return value;
     * }
     * </pre>
     */
    final void proxyMethod(Class<?> clazz, CtClass proxyClazz, Method method, CacheChange cacheChange) throws Exception {
        Class<?> returnType = method.getReturnType();
        CtMethod proxyMethod = new CtMethod(classPool.get(returnType.getName()), method.getName(), toProxyClasses(method.getParameterTypes()), proxyClazz);
        proxyMethod.setModifiers(Modifier.PUBLIC);
        if (method.getExceptionTypes().length != 0) {
            proxyMethod.setExceptionTypes(toProxyClasses(method.getExceptionTypes()));
        }
        StringBuilder methodBuilder = new StringBuilder("{");
        methodBuilder.append(StringUtility.format("{} methodId = {}.valueOf({});", Integer.class.getName(), Integer.class.getName(), cacheInformation.getMethodId(method)));
        methodBuilder.append(StringUtility.format("{} changeValues = _information.getMethodChanges(methodId);", HashSet.class.getName()));
        // TODO 索引变更部分
        // if (!indexChanges.isEmpty()) {
        // methodBuilder.append(StringUtility.format("{} indexNames = ({})
        // (keyValue.getKey());", TreeSet.class.getName(), TreeSet.class.getName()));
        // methodBuilder.append(StringUtility.format("{} newIndexValues = new {}();",
        // HashMap.class.getName(), HashMap.class.getName()));
        // for (Entry<IndexChange, Integer> entry : indexChanges.entrySet()) {
        // methodBuilder.append(StringUtility.format("indexNames.add(\"{}\");",
        // entry.getKey().value()));
        // methodBuilder.append(StringUtility.format("newIndexValues.put(\"{}\",
        // {}.primitiveToWrap(${}));", entry.getKey().value(),
        // ConversionUtility.class.getName(), entry.getValue()));
        // }
        // }
        if (returnType != void.class) {
            String typeName = returnType.isArray() ? toArrayType(returnType) : returnType.getName();
            if (returnType.isPrimitive()) {
                methodBuilder.append(StringUtility.format("{} value;", typeName));
            } else {
                methodBuilder.append(StringUtility.format("{} value = null;", typeName));
            }
        }
        methodBuilder.append(StringUtility.format("try {"));
        // if (!indexChanges.isEmpty()) {
        // methodBuilder.append(StringUtility.format("
        // _information.lockIndexWriteLocks(indexNames);"));
        // methodBuilder.append(StringUtility.format(" {} checkIterator =
        // newIndexValues.entrySet().iterator();", Iterator.class.getName()));
        // methodBuilder.append(StringUtility.format(" while(checkIterator.hasNext())
        // {"));
        // methodBuilder.append(StringUtility.format(" {} entry =
        // checkIterator.next();", Entry.class.getName()));
        // methodBuilder.append(StringUtility.format(" boolean has =
        // _manager.hasIndex((String) entry.getKey(), entry.getValue());"));
        // methodBuilder.append(StringUtility.format(" if (has) {"));
        // methodBuilder.append(StringUtility.format(" throw new {}();",
        // JavassistEntityProxy.TYPE_INDEX_EXCEPTION));
        // methodBuilder.append(StringUtility.format(" }"));
        // methodBuilder.append(StringUtility.format(" }"));
        // methodBuilder.append(StringUtility.format(" {} oldIndexValues =
        // _information.getIndexValues(_instance, indexNames);", Map.class.getName()));
        // }
        if (returnType != void.class) {
            methodBuilder.append(StringUtility.format("	value = super.{}($$);", method.getName()));
        } else {
            methodBuilder.append(StringUtility.format("	super.{}($$);", method.getName()));
        }
        // TODO 数据变更部分
        if (cacheChange != null) {
            if (returnType == void.class) {
                // if (!indexChanges.isEmpty()) {
                // methodBuilder.append(StringUtility.format(" _manager.modifyIndexes(_instance,
                // newIndexValues, oldIndexValues);"));
                // }
                methodBuilder.append(StringUtility.format(" _manager.modifyInstance(this);"));
            } else {
                if (cacheChange.values().length > 0) {
                    methodBuilder.append(StringUtility.format(" if (changeValues.contains({}.primitiveToWrap(value))) {", ConversionUtility.class.getName()));
                }
                // if (!indexChanges.isEmpty()) {
                // methodBuilder.append(StringUtility.format(" _manager.modifyIndexes(_instance,
                // newIndexValues, oldIndexValues);"));
                // }
                methodBuilder.append(StringUtility.format("		_manager.modifyInstance(this);"));
                if (cacheChange.values().length > 0) {
                    methodBuilder.append(StringUtility.format(" }"));
                }
            }
        } else {
            // if (!indexChanges.isEmpty()) {
            // methodBuilder.append(StringUtility.format(" _manager.modifyIndexes(_instance,
            // newIndexValues, oldIndexValues);"));
            // }
        }
        methodBuilder.append(StringUtility.format("} finally {"));
        // if (!indexChanges.isEmpty()) {
        // methodBuilder.append(StringUtility.format("
        // _information.unlockIndexWriteLocks(indexNames);"));
        // }
        methodBuilder.append(StringUtility.format("}"));
        // TODO 返回值部分
        if (returnType != void.class) {
            methodBuilder.append(StringUtility.format("return value;"));
        }
        methodBuilder.append(StringUtility.format("}"));
        proxyMethod.setBody(methodBuilder.toString());
        proxyClazz.addMethod(proxyMethod);
    }

}
