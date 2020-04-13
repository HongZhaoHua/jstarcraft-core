package com.jstarcraft.core.communication.command;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;

import com.jstarcraft.core.common.conversion.ConversionUtility;
import com.jstarcraft.core.common.reflection.TypeUtility;
import com.jstarcraft.core.communication.annotation.CommandVariable;
import com.jstarcraft.core.communication.exception.CommunicationDefinitionException;
import com.jstarcraft.core.communication.message.CommunicationMessage;
import com.jstarcraft.core.communication.message.MessageBody;
import com.jstarcraft.core.communication.message.MessageHead;
import com.jstarcraft.core.communication.message.MessageTail;
import com.jstarcraft.core.communication.session.CommunicationSession;
import com.jstarcraft.core.utility.PropertyUtility;
import com.jstarcraft.core.utility.StringUtility;

/**
 * 变量定义
 * 
 * @author Birdy
 */
public class VariableDefinition {

    /** 变量位置 */
    private Integer position;
    /** 变量类型 */
    private Type type;
    /** 变量注解 */
    private CommandVariable variable;

    /**
     * 获取变量值
     * 
     * @param message
     * @param session
     * @return
     */
    Object getValue(Object content, CommunicationMessage message, CommunicationSession<?> session) {
        switch (variable.type()) {
        case MESSAGE_BODY:
            if (StringUtility.isBlank(variable.property())) {
                if (type.equals(MessageBody.class)) {
                    return message.getBody();
                } else {
                    return content;
                }
            }
            Class<? extends Object> bodyClass = content.getClass();
            if (bodyClass.isArray() || Collection.class.isAssignableFrom(bodyClass)) {
                Object[] values = null;
                if (bodyClass.isArray()) {
                    values = (Object[]) content;
                } else {
                    values = ((Collection<?>) content).toArray();
                }
                int index = Integer.parseInt(variable.property());
                if (index >= 0 && index < values.length) {
                    return values[index];
                } else if (!variable.nullable()) {
                    throw new CommunicationDefinitionException();
                } else {
                    return null;
                }
            } else if (content instanceof Map) {
                String name = variable.property();
                Map<?, ?> map = (Map<?, ?>) content;
                if (map.containsKey(name)) {
                    Object value = map.get(name);
                    try {
                        return ConversionUtility.convert(value, type);
                    } catch (Exception exception) {
                        throw new CommunicationDefinitionException(exception);
                    }
                } else if (!variable.nullable()) {
                    throw new CommunicationDefinitionException();
                } else {
                    return null;
                }
            } else {
                String name = variable.property();
                try {
                    return PropertyUtility.getProperty(content, name);
                } catch (Exception exception) {
                    if (!variable.nullable()) {
                        throw new CommunicationDefinitionException(exception);
                    } else {
                        return null;
                    }
                }
            }
        case MESSAGE_HEAD:
            if (StringUtility.isBlank(variable.property())) {
                if (type.equals(MessageHead.class)) {
                    return message.getHead();
                } else {
                    throw new CommunicationDefinitionException();
                }
            }
            try {
                return PropertyUtility.getProperty(message.getHead(), variable.property());
            } catch (Exception exception) {
                if (!variable.nullable()) {
                    throw new CommunicationDefinitionException(exception);
                } else {
                    return null;
                }
            }
        case MESSAGE_TAIL:
            if (StringUtility.isBlank(variable.property())) {
                if (type.equals(MessageTail.class)) {
                    return message.getTail();
                } else {
                    throw new CommunicationDefinitionException();
                }
            }
            try {
                return PropertyUtility.getProperty(message.getTail(), variable.property());
            } catch (Exception exception) {
                if (!variable.nullable()) {
                    throw new CommunicationDefinitionException(exception);
                } else {
                    return null;
                }
            }
        case SESSION_ATTRIBUTE:
            if (StringUtility.isBlank(variable.property())) {
                if (type.equals(CommunicationSession.class)) {
                    return session;
                } else {
                    throw new CommunicationDefinitionException();
                }
            }
            try {
                return PropertyUtility.getProperty(session, variable.property());
            } catch (Exception exception) {
                if (!variable.nullable()) {
                    throw new CommunicationDefinitionException(exception);
                } else {
                    return null;
                }
            }
        default:
            throw new CommunicationDefinitionException("未支持的VariableType类型[" + variable.type() + "]");
        }
    }

    public Integer getPosition() {
        return position;
    }

    public Type getType() {
        return type;
    }

    public CommandVariable getVariable() {
        return variable;
    }

    static VariableDefinition instanceOf(Type type, CommandVariable variable, Integer index) {
        Class<?> clazz = TypeUtility.getRawType(type, null);
        int modifier = clazz.getModifiers();
        if (!clazz.isPrimitive() && Modifier.isAbstract(modifier)) {
            throw new CommunicationDefinitionException("指令的参数与返回不能为抽象");
        }
        if (Modifier.isInterface(modifier)) {
            throw new CommunicationDefinitionException("指令的参数与返回不能为接口");
        }

        VariableDefinition instance = new VariableDefinition();
        instance.variable = variable;
        instance.position = index;
        instance.type = type;
        return instance;
    }

}
