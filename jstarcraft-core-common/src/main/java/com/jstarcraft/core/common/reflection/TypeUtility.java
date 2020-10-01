package com.jstarcraft.core.common.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;

import org.apache.commons.lang3.reflect.TypeUtils;

/**
 * 类型工具
 * 
 * @author Birdy
 *
 */
public class TypeUtility extends TypeUtils {

    private final static Type[] emptyTypes = new Type[0];

    public static Type refineType(Type fromType, Class<?> toType, Type... context) {
        if (fromType == null) {
            return null;
        }
        Type[] types = emptyTypes;
        if (fromType instanceof ParameterizedType && TypeUtility.isAssignable(fromType, toType)) {
            ParameterizedType parameterizedType = ParameterizedType.class.cast(fromType);
            types = parameterizedType.getActualTypeArguments();
            int cursor = 0;
            for (int index = 0, size = types.length; index < size; index++) {
                Type type = types[index];
                if (type instanceof TypeVariable || type instanceof WildcardType) {
                    if (context.length > cursor) {
                        types[index] = context[cursor++];
                    }
                }
            }
            if (types.length == toType.getTypeParameters().length) {
                return TypeUtility.parameterize(toType, types);
            }
        }
        Class clazz = TypeUtility.getRawType(fromType, null);
        Type[] interfaceTypes = clazz.getGenericInterfaces();
        for (Type interfaceType : interfaceTypes) {
            Type type = refineType(interfaceType, toType, types);
            if (type != null) {
                return type;
            }
        }
        return refineType(clazz.getGenericSuperclass(), toType, types);
    }

    private static final class TypeVariableImpl<D extends GenericDeclaration> implements TypeVariable<D> {

        private final D declaration;

        private final String name;

        private final Type[] bounds;

        private TypeVariableImpl(D declaration, String name, Type[] bounds) {
            this.declaration = declaration;
            this.name = name;
            this.bounds = bounds;
        }

        @Override
        public Type[] getBounds() {
            return bounds;
        }

        @Override
        public D getGenericDeclaration() {
            return declaration;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public <T extends Annotation> T getAnnotation(Class<T> clazz) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Annotation[] getAnnotations() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Annotation[] getDeclaredAnnotations() {
            throw new UnsupportedOperationException();
        }

        @Override
        public AnnotatedType[] getAnnotatedBounds() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean equals(Object object) {
            if (object instanceof TypeVariable) {
                TypeVariable<?> that = (TypeVariable<?>) object;
                return this.name.equals(that.getName());
            }
            return false;
        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }

        @Override
        public String toString() {
            return name;
        }

    }

    public static <D extends GenericDeclaration> TypeVariable<D> typeVariable(D declaration, String name, Type... bounds) {
        return new TypeVariableImpl<>(declaration, name, bounds);
    }

}
