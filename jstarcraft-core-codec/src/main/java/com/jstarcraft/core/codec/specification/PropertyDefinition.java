package com.jstarcraft.core.codec.specification;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.jstarcraft.core.common.reflection.Specification;

/**
 * 属性定义
 * 
 * @author Birdy
 */
public class PropertyDefinition implements Comparable<PropertyDefinition> {

    private String name;
    private int code;
    private Type type;
    private Field field;
    private Method getter;
    private Method setter;
    private Specification specification;

    private PropertyDefinition() {
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public Specification getSpecification() {
        return specification;
    }

    public Object getValue(Object object) throws Exception {
        Object value = null;
        if (getter != null) {
            value = getter.invoke(object);
        } else if (field != null) {
            value = field.get(object);
        }
        return value;
    }

    public void setValue(Object object, Object value) throws Exception {
        if (setter != null) {
            setter.invoke(object, value);
        } else if (field != null) {
            field.set(object, value);
        }
    }

    @Override
    public int compareTo(PropertyDefinition that) {
        CompareToBuilder comparator = new CompareToBuilder();
        comparator.append(this.name, that.name);
        return comparator.toComparison();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object)
            return true;
        if (object == null)
            return false;
        if (getClass() != object.getClass())
            return false;
        PropertyDefinition that = (PropertyDefinition) object;
        EqualsBuilder equal = new EqualsBuilder();
        equal.append(this.name, that.name);
        equal.append(this.type, that.type);
        return equal.isEquals();
    }

    @Override
    public int hashCode() {
        HashCodeBuilder hash = new HashCodeBuilder();
        hash.append(name);
        return hash.toHashCode();
    }

    @Override
    public String toString() {
        ToStringBuilder string = new ToStringBuilder(this);
        string.append(name);
        return string.toString();
    }

    public static PropertyDefinition instanceOf(String name, int code, Type type, Method getter, Method setter) {
        PropertyDefinition definition = new PropertyDefinition();
        definition.name = name;
        definition.code = code;
        definition.type = type;
        definition.specification = Specification.getSpecification(type);
        definition.getter = getter;
        definition.setter = setter;
        if (definition.getter != null) {
            definition.getter.setAccessible(true);
        }
        if (definition.setter != null) {
            definition.setter.setAccessible(true);
        }
        return definition;
    }

    public static PropertyDefinition instanceOf(String name, int code, Type type, Field field) {
        PropertyDefinition definition = new PropertyDefinition();
        definition.name = name;
        definition.code = code;
        definition.type = type;
        definition.specification = Specification.getSpecification(type);
        definition.field = field;
        if (definition.field != null) {
            definition.field.setAccessible(true);
        }
        return definition;
    }

}
