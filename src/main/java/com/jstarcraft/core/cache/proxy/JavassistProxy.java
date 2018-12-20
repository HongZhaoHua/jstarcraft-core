package com.jstarcraft.core.cache.proxy;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jstarcraft.core.cache.CacheInformation;
import com.jstarcraft.core.cache.CacheObject;
import com.jstarcraft.core.cache.annotation.CacheChange;
import com.jstarcraft.core.cache.exception.CacheException;
import com.jstarcraft.core.cache.exception.CacheIndexException;
import com.jstarcraft.core.cache.exception.CacheProxyException;
import com.jstarcraft.core.utility.ReflectionUtility;
import com.jstarcraft.core.utility.StringUtility;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ConstPool;
import javassist.bytecode.Descriptor;
import javassist.bytecode.MethodInfo;

/**
 * 代理转换器
 * 
 * @author Birdy
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
abstract class JavassistProxy implements ProxyTransformer {

	/** 类:代理后缀 */
	protected final static String CLASS_SUFFIX = "$PROXY";

	/** 字段:引用代理缓存信息 */
	protected final static String FIELD_INFORMATION = "_information";

	/** 字段:引用代理缓存对象 */
	protected final static String FIELD_INSTANCE = "_instance";

	/** 字段:引用代理缓存管理 */
	protected final static String FIELD_MANAGER = "_manager";

	/** 方法:获取缓存标识 */
	protected final static String METHOD_GET_ID = "getId";

	/** 方法:获取缓存对象 */
	protected final static String METHOD_GET_INSTANCE = "getInstance";

	/** 类型:缓存信息 */
	protected final static String TYPE_CACHE_INFORMATION = CacheInformation.class.getName();

	/** 类型:索引字段异常 */
	protected final static String TYPE_INDEX_EXCEPTION = CacheIndexException.class.getName();

	private final static HashMap<String, Class<?>[]> DEFAULT_METHODS = new HashMap<>();

	static {
		for (Method method : Object.class.getDeclaredMethods()) {
			DEFAULT_METHODS.put(method.getName(), method.getParameterTypes());
		}
		for (Method method : Comparable.class.getDeclaredMethods()) {
			DEFAULT_METHODS.put(method.getName(), method.getParameterTypes());
		}
	}

	protected static final ClassPool classPool = ClassPool.getDefault();

	protected ProxyManager proxyManager;

	protected CacheInformation cacheInformation;

	protected ConcurrentHashMap<Class<?>, Constructor<? extends CacheObject<?>>> constructors = new ConcurrentHashMap<>();

	JavassistProxy(ProxyManager proxyManager, CacheInformation cacheInformation) {
		this.proxyManager = proxyManager;
		this.cacheInformation = cacheInformation;
	}

	@Override
	public <T extends CacheObject<?>> T transform(T object) {
		Class<? extends CacheObject> clazz = object.getClass();
		if (proxyManager == null) {
			String message = StringUtility.format("指定类[{}]所对应的缓存管理器不存在", clazz.getName());
			throw new CacheProxyException(object, message);
		}
		try {
			Constructor constructor = getConstructor(clazz);
			return (T) constructor.newInstance(object, proxyManager, cacheInformation);
		} catch (Exception exception) {
			String message = StringUtility.format("指定类[{}]代理异常", clazz.getName());
			throw new CacheProxyException(object, message, exception);
		}
	}

	/**
	 * 获取构造器
	 * 
	 * @param clazz
	 * @return
	 * @throws Exception
	 */
	private <T extends CacheObject<?>> Constructor<T> getConstructor(Class<T> clazz) throws Exception {
		if (constructors.containsKey(clazz)) {
			return (Constructor<T>) constructors.get(clazz);
		}
		synchronized (clazz) {
			if (constructors.containsKey(clazz)) {
				return (Constructor<T>) constructors.get(clazz);
			}
			Class current = transformClass(clazz);
			Constructor<T> constructor = current.getConstructor(clazz, ProxyManager.class, CacheInformation.class);
			constructors.put(clazz, constructor);
			return constructor;
		}
	}

	/**
	 * 转换指定类
	 * 
	 * @param clazz
	 * @return
	 * @throws Exception
	 */
	private Class<?> transformClass(final Class<?> clazz) throws Exception {
		CtClass proxyClass = proxyClass(clazz);
		proxyCacheFields(clazz, proxyClass);
		proxyConstructor(clazz, proxyClass);
		proxyCacheMethods(clazz, proxyClass);
		ReflectionUtility.doWithMethods(clazz, (method) -> {
			CacheChange cacheChange = method.getAnnotation(CacheChange.class);
			try {
				proxyMethod(clazz, proxyClass, method, cacheChange);
			} catch (Exception exception) {
				String message = StringUtility.format("缓存类型[{}]转换异常", clazz.getName());
				throw new CacheException(message, exception);
			}
		}, (method) -> {
			Class<?>[] classes = DEFAULT_METHODS.get(method.getName());
			if (classes != null && Arrays.equals(classes, method.getParameterTypes())) {
				return false;
			}
			if (Modifier.isFinal(method.getModifiers()) || Modifier.isStatic(method.getModifiers()) || Modifier.isPrivate(method.getModifiers())) {
				return false;
			}
			if (method.isSynthetic() && method.getName().equals(METHOD_GET_ID)) {
				return false;
			}
			return true;
		});
		return proxyClass.toClass();
	}

	/**
	 * 代理类
	 * 
	 * <pre>
	 * public class [clazz.name]$ENHANCED extends [clazz.name] implements CacheObject {
	 * }
	 * </pre>
	 * 
	 * @param clazz
	 * @return
	 * @throws Exception
	 */
	private CtClass proxyClass(Class<?> clazz) throws Exception {
		CtClass source = classPool.get(clazz.getName());
		CtClass result = classPool.makeClass(clazz.getCanonicalName() + CLASS_SUFFIX);
		result.setSuperclass(source);
		result.setInterfaces(new CtClass[] { classPool.get(ProxyObject.class.getName()) });
		return result;
	}

	/**
	 * 代理构造器
	 * 
	 * <pre>
	 * public [proxyClass.name]([clazz.name] _object, CacheManager _manager) {
	 *     this._object = _object;
	 *     this._manager = _manager;
	 * }
	 * </pre>
	 * 
	 * @param clazz
	 * @param proxyClass
	 * @throws Exception
	 */
	private void proxyConstructor(Class<?> clazz, CtClass proxyClass) throws Exception {
		CtConstructor constructor = new CtConstructor(toProxyClasses(clazz, ProxyManager.class, CacheInformation.class), proxyClass);
		StringBuilder methodBuilder = new StringBuilder("{");
		methodBuilder.append(StringUtility.format("this.{} = $1;", FIELD_INSTANCE));
		methodBuilder.append(StringUtility.format("this.{} = $2;", FIELD_MANAGER));
		methodBuilder.append(StringUtility.format("this.{} = $3;", FIELD_INFORMATION));
		methodBuilder.append("}");
		constructor.setBody(methodBuilder.toString());
		constructor.setModifiers(Modifier.PUBLIC);
		proxyClass.addConstructor(constructor);
	}

	/**
	 * 代理缓存字段
	 * 
	 * <pre>
	 * private final [clazz.name] _object;
	 * private final CacheManager _manager;
	 * </pre>
	 * 
	 * @param clazz
	 * @param proxyClass
	 * @throws Exception
	 */
	private void proxyCacheFields(Class<?> clazz, CtClass proxyClass) throws Exception {
		CtField objectField = new CtField(classPool.get(clazz.getName()), FIELD_INSTANCE, proxyClass);
		objectField.setModifiers(Modifier.PRIVATE + Modifier.FINAL);
		proxyClass.addField(objectField);

		CtField managerField = new CtField(classPool.get(ProxyManager.class.getName()), FIELD_MANAGER, proxyClass);
		managerField.setModifiers(Modifier.PRIVATE + Modifier.FINAL);
		proxyClass.addField(managerField);

		CtField informationField = new CtField(classPool.get(CacheInformation.class.getName()), FIELD_INFORMATION, proxyClass);
		informationField.setModifiers(Modifier.PRIVATE + Modifier.FINAL);
		proxyClass.addField(informationField);

	}

	/**
	 * 代理缓存方法
	 * 
	 * <pre>
	 * public CacheObject getCacheObject() {
	 * 	return this._object;
	 * }
	 * </pre>
	 * 
	 * @param clazz
	 * @param proxyClass
	 * @throws Exception
	 */
	private void proxyCacheMethods(Class<?> clazz, CtClass proxyClass) throws Exception {
		CtClass returnType = classPool.get(CacheObject.class.getName());
		CtClass[] parameters = new CtClass[0];
		ConstPool constPool = proxyClass.getClassFile2().getConstPool();
		String descriptor = Descriptor.ofMethod(returnType, parameters);
		MethodInfo methodInformation = new MethodInfo(constPool, METHOD_GET_INSTANCE, descriptor);
		// JsonIgnore注解
		AnnotationsAttribute annotationsAttribute = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
		javassist.bytecode.annotation.Annotation annotation = new javassist.bytecode.annotation.Annotation(JsonIgnore.class.getName(), constPool);
		annotationsAttribute.addAnnotation(annotation);
		methodInformation.addAttribute(annotationsAttribute);
		CtMethod method = CtMethod.make(methodInformation, proxyClass);
		StringBuilder methodBuilder = new StringBuilder("{");
		methodBuilder.append(StringUtility.format("return this.{};", FIELD_INSTANCE));
		methodBuilder.append("}");
		method.setBody(methodBuilder.toString());
		method.setModifiers(Modifier.PUBLIC);
		proxyClass.addMethod(method);
	}

	/**
	 * 代理方法
	 * 
	 * @param clazz
	 * @param proxyClazz
	 * @param method
	 * @param memoryIndexChange
	 * @param cacheChange
	 * @throws Exception
	 */
	abstract void proxyMethod(Class<?> clazz, CtClass proxyClazz, Method method, CacheChange cacheChange) throws Exception;

	/**
	 * 将指定类转换为{@link CtClass}
	 * 
	 * @param classes
	 * @return
	 * @throws NotFoundException
	 */
	protected CtClass[] toProxyClasses(Class<?>... classes) throws NotFoundException {
		if (classes == null || classes.length == 0) {
			return new CtClass[0];
		}
		CtClass[] result = new CtClass[classes.length];
		for (int index = 0; index < classes.length; index++) {
			result[index] = classPool.get(classes[index].getName());
		}
		return result;
	}

	/**
	 * 将指定数组类型转换为定义
	 * 
	 * @param arrayClass
	 * @return
	 */
	protected String toArrayType(Class<?> arrayClass) {
		Class<?> type = arrayClass.getComponentType();
		return type.getName() + "[]";
	}

}
