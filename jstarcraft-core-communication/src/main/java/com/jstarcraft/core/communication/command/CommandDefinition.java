package com.jstarcraft.core.communication.command;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.jstarcraft.core.communication.annotation.CommunicationCommand;
import com.jstarcraft.core.communication.annotation.CommunicationModule;
import com.jstarcraft.core.communication.annotation.CommunicationModule.ModuleSide;
import com.jstarcraft.core.communication.annotation.MessageCodec;
import com.jstarcraft.core.communication.exception.CommunicationDefinitionException;

/**
 * 指令定义
 * 
 * @author Birdy
 *
 */
public class CommandDefinition {

    /** 模块编号 */
    private byte[] module;
    /** 模块端 */
    private ModuleSide side;
    /** 指令编号 */
    private byte command;
    /** 指令策略 */
    private String strategy;

    /** 输入定义 */
    private InputDefinition inputDefinition;
    /** 输出定义 */
    private OutputDefinition outputDefinition;

    /** 接口类型 */
    private Class<?> clazz;
    /** 接口方法 */
    private Method method;

    private CommandDefinition() {
    }

    public byte[] getModule() {
        return module;
    }

    public ModuleSide getSide() {
        return side;
    }

    public byte getCommand() {
        return command;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public Method getMethod() {
        return method;
    }

    public String getStrategy() {
        return strategy;
    }

    public InputDefinition getInputDefinition() {
        return inputDefinition;
    }

    public OutputDefinition getOutputDefinition() {
        return outputDefinition;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object)
            return true;
        if (object == null)
            return false;
        if (getClass() != object.getClass())
            return false;
        CommandDefinition that = (CommandDefinition) object;
        EqualsBuilder equal = new EqualsBuilder();
        equal.append(this.module, that.module);
        equal.append(this.command, that.command);
        return equal.isEquals();
    }

    @Override
    public int hashCode() {
        HashCodeBuilder hash = new HashCodeBuilder();
        hash.append(module);
        hash.append(command);
        return hash.toHashCode();
    }

    @Override
    public String toString() {
        return "CommandDefinition [clazz=" + clazz + ", method=" + method + "]";
    }

    /**
     * 检查指定的类型
     * 
     * <pre>
     * 不能有擦拭类型和通配类型
     * </pre>
     * 
     * @param type
     * @return
     */
    static boolean checkType(Type type) {
        if (type instanceof ParameterizedType) {
            // 泛型类型
            ParameterizedType parameterizedType = (ParameterizedType) type;
            if (!checkType(parameterizedType.getRawType())) {
                return false;
            }
            for (Type value : parameterizedType.getActualTypeArguments()) {
                if (!checkType(value)) {
                    return false;
                }
            }
        } else if (type instanceof TypeVariable) {
            // 擦拭类型
            return false;
        } else if (type instanceof GenericArrayType) {
            // 数组类型
            GenericArrayType genericArrayType = (GenericArrayType) type;
            if (!checkType(genericArrayType.getGenericComponentType())) {
                return false;
            }
        } else if (type instanceof WildcardType) {
            // 通配类型
            return false;
        } else if (type instanceof Class) {
            return true;
        }
        return true;
    }

    /**
     * 指令定义
     * 
     * @param method
     * @return
     */
    public static CommandDefinition instanceOf(Method method) {
        Class<?> clazz = method.getDeclaringClass();
        if (!clazz.isInterface()) {
            throw new CommunicationDefinitionException("指令定义必须为接口");
        }
        CommunicationModule module = clazz.getAnnotation(CommunicationModule.class);
        CommunicationCommand command = method.getAnnotation(CommunicationCommand.class);
        MessageCodec codec = method.getAnnotation(MessageCodec.class);
        if (codec == null) {
            codec = clazz.getAnnotation(MessageCodec.class);
            if (codec == null) {
                throw new CommunicationDefinitionException("指令定义必须指定输入与输出编解码格式");
            }
        }
        CommandDefinition instance = new CommandDefinition();
        instance.module = module.code();
        instance.side = module.side();
        instance.command = command.code();
        instance.strategy = command.strategy();
        instance.inputDefinition = InputDefinition.instanceOf(method, command.input(), codec);
        instance.outputDefinition = OutputDefinition.instanceOf(method, command.output(), codec);
        instance.clazz = clazz;
        instance.method = method;
        return instance;
    }

}
