package com.jstarcraft.core.storage.hibernate;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.Column;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.type.TextType;
import org.hibernate.usertype.UserType;

import com.jstarcraft.core.common.conversion.json.JsonUtility;
import com.jstarcraft.core.common.reflection.ReflectionUtility;
import com.jstarcraft.core.storage.exception.StorageAccessException;
import com.jstarcraft.core.utility.StringUtility;

/**
 * JSON格式
 * 
 * @author Birdy
 *
 */
public class JsonType implements UserType {

    public final static String CLASS_NAME = "com.jstarcraft.core.storage.hibernate.JsonType";

    private static Map<Class<?>, Map<String, String>> CLAZZ_2_COLUMN = new ConcurrentHashMap<>();

    @Override
    public int[] sqlTypes() {
        return new int[] { TextType.INSTANCE.sqlType() };
    }

    @Override
    public Class<Object> returnedClass() {
        return Object.class;
    }

    @Override
    public boolean equals(Object left, Object right) throws HibernateException {
        if (left == right) {
            return true;
        }
        if (left == null || left == null) {
            return false;
        }
        return left.equals(right);
    }

    @Override
    public int hashCode(Object object) throws HibernateException {
        return object.hashCode();
    }

    @Override
    public Object nullSafeGet(ResultSet resultSet, String[] names, SharedSessionContractImplementor session, Object object) throws HibernateException, SQLException {
        if (object == null) {
            return null;
        }
        String json = resultSet.getString(names[0]);
        String columnName = getColumnName(resultSet, names[0]);
        String fieldName = getFieldName(object.getClass(), columnName);
        Type type = null;
        try {
            type = ReflectionUtility.findField(object.getClass(), fieldName).getGenericType();
        } catch (Exception exception) {
            throw new StorageAccessException(exception);
        }
        Object value = JsonUtility.string2Object(json, type);
        return value;
    }

    @Override
    public void nullSafeSet(PreparedStatement preparedStatement, Object value, int index, SharedSessionContractImplementor session) throws HibernateException, SQLException {
        if (value != null) {
            String json;
            synchronized (value) {
                json = value instanceof String ? (String) value : JsonUtility.object2String(value);
            }
            preparedStatement.setString(index, json);
        } else {
            preparedStatement.setNull(index, TextType.INSTANCE.sqlType());
        }
    }

    @Override
    public Object deepCopy(Object value) throws HibernateException {
        return value;
    }

    @Override
    public boolean isMutable() {
        return true;
    }

    @Override
    public Serializable disassemble(Object value) throws HibernateException {
        return ((Serializable) value);
    }

    @Override
    public Object assemble(Serializable serializable, Object object) throws HibernateException {
        return serializable;
    }

    @Override
    public Object replace(Object original, Object target, Object object) throws HibernateException {
        return original;
    }

    private static String getFieldName(Class<?> clazz, String columnName) {
        Map<String, String> column2Field = CLAZZ_2_COLUMN.get(clazz);
        synchronized (CLAZZ_2_COLUMN) {
            if (column2Field == null) {
                column2Field = new ConcurrentHashMap<>();
                CLAZZ_2_COLUMN.put(clazz, column2Field);
            }
        }
        String fieldName = column2Field.get(columnName);
        if (fieldName != null) {
            return fieldName;
        }
        synchronized (column2Field) {
            while (clazz != null) {
                Field[] fields = clazz.getDeclaredFields();
                for (Field field : fields) {
                    org.hibernate.annotations.Type typeAnnotation = field.getDeclaredAnnotation(org.hibernate.annotations.Type.class);
                    if (typeAnnotation != null && typeAnnotation.type().equals(JsonType.class.getName())) {
                        if (field.getName().equalsIgnoreCase(columnName)) {
                            fieldName = field.getName();
                            column2Field.put(columnName, fieldName);
                            return fieldName;
                        } else {
                            Column columnAnnotation = field.getDeclaredAnnotation(Column.class);
                            if (columnAnnotation != null && columnAnnotation.name().equalsIgnoreCase(columnName)) {
                                fieldName = field.getName();
                                column2Field.put(columnName, fieldName);
                                return fieldName;
                            }
                        }
                    }
                }
                clazz = clazz.getSuperclass();
            }
        }
        String message = StringUtility.format("数据列{}对应字段的不存在", columnName);
        throw new StorageAccessException(message);
    }

    private static String getColumnName(ResultSet resultSet, String fieldName) throws SQLException {
        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
        int count = resultSetMetaData.getColumnCount();
        for (int index = 1; index <= count; index++) {
            String columnLabel = resultSetMetaData.getColumnLabel(index);
            String columnName = resultSetMetaData.getColumnName(index);
            if (columnLabel.equalsIgnoreCase(fieldName)) {
                return columnName;
            }
        }
        String message = StringUtility.format("字段{}对应的数据列不存在", fieldName);
        throw new StorageAccessException(message);
    }

}
