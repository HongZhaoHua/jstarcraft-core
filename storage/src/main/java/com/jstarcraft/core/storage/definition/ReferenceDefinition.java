package com.jstarcraft.core.storage.definition;

import java.lang.reflect.Field;
import java.util.Observer;

import com.jstarcraft.core.storage.annotation.StorageReference;

/**
 * 引用定义
 * 
 * @author Birdy
 */
public abstract class ReferenceDefinition implements Observer {

	/** 引用字段 */
	protected final Field field;
	/** 引用注解 */
	protected final StorageReference reference;

	ReferenceDefinition(Field field) {
		if (field == null) {
			throw new IllegalArgumentException("引用定义字段不能为null");
		}
		StorageReference reference = field.getAnnotation(StorageReference.class);
		if (reference == null) {
			throw new IllegalArgumentException("引用定义注解不能为null");
		}
		field.setAccessible(true);
		this.field = field;
		this.reference = reference;
	}

	/**
	 * 将引用设置到指定的实例
	 * 
	 * @param instance
	 */
	abstract public void setReference(Object instance);

	/**
	 * 获取监控的仓储类型
	 * 
	 * @return
	 */
	abstract public Class getMonitorStorage();

}
