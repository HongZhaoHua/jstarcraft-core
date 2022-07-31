package com.jstarcraft.core.storage.berkeley.migration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.jstarcraft.core.utility.ClassUtility;
import com.sleepycat.persist.model.EntityModel;
import com.sleepycat.persist.model.Persistent;
import com.sleepycat.persist.model.PersistentProxy;
import com.sleepycat.persist.raw.RawField;
import com.sleepycat.persist.raw.RawObject;
import com.sleepycat.persist.raw.RawStore;
import com.sleepycat.persist.raw.RawType;

/**
 * Berkeley转换器
 * 
 * @author Birdy
 *
 */
public class BerkeleyConverter implements MigrationConverter {

    private final static Map<String, Object> primitiveMap = new HashMap<String, Object>();
    static {
        primitiveMap.put(Boolean.TYPE.getName(), false);
        primitiveMap.put(Byte.TYPE.getName(), 0);
        primitiveMap.put(Character.TYPE.getName(), '\u0000');
        primitiveMap.put(Double.TYPE.getName(), 0D);
        primitiveMap.put(Float.TYPE.getName(), 0F);
        primitiveMap.put(Integer.TYPE.getName(), 0);
        primitiveMap.put(Long.TYPE.getName(), 0L);
        primitiveMap.put(Short.TYPE.getName(), 0);
        primitiveMap.put(Void.TYPE.getName(), null);
    }

    // TODO 多个NewName对应一个OldName时,名称如何转换?
    public static String fromOldToNewName(MigrationContext context, String className) {
        for (Entry<String, String> keyValue : context.getClassNameMap().entrySet()) {
            if (keyValue.getValue().equals(className)) {
                return keyValue.getKey();
            }
        }
        return className;
    }

    public static String fromNewToOldName(MigrationContext context, String className) {
        final String oldName = context.getClassNameMap().get(className);
        return oldName == null ? className : oldName;
    }

    /**
     * 处理字段的类型转换,继承者通常会重写的方法
     * 
     * @param fieldName    新原始对象的字段名称
     * @param fieldType    新原始对象的字段类型
     * @param fieldValue   旧原始对象的字段值
     * @param oldRawObject 旧原始对象实例
     * @param newRawObject 新原始对象实例
     * @param context      迁移上下文对象
     * @param oldRawStore  旧仓储
     * @param newRawStore  新仓储
     * @return 处理以后的字段值
     * @throws Exception
     */
    protected Object handleField(String fieldName, RawType fieldType, Object fieldValue, RawObject oldRawObject, RawObject newRawObject, MigrationContext context, RawStore oldRawStore, RawStore newRawStore) throws Exception {
        try {
            if (fieldValue == null) {
                // 失效类型
                if (fieldType.isPrimitive()) {
                    return primitiveMap.get(fieldType.getClassName());
                }
                return fieldValue;
            }

            final EntityModel oldModel = oldRawStore.getModel();
            final EntityModel newModel = newRawStore.getModel();

            // 判断新原始对象的字段声明类型 与 旧原始对象的字段值类型是否一致
            // 考虑内建类型的情况
            final RawType valueType = (fieldValue instanceof RawObject) ? RawObject.class.cast(fieldValue).getType() : oldModel.getRawType(fieldValue.getClass().getName());
            final Class<?> fieldTypeClass = ClassUtility.primitiveToWrapper(ClassUtility.getClass(fieldType.getClassName()));
            final Class<?> valueTypeClass = ClassUtility.primitiveToWrapper(ClassUtility.getClass(valueType.getClassName()));
            // 考虑代理类的情况
            final Class<?> proxyTypeClass = PersistentProxy.class.isAssignableFrom(valueTypeClass) ? valueTypeClass.getAnnotation(Persistent.class).proxyFor() : valueTypeClass;
            if (!fieldTypeClass.isAssignableFrom(proxyTypeClass)) {
                throw new RuntimeException(fieldName + "新原始对象的字段声明类型[" + fieldTypeClass.getName() + "]与旧原始对象的字段值类型[" + valueTypeClass.getName() + "]不一致且无法转换");
            }

            if (valueType.isSimple()) {
                // 简单类型
                return fieldValue;
            } else if (valueType.isEnum()) {
                // 枚举类型
                final RawObject oldEnum = RawObject.class.cast(fieldValue);
                final RawObject newEnum = new RawObject(newRawStore.getModel().getRawType(fieldType.getClassName()), oldEnum.getEnum());
                return newEnum;
            } else if (valueType.isArray()) {
                // 数组类型
                final RawObject oldArray = RawObject.class.cast(fieldValue);
                final RawObject newArray = this.copyArray(context, oldRawStore, newRawStore, oldArray);
                return newArray;
            } else {
                final RawObject oldObject = RawObject.class.cast(fieldValue);
                final RawObject newObject;
                if (valueType.getSuperType() != null) {
                    // 父类型
                    final RawObject oldSuper = oldObject.getSuper();
                    final RawObject newSuper = new RawObject(newModel.getRawType(fromOldToNewName(context, oldSuper.getType().getClassName())), new HashMap<String, Object>(), null);
                    this.convert(context, oldRawStore, newRawStore, oldSuper, newSuper);
                    newObject = new RawObject(newModel.getRawType(fromOldToNewName(context, valueType.getClassName())), new HashMap<String, Object>(), newSuper);
                } else {
                    // 复合类型
                    newObject = new RawObject(newModel.getRawType(fromOldToNewName(context, valueType.getClassName())), new HashMap<String, Object>(), null);
                }
                this.convert(context, oldRawStore, newRawStore, oldObject, newObject);
                return newObject;
            }
        } catch (Exception exception) {
            throw exception;
        }
    }

