package com.jstarcraft.core.common.conversion.json;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.databind.JavaType;
import com.jstarcraft.core.common.reflection.TypeUtility;

public class JsonUtilityTestCase {

    @Test
    public void testBindModule() {
        JsonUtility.registerModule(UnitNumber.class, new UnitNumberDeserializer(), new UnitNumberSerializer());
        UnitNumber left = new UnitNumber(1, 1D);
        String json = JsonUtility.object2String(left);
        UnitNumber right = JsonUtility.string2Object(json, UnitNumber.class);
        Assert.assertThat(json, CoreMatchers.equalTo("[1.0,1]"));
        Assert.assertThat(left, CoreMatchers.equalTo(right));
    }

    private void convertType(Type type) {
        JavaType java = JsonUtility.type2Java(type);
        Assert.assertEquals(type, JsonUtility.java2Type(java));
    }

    @Test
    public void testConvertType() {
        // 基于原始与包装类型测试
        convertType(AtomicBoolean.class);
        convertType(Boolean.class);
        convertType(boolean.class);

        // 基于对象类型测试
        convertType(MockComplexObject.class);
        convertType(MockMatrix.class);

        // 基于枚举类型测试
        convertType(MockEnumeration.class);

        // 基于数组类型测试
        convertType(MockEnumeration[].class);
        convertType(Integer[].class);
        convertType(int[].class);
        convertType(MockComplexObject[].class);
        convertType(Byte[].class);
        convertType(byte[].class);

        // 基于集合类型测试
        convertType(TypeUtility.parameterize(List.class, MockEnumeration.class));
        convertType(TypeUtility.parameterize(Set.class, MockEnumeration.class));

        convertType(TypeUtility.parameterize(List.class, Integer.class));
        convertType(TypeUtility.parameterize(Set.class, Integer.class));

        convertType(TypeUtility.parameterize(List.class, MockComplexObject.class));
        convertType(TypeUtility.parameterize(Set.class, MockComplexObject.class));

        convertType(TypeUtility.parameterize(List.class, MockComplexObject[].class));
        convertType(TypeUtility.parameterize(Set.class, MockComplexObject[].class));

        // 基于映射类型测试
        convertType(TypeUtility.parameterize(Map.class, String.class, MockComplexObject.class));

        // 基于泛型类型测试
        Type type = TypeUtility.genericArrayType(TypeUtility.parameterize(Map.class, String.class, MockComplexObject.class));
        convertType(type);
        type = TypeUtility.genericArrayType(TypeUtility.parameterize(Map.class, String.class, type));
        convertType(type);
    }

}
