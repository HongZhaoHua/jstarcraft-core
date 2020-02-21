package com.jstarcraft.core.storage.mybatis;

import java.io.Serializable;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.Version;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jstarcraft.core.common.identification.IdentityObject;
import com.jstarcraft.core.common.reflection.ReflectionUtility;
import com.jstarcraft.core.storage.StorageMetadata;
import com.jstarcraft.core.utility.ClassUtility;
import com.jstarcraft.core.utility.StringUtility;

/**
 * Mybatis元信息
 * 
 * @author Birdy
 */
@SuppressWarnings("rawtypes")
public class MyBatisMetadata implements StorageMetadata {

    /** 实体名称 */
    private String ormName;
    /** 实体类型 */
    private Class ormClass;
    /** 主键名称 */
    private String primaryName;
    /** 主键类型 */
    private Class primaryClass;
    /** 字段映射(名称-类型) */
    private Map<String, Class<?>> fields = new HashMap<>();
    /** 版本号域名 */
    private String versionName;
    /** 映射类型 */
    private Class<? extends BaseMapper<?>> mapperClass;
    /** 纵列名称 */
    private Map<String, String> columnNames = new HashMap<>();

    /**
     * 构造方法
     * 
     * @param metadata
     */
    MyBatisMetadata(Class<? extends BaseMapper<?>> clazz) {
        ormClass = Class.class.cast(ParameterizedType.class.cast(clazz.getGenericInterfaces()[0]).getActualTypeArguments()[0]);
        ormName = ormClass.getName();
        ReflectionUtility.doWithFields(ormClass, (field) -> {
            if (Modifier.isStatic(field.getModifiers()) || Modifier.isTransient(field.getModifiers())) {
                return;
            }
            TableField annotation = field.getAnnotation(TableField.class);
            if (annotation != null) {
                if (!annotation.exist()) {
                    return;
                }
                String columnName = annotation.value();
                if (!StringUtility.isEmpty(columnName)) {
                    columnNames.put(field.getName(), columnName);
                }
            }
            if (field.isAnnotationPresent(Version.class)) {
                versionName = field.getName();
                return;
            }
            Class<?> type = ClassUtility.primitiveToWrapper(field.getType());
            if (String.class == type) {
                fields.put(field.getName(), type);
            } else if (type.isEnum()) {
                fields.put(field.getName(), type);
            } else if (Collection.class.isAssignableFrom(type) || type.isArray()) {
                fields.put(field.getName(), List.class);
            } else if (Date.class.isAssignableFrom(type)) {
                fields.put(field.getName(), Date.class);
            } else {
                fields.put(field.getName(), Map.class);
            }
            if (field.isAnnotationPresent(TableId.class)) {
                primaryName = field.getName();
                primaryClass = type;
            }
        });
        mapperClass = clazz;
    }

    @Override
    public String getOrmName() {
        return ormName;
    }

    @Override
    public Map<String, Class<?>> getFields() {
        return fields;
    }

    @Override
    public String getPrimaryName() {
        return primaryName;
    }

    @Override
    public Collection<String> getIndexNames() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getVersionName() {
        return versionName;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <K extends Serializable> Class<K> getPrimaryClass() {
        return primaryClass;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends IdentityObject> Class<T> getOrmClass() {
        return ormClass;
    }

    public Class<? extends BaseMapper<?>> getMapperClass() {
        return mapperClass;
    }

    public String getColumnName(String name) {
        String column = columnNames.get(name);
        // 将驼峰转蛇形
        return column == null ? camel2Snake(name) : column;
    }

    /**
     * 是否需要下划线
     * 
     * @param before
     * @param current
     * @param after
     * @return
     */
    private boolean isUnderscoreRequired(char before, char current, char after) {
        return Character.isLowerCase(before) && Character.isUpperCase(current) && Character.isLowerCase(after);
    }

    private String camel2Snake(String name) {
        StringBuilder builder = new StringBuilder(name.replace(StringUtility.PERIOD, StringUtility.UNDERSCORE));
        for (int index = 1; index < builder.length() - 1; index++) {
            if (isUnderscoreRequired(builder.charAt(index - 1), builder.charAt(index), builder.charAt(index + 1))) {
                builder.insert(index++, StringUtility.UNDERSCORE);
            }
        }
        return builder.toString().toLowerCase(Locale.ROOT);
    }

}
