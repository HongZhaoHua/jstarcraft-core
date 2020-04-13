package com.jstarcraft.core.communication.command;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.jstarcraft.core.codec.ContentCodec;
import com.jstarcraft.core.common.conversion.ConversionUtility;
import com.jstarcraft.core.communication.annotation.CommandVariable;
import com.jstarcraft.core.communication.annotation.CommandVariable.VariableType;
import com.jstarcraft.core.communication.annotation.MessageCodec;
import com.jstarcraft.core.communication.exception.CommunicationDefinitionException;
import com.jstarcraft.core.communication.message.CommunicationMessage;
import com.jstarcraft.core.communication.message.MessageBody;
import com.jstarcraft.core.communication.message.MessageFormat;
import com.jstarcraft.core.communication.session.CommunicationSession;
import com.jstarcraft.core.utility.StringUtility;

/**
 * 输入定义
 * 
 * @author Birdy
 *
 */
class InputDefinition {

    /** 内容格式 */
    private byte contentFormat;
    /** 内容类型 */
    private Type contentType;
    /** 输入类型 */
    private Integer inputVariableIndex;
    /** 输入变量 */
    private VariableDefinition[] inputVariables;

    private InputDefinition() {
    }

    boolean hasInput() {
        return inputVariables.length != 0;
    }

    Type getInputType() {
        // 没有输入
        if (inputVariables.length == 0) {
            return void.class;
        }
        // 以变量类型作为输入类型
        if (inputVariableIndex != null) {
            return inputVariables[inputVariableIndex].getType();
        }
        // 以内容类型作为输入类型
        if (!(void.class.equals(contentType) || Void.class.equals(contentType))) {
            return contentType;
        } else {
            return void.class;
        }
    }

    Type getContentType() {
        // 没有输入
        if (inputVariables == null) {
            return void.class;
        }
        // 存在指定的内容类型
        if (!(void.class.equals(contentType) || Void.class.equals(contentType))) {
            return contentType;
        }
        // 以变量类型作为内容类型
        if (inputVariableIndex != null) {
            return inputVariables[inputVariableIndex].getType();
        } else {
            return void.class;
        }
    }

    private static Class<?> getClass(Type type) {
        if (type instanceof ParameterizedType) {
            // 泛型类型
            ParameterizedType parameterizedType = (ParameterizedType) type;
            return getClass(parameterizedType.getRawType());
        } else if (type instanceof GenericArrayType) {
            // 数组类型
            return Array.class;
        } else if (type instanceof TypeVariable) {
            // 擦拭类型
            throw new CommunicationDefinitionException();
        } else if (type instanceof WildcardType) {
            // 通配类型
            throw new CommunicationDefinitionException();
        } else if (type instanceof Class) {
            return (Class<?>) type;
        }
        return null;
    }

    /**
     * 将内容转换为消息体
     * 
     * @param inputValue
     * @return
     */
    MessageBody getMessageBody(Map<Byte, ContentCodec> codecs, Object inputValue) {
        try {
            if (inputValue != null) {
                Class<?> clazz = inputValue.getClass();
                if (Collection.class.isAssignableFrom(clazz)) {
                    Collection<?> inputValues = (Collection<?>) inputValue;
                    return getMessageBody(codecs, inputValues.toArray());
                }
            }
            Object content = ConversionUtility.convert(inputValue, getContentType());
            MessageFormat format = MessageFormat.fromByte(contentFormat);
            ContentCodec codec = codecs.get(format.getMark());
            byte[] data = codec.encode(getContentType(), content);
            return MessageBody.instanceOf(MessageFormat.isZip(contentFormat), format, data);
        } catch (Exception exception) {
            throw new IllegalArgumentException(exception);
        }
    }

    /**
     * 将输入值转换为消息体
     * 
     * @param inputValues
     * @return
     */
    MessageBody getMessageBody(Map<Byte, ContentCodec> codecs, Object[] inputValues) {
        Type inputType = getInputType();
        Object content = null;
        if (void.class.equals(inputType)) {
            if (inputValues.length != inputVariables.length) {
                throw new IllegalArgumentException();
            }
            MessageFormat format = MessageFormat.fromByte(contentFormat);
            ContentCodec codec = codecs.get(format.getMark());
            byte[] data = codec.encode(getContentType(), content);
            return MessageBody.instanceOf(MessageFormat.isZip(contentFormat), format, data);
        }
        if (inputVariableIndex != null) {
            content = inputValues[inputVariableIndex];
        } else {
            Class<?> inputClass = getClass(inputType);
            if (Collection.class.isAssignableFrom(inputClass) || Arrays.class.isAssignableFrom(inputClass)) {
                Object[] value = new Object[inputValues.length];
                for (int cursor = 0, size = inputVariables.length; cursor < size; cursor++) {
                    VariableDefinition definition = inputVariables[cursor];
                    CommandVariable variable = definition.getVariable();
                    if (VariableType.MESSAGE_BODY.equals(variable.type())) {
                        Integer index = Integer.valueOf(variable.property());
                        value[index] = ConversionUtility.convert(inputValues[cursor], definition.getType());
                    }
                }
                content = value;
            } else {
                Map<String, Object> value = new HashMap<>();
                for (int cursor = 0, size = inputVariables.length; cursor < size; cursor++) {
                    VariableDefinition definition = inputVariables[cursor];
                    CommandVariable variable = definition.getVariable();
                    if (VariableType.MESSAGE_BODY.equals(variable.type())) {
                        value.put(variable.property(), ConversionUtility.convert(inputValues[cursor], definition.getType()));
                    }
                }
                content = value;
            }
        }
        content = ConversionUtility.convert(content, getContentType());
        MessageFormat format = MessageFormat.fromByte(contentFormat);
        ContentCodec codec = codecs.get(format.getMark());
        byte[] data = codec.encode(getContentType(), content);
        return MessageBody.instanceOf(MessageFormat.isZip(contentFormat), format, data);
    }

