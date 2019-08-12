package com.jstarcraft.core.resource.definition;

import java.lang.reflect.Field;
import java.util.Observable;

import org.springframework.beans.factory.BeanFactory;

import com.jstarcraft.core.common.reflection.ReflectionUtility;
import com.jstarcraft.core.resource.ResourceStorage;
import com.jstarcraft.core.resource.exception.StorageException;
import com.jstarcraft.core.script.ScriptContext;
import com.jstarcraft.core.script.ScriptExpression;
import com.jstarcraft.core.script.ScriptScope;
import com.jstarcraft.core.utility.StringUtility;

/**
 * Spring引用定义
 * 
 * @author Birdy
 */
public class SpringReferenceDefinition extends ReferenceDefinition {

	private final Class clazz;

	private final BeanFactory factory;

	public SpringReferenceDefinition(Field field, BeanFactory factory) {
		super(field);
		this.clazz = field.getDeclaringClass();
		this.factory = factory;
	}

	@Override
	public void setReference(Object instance) {
		Object value;
		if (StringUtility.isBlank(reference.expression())) {
			value = factory.getBean(field.getType());
		} else {
			ScriptContext context = new ScriptContext();
			ScriptScope scope = new ScriptScope();
			scope.createAttribute("instance", instance);
			ScriptExpression expression = ReflectionUtility.getInstance(reference.type(), context, scope, reference.expression());
			value = factory.getBean(expression.doWith(String.class));
		}
		if (value == null && reference.necessary()) {
			throw new StorageException("引用定义对象不能为null");
		}
		try {
			field.set(instance, value);
		} catch (Exception exception) {
			throw new StorageException("引用定义设置对象异常", exception);
		}
	}

	@Override
	public Class getMonitorStorage() {
		return clazz;
	}

	/** 更新通知 */
	@Override
	public void update(Observable object, Object argument) {
		ResourceStorage storage = ResourceStorage.class.cast(object);
		for (Object instance : storage.getAll()) {
			setReference(instance);
		}
	}

}
