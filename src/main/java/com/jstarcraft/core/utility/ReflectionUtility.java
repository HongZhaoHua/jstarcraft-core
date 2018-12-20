package com.jstarcraft.core.utility;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.WeakHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;

/**
 * 反射工具
 * 
 * @author Birdy
 */
public abstract class ReflectionUtility extends ReflectionUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(ReflectionUtility.class);

	/** 属性描述符的缓存 */
	private static final WeakHashMap<Class<?>, PropertyDescriptor[]> DESCRIPTORS_CACHE = new WeakHashMap<Class<?>, PropertyDescriptor[]>();

	/**
	 * 查找指定类型包含指定注解的唯一字段
	 * 
	 * @param clazz
	 * @param annotation
	 * @return
	 */
	public static Field uniqueField(Class<?> clazz, Class<? extends Annotation> annotation) {
		List<Field> fields = findFields(clazz, annotation);
		if (fields.size() != 1) {
			String message = StringUtility.format("字段不是唯一", clazz);
			throw new RuntimeException(message);
		}
		return fields.get(0);
	}

	/**
	 * 查找指定类型包含指定注解的所有字段
	 * 
	 * @param clazz
	 * @param annotation
	 * @return
	 */
	public static List<Field> findFields(Class<?> clazz, Class<? extends Annotation> annotation) {
		LinkedList<Field> fields = new LinkedList<Field>();
		doWithFields(clazz, new FieldCallback() {
			@Override
			public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
				if (field.isAnnotationPresent(annotation)) {
					fields.add(field);
				}
			}
		});
		return fields;
	}

	/**
	 * 查找指定类型包含指定注解的唯一方法
	 * 
	 * @param clazz
	 * @param annotation
	 * @return
	 */
	public static Method uniqueMethod(Class<?> clazz, Class<? extends Annotation> annotation) {
		List<Method> methods = findMethods(clazz, annotation);
		if (methods.size() != 1) {
			String message = StringUtility.format("方法不是唯一", clazz);
			throw new RuntimeException(message);
		}
		return methods.get(0);
	}

	/**
	 * 查找指定类型包含指定注解的所有方法
	 * 
	 * @param clazz
	 * @param annotation
	 * @return
	 */
	public static List<Method> findMethods(Class<?> clazz, Class<? extends Annotation> annotation) {
		LinkedList<Method> methods = new LinkedList<Method>();
		doWithMethods(clazz, new MethodCallback() {
			@Override
			public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
				if (method.isAnnotationPresent(annotation)) {
					methods.add(method);
				}
			}
		});
		return methods;
	}

	/**
	 * 判断虚拟参数与实际参数是否匹配
	 * 
	 * @param variable
	 *            是否为可变
	 * @param parameterTypes
	 *            虚拟参数
	 * @param argumentTypes
	 *            实际参数
	 * @return
	 */
	private static boolean match(boolean variable, Class<?>[] parameterTypes, Class<?>[] argumentTypes) {
		if (variable) {
			for (int index = 0; index < parameterTypes.length - 1 && index < argumentTypes.length; index++) {
				if (!ClassUtility.isAssignable(argumentTypes[index], parameterTypes[index], true)) {
					return false;
				}
			}
			Class<?> ComponentType = parameterTypes[parameterTypes.length - 1].getComponentType();
			for (int index = 0; index < argumentTypes.length; index++) {
				if (!ClassUtility.isAssignable(argumentTypes[index], ComponentType, true)) {
					return false;
				}
			}
			return true;
		} else {
			return ClassUtility.isAssignable(argumentTypes, parameterTypes, true);
		}
	}

	/**
	 * 通过指定参数获取指定类型的实例
	 * 
	 * @param clazz
	 * @param parameters
	 * @return
	 */
	public static <T> T getInstance(Class<T> clazz, Object... parameters) {
		Class<?>[] classes = new Class[parameters.length];
		for (int index = 0; index < parameters.length; index++) {
			classes[index] = parameters[index].getClass();
		}
		try {
			Constructor<?> constructor = clazz.getDeclaredConstructor(classes);
			constructor.setAccessible(true);
			return (T) constructor.newInstance(parameters);
		} catch (NoSuchMethodException exception) {
			Constructor<?>[] constructors = clazz.getDeclaredConstructors();
			try {
				for (Constructor<?> constructor : constructors) {
					if (match(constructor.isVarArgs(), constructor.getParameterTypes(), classes)) {
						constructor.setAccessible(true);
						return (T) constructor.newInstance(parameters);
					}
				}
				throw new RuntimeException();
			} catch (Exception throwable) {
				throw new RuntimeException(throwable);
			}
		} catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	/**
	 * 获取指定类型的属性描述符
	 * 
	 * @param clazz
	 * @return
	 */
	public static PropertyDescriptor[] getPropertyDescriptors(Class<?> clazz) {
		if (clazz == null) {
			throw new IllegalArgumentException();
		}
		PropertyDescriptor[] descriptors = DESCRIPTORS_CACHE.get(clazz);
		if (descriptors != null) {
			return descriptors;
		}
		try {
			BeanInfo information = Introspector.getBeanInfo(clazz);
			descriptors = information.getPropertyDescriptors();
			if (descriptors == null) {
				descriptors = new PropertyDescriptor[0];
			}
			for (int index = 0; index < descriptors.length; index++) {
				PropertyDescriptor descriptor = descriptors[index];
				String name = descriptor.getName();
				// 忽略getClass方法
				if (name.equals("class")) {
					continue;
				}
				// 属性类型
				Class<?> type = descriptor.getPropertyType();
				name = StringUtility.capitalize(name);
				// Getter
				if (descriptor.getReadMethod() == null) {
					try {
						String getter = ((type == Boolean.class || type == boolean.class) ? "is" : "get") + name;
						Method method = clazz.getDeclaredMethod(getter);
						if (method != null) {
							descriptor.setReadMethod(method);
						}
					} catch (Exception exception) {
						LOGGER.debug("属性[{}]没有getter方法", name);
					}
				}
				// Setter
				if (descriptor.getWriteMethod() == null) {
					try {
						String setter = "set" + name;
						Method method = clazz.getDeclaredMethod(setter, type);
						if (method != null) {
							descriptor.setWriteMethod(method);
						}
					} catch (Exception exception) {
						LOGGER.debug("属性[{}]没有setter方法", name);
					}
				}
			}
			DESCRIPTORS_CACHE.put(clazz, descriptors);
			return descriptors;
		} catch (IntrospectionException exception) {
			return new PropertyDescriptor[0];
		}
	}

}
