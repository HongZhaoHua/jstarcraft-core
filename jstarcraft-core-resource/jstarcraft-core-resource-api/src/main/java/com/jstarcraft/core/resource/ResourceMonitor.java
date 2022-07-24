package com.jstarcraft.core.resource;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jstarcraft.core.common.reflection.ReflectionUtility;
import com.jstarcraft.core.resource.annotation.ResourceAccessor;
import com.jstarcraft.core.resource.exception.StorageException;
import com.jstarcraft.core.utility.StringUtility;

/**
 * 资源监控器
 * 
 * @author Birdy
 */
public class ResourceMonitor implements Observer {

    private final static Logger logger = LoggerFactory.getLogger(ResourceMonitor.class);

    /** 访问对象 */
    private final Object object;
    /** 访问字段 */
    private final Field field;

    /** 资源类型 */
    private final Class<?> clazz;
    /** 资源主键 */
    private final Object key;

    /** 是否必须 */
    private final boolean necessary;
    /** 属性配置 */
    private PropertyDescriptor property;

    public ResourceMonitor(ResourceAccessor accessor, Object object, Field field, Class<?> clazz, Object key) {
        this.object = object;
        this.field = field;
        this.clazz = clazz;
        this.key = key;
        this.necessary = accessor.necessary();
        if (StringUtility.isNotBlank(accessor.property())) {
            Map<String, PropertyDescriptor> properties = ReflectionUtility.getPropertyDescriptors(clazz);
            for (PropertyDescriptor property : properties.values()) {
                if (property.getName().equals(accessor.property())) {
                    this.property = property;
                    break;
                }
            }
            if (this.property == null) {
                String message = StringUtility.format("仓储[{}]的属性[{}]不存在", clazz, property);
                logger.error(message);
                throw new StorageException(message);
            }
        } else {
            this.property = null;
        }
    }

    private void updateInstance(Object value) throws Exception {
        field.setAccessible(true);
        field.set(object, value);
    }

    private void updateStorage(ResourceManager storage) throws Exception {
        Object instance = storage.getInstance(key, false);
        if (necessary && instance == null) {
            String message = StringUtility.format("仓储[{}]不存在指定的实例[{}]", clazz, key);
            logger.error(message);
            throw new StorageException(message);
        }

        Class<?> fieldClass = field.getType();
        if (instance == null) {
            // 不更新
        } else if (fieldClass.isInstance(instance)) {
            // 更新对象
            updateInstance(instance);
        } else {
            // 更新属性
            Method method = this.property.getReadMethod();
            if (method != null) {
                ReflectionUtility.makeAccessible(method);
                instance = method.invoke(instance);
            } else {
                Field field = clazz.getField(this.property.getName());
                ReflectionUtility.makeAccessible(field);
                instance = field.get(instance);
            }
            updateInstance(instance);
        }
    }

    /** 更新通知 */
    @Override
    public void update(Observable manager, Object argument) {
        try {
            updateStorage(ResourceManager.class.cast(manager));
        } catch (Exception exception) {
            String message = StringUtility.format("仓储[{}]更新异常", clazz);
            logger.error(message);
            throw new StorageException(message, exception);
        }
    }

}