    @Override
    public boolean convert(MigrationContext context, RawStore oldRawStore, RawStore newRawStore, RawObject oldObject, RawObject newObject) throws Exception {
        final Map<String, RawField> rawFieldMap = new HashMap<String, RawField>();
        final RawType oldRawType = oldObject.getType();
        final Map<String, RawField> oldRawFieldMap = oldRawType.getFields();
        final RawType newRawType = newObject.getType();
        final Map<String, RawField> newRawFieldMap = newRawType.getFields();
        if (oldRawFieldMap != null) {
            rawFieldMap.putAll(oldRawFieldMap);
        }
        if (newRawFieldMap != null) {
            rawFieldMap.putAll(newRawFieldMap);
        }

        final EntityModel newModel = newRawStore.getModel();
        for (Entry<String, RawField> keyValue : rawFieldMap.entrySet()) {
            final String fieldName = keyValue.getKey();
            // RawField可能来自新RawObject或者旧RawObject,所以必须重新获取RawType
            final RawType fieldType = newModel.getRawType(keyValue.getValue().getType().getClassName());
            final Object fieldValue = oldObject.getValues().get(fieldName);
            newObject.getValues().put(fieldName, this.handleField(fieldName, fieldType, fieldValue, oldObject, newObject, context, oldRawStore, newRawStore));
        }

        if (newRawType.getSuperType() != null) {
            final RawObject oldSuper = oldObject.getSuper();
            final RawObject newSuper = newObject.getSuper();
            // TODO 此处可能需要验证oldSuper与newSuper的类型是否一致
            convert(context, oldRawStore, newRawStore, oldSuper, newSuper);
        }
        return true;
    }

    protected RawObject copyArray(MigrationContext context, RawStore oldRawStore, RawStore newRawStore, RawObject oldArray) throws Exception {
        final List<Object> array = new ArrayList<Object>();
        final EntityModel newModel = newRawStore.getModel();

        for (Object element : oldArray.getElements()) {
            array.add(this.copyElement(context, oldRawStore, newRawStore, element));
        }
        return new RawObject(newModel.getRawType(fromOldToNewName(context, oldArray.getType().getClassName())), array.toArray());
    }

    protected Object copyElement(MigrationContext context, RawStore oldRawStore, RawStore newRawStore, Object element) throws Exception {
        final EntityModel newModel = newRawStore.getModel();

        if (!(element instanceof RawObject)) {
            return element;
        }

        final RawObject elementObject = RawObject.class.cast(element);
        final RawType elementType = elementObject.getType();

        if (elementType.isSimple()) {
            return elementObject;
        } else if (elementType.isEnum()) {
            final RawObject oldEnum = elementObject;
            final RawObject newEnum = new RawObject(newModel.getRawType(fromOldToNewName(context, elementType.getClassName())), oldEnum.getEnum());
            return newEnum;
        } else if (elementType.isArray()) {
            return copyArray(context, oldRawStore, newRawStore, elementObject);
        } else {
            try {
                final RawObject oldObject = elementObject;
                final RawObject newObject;
                if (elementType.getSuperType() != null) {
                    // 父类型
                    final RawObject oldSuper = oldObject.getSuper();
                    final RawObject newSuper = new RawObject(newModel.getRawType(fromOldToNewName(context, oldSuper.getType().getClassName())), new HashMap<String, Object>(), null);
                    this.convert(context, oldRawStore, newRawStore, oldSuper, newSuper);
                    newObject = new RawObject(newModel.getRawType(fromOldToNewName(context, elementType.getClassName())), new HashMap<String, Object>(), newSuper);
                } else {
                    // 复合类型
                    newObject = new RawObject(newModel.getRawType(fromOldToNewName(context, elementType.getClassName())), new HashMap<String, Object>(), null);
                }
                this.convert(context, oldRawStore, newRawStore, oldObject, newObject);
                return newObject;
            } catch (Exception exception) {
                throw exception;
            }
        }
    }

}
