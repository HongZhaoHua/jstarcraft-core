package com.jstarcraft.core.storage.elasticsearch.converter;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.core.convert.converter.Converter;

import com.jstarcraft.core.storage.exception.StorageException;
import com.jstarcraft.core.utility.StringUtility;

import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.bytecode.ConstPool;
import javassist.bytecode.Descriptor;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.SignatureAttribute.ClassSignature;
import javassist.bytecode.SignatureAttribute.ClassType;
import javassist.bytecode.SignatureAttribute.TypeArgument;

/**
 * 代理转换器
 * 
 * @author Birdy
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class DecodeJsonProxy {

    /** 类:代理后缀 */
    protected final static String CLASS_SUFFIX = "_DECODE_CONVERTER";

    /** 字段:引用代理缓存管理 */
    protected final static String FIELD_CLAZZ = "_clazz";

    /** 方法:获取缓存标识 */
    protected final static String METHOD_CONVERT = "convert";

    private final static HashMap<String, Class<?>[]> DEFAULT_METHODS = new HashMap<>();

    protected static final ClassPool classPool = ClassPool.getDefault();

    static {
        for (Method method : Object.class.getDeclaredMethods()) {
            DEFAULT_METHODS.put(method.getName(), method.getParameterTypes());
        }
        for (Method method : Comparable.class.getDeclaredMethods()) {
            DEFAULT_METHODS.put(method.getName(), method.getParameterTypes());
        }

        classPool.insertClassPath(new ClassClassPath(AbstractConverter.class));
    }

    protected ConcurrentHashMap<Class<?>, Constructor<?>> constructors = new ConcurrentHashMap<>();

    public <T> Converter<String, T> getToConverter(Class<T> clazz) {
        try {
            Constructor constructor = getConverterConstructor(clazz);
            Converter<String, T> converter = (Converter<String, T>) constructor.newInstance(clazz);
            return converter;
        } catch (Exception exception) {
            String message = StringUtility.format("指定类[{}]代理异常", clazz.getName());
            throw new StorageException(message, exception);
        }
    }

    /**
     * 获取构造器
     * 
     * @param clazz
     * @return
     * @throws Exception
     */
    private <T> Constructor<T> getConverterConstructor(Class<T> clazz) throws Exception {
        if (constructors.containsKey(clazz)) {
            return (Constructor<T>) constructors.get(clazz);
        }
        synchronized (clazz) {
            if (constructors.containsKey(clazz)) {
                return (Constructor<T>) constructors.get(clazz);
            }
            Class current = buildeConverterClass(clazz);
            Constructor<T> constructor = current.getConstructor(Class.class);
            constructors.put(clazz, constructor);
            return constructor;
        }
    }

    /**
     * 构建指定类
     * 
     * @param clazz
     * @return
     * @throws Exception
     */
    private Class<?> buildeConverterClass(final Class<?> clazz) throws Exception {
        CtClass proxyClass = proxyClass(clazz);
        proxyFields(proxyClass);
        proxyConstructor(proxyClass);
        proxyMethods(clazz, proxyClass);
        return proxyClass.toClass();
    }

    /**
     * 代理类
     * 
     * <pre>
     * public class [clazz.name]$ENHANCED extends [clazz.name] implements CacheObject {
     * }
     * </pre>
     * 
     * @param clazz
     * @return
     * @throws Exception
     */
    private CtClass proxyClass(Class<?> clazz) throws Exception {
        classPool.insertClassPath(new ClassClassPath(clazz));
        CtClass source = classPool.get(AbstractConverter.class.getName());
        CtClass result = classPool.makeClass("com.jstarcraft.core.storage.elasticsearch.converter." + clazz.getSimpleName() + CLASS_SUFFIX);
        result.setSuperclass(source);
        ClassSignature signature = new ClassSignature(null,

                new ClassType(AbstractConverter.class.getName(),

                        new TypeArgument[] {

                                new TypeArgument(new ClassType("java.lang.String")),

                                new TypeArgument(new ClassType(clazz.getName())) }),

                new ClassType[] {

                        new ClassType(Converter.class.getName(),

                                new TypeArgument[] {

                                        new TypeArgument(new ClassType("java.lang.String")),

                                        new TypeArgument(new ClassType(clazz.getName())) })

                });
        result.setGenericSignature(signature.encode());
        return result;
    }

    /**
     * 代理构造器
     * 
     * <pre>
     * public [proxyClass.name](Class _clazz) {
     *     this._clazz = _clazz;
     * }
     * </pre>
     * 
     * @param clazz
     * @param proxyClass
     * @throws Exception
     */
    private void proxyConstructor(CtClass proxyClass) throws Exception {
        CtConstructor constructor = new CtConstructor(toProxyClasses(Class.class), proxyClass);
        StringBuilder methodBuilder = new StringBuilder("{");
        methodBuilder.append(StringUtility.format("this.{} = $1;", FIELD_CLAZZ));
        methodBuilder.append("}");
        constructor.setBody(methodBuilder.toString());
        constructor.setModifiers(Modifier.PUBLIC);
        proxyClass.addConstructor(constructor);
    }

    /**
     * 代理字段
     * 
     * <pre>
     * private final Class _clazz;
     * </pre>
     * 
     * @param clazz
     * @param proxyClass
     * @throws Exception
     */
    private void proxyFields(CtClass proxyClass) throws Exception {
        CtField field = new CtField(classPool.get(Class.class.getName()), FIELD_CLAZZ, proxyClass);
        field.setModifiers(Modifier.PRIVATE + Modifier.FINAL + Modifier.TRANSIENT);
        proxyClass.addField(field);
    }

    /**
     * 代理方法
     * 
     * <pre>
     * public Object convert(Object instance) {
     *     return JsonUtility.string2Object(instance, this._clazz);
     * }
     * </pre>
     * 
     * @param clazz
     * @param proxyClass
     * @throws Exception
     */
    private void proxyMethods(Class<?> clazz, CtClass proxyClass) throws Exception {
        // 由于convert方法是泛型,所以入参和出参都要设置为Object.class
        CtClass returnType = classPool.get(Object.class.getName());
        CtClass[] parameters = new CtClass[] { classPool.get(Object.class.getName()) };
        ConstPool constPool = proxyClass.getClassFile2().getConstPool();
        String descriptor = Descriptor.ofMethod(returnType, parameters);
        MethodInfo methodInformation = new MethodInfo(constPool, METHOD_CONVERT, descriptor);
        CtMethod method = CtMethod.make(methodInformation, proxyClass);
        StringBuilder methodBuilder = new StringBuilder("{");
        methodBuilder.append(StringUtility.format("return com.jstarcraft.core.common.conversion.json.JsonUtility.string2Object((String) $1, this._clazz);"));
        methodBuilder.append("}");
        method.setBody(methodBuilder.toString());
        method.setModifiers(Modifier.PUBLIC);
        proxyClass.addMethod(method);
    }

    /**
     * 将指定类转换为{@link CtClass}
     * 
     * @param classes
     * @return
     * @throws NotFoundException
     */
    protected CtClass[] toProxyClasses(Class<?>... classes) throws NotFoundException {
        if (classes == null || classes.length == 0) {
            return new CtClass[0];
        }
        CtClass[] result = new CtClass[classes.length];
        for (int index = 0; index < classes.length; index++) {
            result[index] = classPool.get(classes[index].getName());
        }
        return result;
    }

    public static void main(String[] arguments) throws Exception {
        DecodeJsonProxy proxy = new DecodeJsonProxy();
        Converter<String, Integer> converter = proxy.getToConverter(Integer.class);
        System.out.println(converter.convert("10"));
        System.out.println(converter.getClass());
        System.out.println(converter.getClass().getGenericInterfaces());
    }

}