    /**
     * 根据指定消息与会话转化为输入值
     * 
     * @param message
     * @param session
     * @return
     */
    Object[] getInputValues(Map<Byte, ContentCodec> codecs, CommunicationMessage message, CommunicationSession<?> session) {
        Type inputType = getInputType();
        MessageBody body = message.getBody();
        ContentCodec codec = codecs.get(body.getType().getMark());
        Object content = codec.decode(getContentType(), body.getContent());
        if (void.class.equals(inputType) && inputVariables.length == 0) {
            if (content != null) {
                throw new IllegalArgumentException();
            }
            return new Object[] {};
        }
        content = ConversionUtility.convert(content, inputType);
        Object[] values = new Object[inputVariables.length];
        for (int cursor = 0, size = inputVariables.length; cursor < size; cursor++) {
            values[cursor] = inputVariables[cursor].getValue(content, message, session);
        }
        return values;
    }

    /**
     * 
     * @param method
     * @param convertor
     * @return
     */
    private static VariableDefinition[] getVariables(Method method) {
        Type[] types = method.getGenericParameterTypes();
        VariableDefinition[] definitions = new VariableDefinition[types.length];
        Annotation[][] annotations = method.getParameterAnnotations();
        for (int index = 0; index < types.length; index++) {
            CommandVariable variable = null;
            for (Annotation annotation : annotations[index]) {
                if (annotation instanceof CommandVariable) {
                    variable = (CommandVariable) annotation;
                }
            }
            if (variable == null) {
                throw new CommunicationDefinitionException();
            }
            VariableDefinition definition = VariableDefinition.instanceOf(types[index], variable, index);
            definitions[index] = definition;
        }
        return definitions;
    }

    static InputDefinition instanceOf(Method method, Class<?> inputClass, MessageCodec socketCodec) {
        for (Type type : method.getGenericParameterTypes()) {
            if (!CommandDefinition.checkType(type)) {
                throw new CommunicationDefinitionException("指令的参数与返回不能有擦拭类型与通配符类型");
            }
        }
        int modifier = inputClass.getModifiers();
        if (!inputClass.isPrimitive() && Modifier.isAbstract(modifier)) {
            throw new CommunicationDefinitionException("指令的参数与返回不能为抽象");
        }
        if (Modifier.isInterface(modifier)) {
            throw new CommunicationDefinitionException("指令的参数与返回不能为接口");
        }

        InputDefinition instance = new InputDefinition();
        instance.contentFormat = MessageFormat.toByte(socketCodec.inputFormat(), socketCodec.inputZip());
        instance.contentType = inputClass;
        instance.inputVariables = getVariables(method);
        instance.inputVariableIndex = null;

        boolean hasBodyVariable = false;
        for (VariableDefinition definition : instance.inputVariables) {
            CommandVariable variable = definition.getVariable();
            if (VariableType.MESSAGE_BODY.equals(variable.type())) {
                if (StringUtility.isBlank(variable.property()) && !MessageBody.class.equals(definition.getType())) {
                    if (instance.inputVariableIndex != null) {
                        throw new CommunicationDefinitionException();
                    }
                    instance.inputVariableIndex = definition.getPosition();
                }
                hasBodyVariable = true;
            }
        }

        if (instance.inputVariables.length == 0 && !inputClass.equals(void.class)) {
            throw new CommunicationDefinitionException();
        }
        if (instance.inputVariables.length != 0) {
            if (!hasBodyVariable && !inputClass.equals(void.class)) {
                throw new CommunicationDefinitionException();
            }
            if (hasBodyVariable && instance.inputVariableIndex == null && inputClass.equals(void.class)) {
                throw new CommunicationDefinitionException();
            }
        }

        // TODO 存在索引变量,类型必须为Array或者Collection
        return instance;
    }

}
