package com.jstarcraft.core.storage.definition;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jstarcraft.core.common.reflection.ReflectionUtility;
import com.jstarcraft.core.storage.annotation.StorageId;
import com.jstarcraft.core.storage.annotation.StorageIndex;
import com.jstarcraft.core.storage.exception.StorageException;
import com.jstarcraft.core.utility.StringUtility;

/**
 * 属性访问器
 * 
 * @author Birdy
 *
 */
public interface PropertyAccessor {

	public static final Logger logger = LoggerFactory.getLogger(PropertyAccessor.class);

	/**
	 * 获取属性名称
	 * 
	 * @return
	 */
	public String getName();

	/**
	 * 通过指定的实例获取属性值
	 * 
	 * @param instance
	 * @return
	 */
	public Object getValue(Object instance);

	/**
	 * 是否唯一
	 * 
	 * @return
	 */
	public boolean isUnique();

	/**
	 * 获取比较器
	 * 
	 * @return
	 */
	public Comparator getComparator();

	/**
	 * 获取标识访问器
	 * 
	 * @param clazz
	 * @return
	 */
	public static PropertyAccessor getIdAccessor(Class<?> clazz) {
		List<Field> fields = ReflectionUtility.findFields(clazz, StorageId.class);
		List<Method> methods = ReflectionUtility.findMethods(clazz, StorageId.class);
		if (fields.size() + methods.size() != 1) {
			String message = StringUtility.format("仓储[{}]的主键不是唯一.", clazz);
			logger.error(message);
			throw new StorageException(message);
		}
		PropertyAccessor accessor = null;
		try {
			if (fields.isEmpty()) {
				Method method = methods.get(0);
				StorageId id = method.getAnnotation(StorageId.class);
				Comparator comparator = null;
				if (!id.comparator().isInterface()) {
					comparator = id.comparator().newInstance();
				}
				accessor = new MethodAccessor(method, method.getName(), true, comparator);
			} else {
				Field field = fields.get(0);
				StorageId id = field.getAnnotation(StorageId.class);
				Comparator comparator = null;
				if (!id.comparator().isInterface()) {
					comparator = id.comparator().newInstance();
				}
				accessor = new FieldAccessor(field, field.getName(), true, comparator);
			}
		} catch (Exception exception) {
			String message = StringUtility.format("仓储[{}]的主键比较器无法实例化.", clazz);
			logger.error(message);
			throw new StorageException(message);
		}
		return accessor;
	}

	/**
	 * 获取索引访问器
	 * 
	 * @param clazz
	 * @return
	 */
	public static Map<String, PropertyAccessor> getIndexAccessors(Class<?> clazz) {
		List<Field> fields = ReflectionUtility.findFields(clazz, StorageIndex.class);
		List<Method> methods = ReflectionUtility.findMethods(clazz, StorageIndex.class);
		Map<String, PropertyAccessor> assessors = new HashMap<>();

		try {
			for (Field field : fields) {
				StorageIndex index = field.getAnnotation(StorageIndex.class);
				Comparator comparator = null;
				if (!index.comparator().isInterface()) {
					comparator = index.comparator().newInstance();
				}
				PropertyAccessor accessor = new FieldAccessor(field, index.name(), index.unique(), comparator);
				if (assessors.containsKey(accessor.getName())) {
					String message = StringUtility.format("仓储[{}]的索引不是唯一.", clazz);
					logger.error(message);
					throw new StorageException(message);
				}
				assessors.put(accessor.getName(), accessor);
			}
			for (Method method : methods) {
				StorageIndex index = method.getAnnotation(StorageIndex.class);
				Comparator comparator = null;
				if (!index.comparator().isInterface()) {
					comparator = index.comparator().newInstance();
				}
				PropertyAccessor accessor = new MethodAccessor(method, index.name(), index.unique(), comparator);
				if (assessors.containsKey(accessor.getName())) {
					String message = StringUtility.format("仓储[{}]的索引不是唯一.", clazz);
					logger.error(message);
					throw new StorageException(message);
				}
				assessors.put(accessor.getName(), accessor);
			}
		} catch (Exception exception) {
			String message = StringUtility.format("仓储[{}]的索引比较器无法实例化.", clazz);
			logger.error(message);
			throw new StorageException(message);
		}
		return assessors;
	}

}
