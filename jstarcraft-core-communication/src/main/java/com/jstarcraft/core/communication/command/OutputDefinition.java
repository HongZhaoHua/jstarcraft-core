package com.jstarcraft.core.communication.command;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
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
 * 输出定义
 * 
 * @author Birdy
 *
 */
public class OutputDefinition {

    /** 内容格式 */
    private byte contentFormat;
    /** 内容类型 */
    private Type contentType;
    /** 输出变量 */
    private VariableDefinition outputVariable;

    boolean hasOutput() {
        return outputVariable != null;
    }

    Type getOutputType() {
        // 没有输出
        if (outputVariable == null) {
            return void.class;
        }
        CommandVariable variable = outputVariable.getVariable();
        if (VariableType.MESSAGE_BODY.equals(variable.type()) && StringUtility.isBlank(variable.property())) {
            // 以变量类型作为输出类型
            return outputVariable.getType();
        }
        // 以内容类型作为输出类型
        if (!(void.class.equals(contentType) || Void.class.equals(contentType))) {
            return contentType;
        } else {
            return void.class;
        }
    }

    Type getContentType() {
        // 没有输入
        if (outputVariable == null) {
            return void.class;
        }
        // 存在指定的内容类型
        if (!(void.class.equals(contentType) || Void.class.equals(contentType))) {
            return contentType;
        }
        // 以变量类型作为内容类型
        CommandVariable variable = outputVariable.getVariable();
        if (VariableType.MESSAGE_BODY.equals(variable.type()) && StringUtility.isBlank(variable.property())) {
            // 以变量类型作为输出类型
            return outputVariable.getType();
        } else {
            return void.class;
        }
    }

    /**
     * 将输出值转换为消息体
     * 
     * @param outputValue
     * @return
     */
    MessageBody getMessageBody(Map<Byte, ContentCodec> codecs, Object outputValue) {
        Type outputType = getOutputType();
        Object content = null;
        if (void.class.equals(outputType)) {
            if (outputValue != null && outputVariable == null) {
                throw new IllegalArgumentException();
            }
            MessageFormat format = MessageFormat.fromByte(contentFormat);
            ContentCodec codec = codecs.get(format.getMark());
            byte[] data = codec.encode(getContentType(), content);
            return MessageBody.instanceOf(MessageFormat.isZip(contentFormat), format, data);
        }
        CommandVariable variable = outputVariable.getVariable();
        if (variable != null && VariableType.MESSAGE_BODY.equals(variable.type())) {
            if (!StringUtility.isBlank(variable.property())) {
                Map<String, Object> value = new HashMap<>();
                value.put(variable.property(), outputValue);
                content = value;
            } else {
                content = outputValue;
            }
            content = ConversionUtility.convert(content, getContentType());
        }
        MessageFormat format = MessageFormat.fromByte(contentFormat);
        ContentCodec codec = codecs.get(format.getMark());
        byte[] data = codec.encode(getContentType(), content);
        return MessageBody.instanceOf(MessageFormat.isZip(contentFormat), format, data);
    }

    /**
     * 根据指定消息与会话转化为输出值
     * 
     * @param message
     * @param session
     * @return
     */
    Object getOutputValue(Map<Byte, ContentCodec> codecs, CommunicationMessage message, CommunicationSession<?> session) {
        Type outputType = getOutputType();
        MessageBody body = message.getBody();
        ContentCodec codec = codecs.get(body.getType().getMark());
        Object content = codec.decode(getContentType(), body.getContent());
        if (void.class.equals(outputType)) {
            if (content != null) {
                throw new IllegalArgumentException();
            }
            return null;
        }
        content = ConversionUtility.convert(content, outputType);
        return outputVariable.getValue(content, message, session);
    }

    static OutputDefinition instanceOf(Method method, Class<?> outputClass, MessageCodec socketCodec) {
        if (!CommandDefinition.checkType(method.getGenericReturnType())) {
            throw new CommunicationDefinitionException("指令的参数与返回不能有擦拭类型与通配符类型");
        }
        int modifier = outputClass.getModifiers();
        if (!outputClass.isPrimitive() && Modifier.isAbstract(modifier)) {
            throw new CommunicationDefinitionException("指令的参数与返回不能为抽象");
        }
        if (Modifier.isInterface(modifier)) {
            throw new CommunicationDefinitionException("指令的参数与返回不能为接口");
        }

        OutputDefinition instance = new OutputDefinition();
        instance.contentFormat = MessageFormat.toByte(socketCodec.outputFormat(), socketCodec.outputZip());
        instance.contentType = outputClass;
        AnnotatedType annotatedType = method.getAnnotatedReturnType();
        Annotation[] annotations = annotatedType.getAnnotations();
        boolean hasBodyVariable = false;
        CommandVariable variable = null;
        for (Annotation annotation : annotations) {
            if (annotation instanceof CommandVariable) {
                if (variable != null) {
                    throw new CommunicationDefinitionException();
                }
                variable = (CommandVariable) annotation;

                if (VariableType.MESSAGE_BODY.equals(variable.type())) {
                    hasBodyVariable = true;
                }
            }
        }
        if (variable != null) {
            instance.outputVariable = VariableDefinition.instanceOf(annotatedType.getType(), variable, null);
        }

        if (instance.outputVariable == null && !(outputClass.equals(void.class) || outputClass.equals(Void.class))) {
            throw new CommunicationDefinitionException();
        }
        if (instance.outputVariable != null) {
            if (!hasBodyVariable && !(outputClass.equals(void.class) || outputClass.equals(Void.class))) {
                throw new CommunicationDefinitionException();
            }

            if (hasBodyVariable && !StringUtility.isBlank(instance.outputVariable.getVariable().property()) && outputClass.equals(void.class)) {
                throw new CommunicationDefinitionException();
            }
        }

        return instance;
    }

}
