package com.jstarcraft.core.common.reflection;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Assert;
import org.junit.Test;

import com.jstarcraft.core.common.conversion.json.MockComplexObject;
import com.jstarcraft.core.common.conversion.json.MockEnumeration;
import com.jstarcraft.core.common.conversion.json.MockMatrix;
import com.jstarcraft.core.utility.KeyValue;

import it.unimi.dsi.fastutil.bytes.Byte2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.bytes.ByteArraySet;
import it.unimi.dsi.fastutil.objects.Object2ByteOpenHashMap;

public class TypeUtilityTestCase {

    @Test
    public void testRefineType() {
        {
            Type type = TypeUtility.parameterize(HashMap.class, String.class, String.class);
            Assert.assertEquals(TypeUtility.parameterize(Map.class, String.class, String.class), TypeUtility.refineType(type, Map.class));
        }
        {
            Type type = TypeUtility.parameterize(Byte2ObjectOpenHashMap.class, String.class);
            Assert.assertEquals(TypeUtility.parameterize(Map.class, Byte.class, String.class), TypeUtility.refineType(type, Map.class));
        }
        {
            Type type = TypeUtility.parameterize(Object2ByteOpenHashMap.class, String.class);
            Assert.assertEquals(TypeUtility.parameterize(Map.class, String.class, Byte.class), TypeUtility.refineType(type, Map.class));
        }
        {
            Type type = TypeUtility.refineType(Byte2BooleanOpenHashMap.class, Map.class);
            Assert.assertEquals(TypeUtility.parameterize(Map.class, Byte.class, Boolean.class), TypeUtility.refineType(type, Map.class));
        }
        {
            Type type = TypeUtility.refineType(ByteArrayList.class, List.class);
            Assert.assertEquals(TypeUtility.parameterize(List.class, Byte.class), TypeUtility.refineType(type, List.class));
        }
        {
            Type type = TypeUtility.refineType(ByteArraySet.class, Set.class);
            Assert.assertEquals(TypeUtility.parameterize(Set.class, Byte.class), TypeUtility.refineType(type, Set.class));
        }
    }

    @Test
    public void testTypeVariable() {
        TypeVariable<?>[] typeVariables = KeyValue.class.getTypeParameters();
        TypeVariable<?> keyVariable = TypeUtility.typeVariable(KeyValue.class, "K");
        Assert.assertEquals(typeVariables[0].getGenericDeclaration(), keyVariable.getGenericDeclaration());
        Assert.assertEquals(typeVariables[0].getTypeName(), keyVariable.getTypeName());
        TypeVariable<?> valueVariable = TypeUtility.typeVariable(KeyValue.class, "V");
        Assert.assertEquals(typeVariables[1].getGenericDeclaration(), valueVariable.getGenericDeclaration());
        Assert.assertEquals(typeVariables[1].getTypeName(), valueVariable.getTypeName());
    }

    private void convertType(Type type) {
        String java = TypeUtility.type2String(type);
        Assert.assertEquals(type, TypeUtility.string2Type(java));
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

    private void convertString(String java) {
        Type type = TypeUtility.string2Type(java);
        Assert.assertEquals(java, TypeUtility.type2String(type));
    }

    @Test
    public void testConvertString() {
        convertString("com.jstarcraft.core.utility.KeyValue<java.lang.Object, T extends java.lang.Comparable<? super java.lang.Object[]>><><>");

        convertString("com.jstarcraft.core.utility.KeyValue<K extends java.land.Boolean, T extends com.jstarcraft.core.utility.KeyValue<K, T>>");
    }

}
