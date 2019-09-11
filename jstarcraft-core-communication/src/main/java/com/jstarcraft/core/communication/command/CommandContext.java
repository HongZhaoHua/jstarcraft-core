package com.jstarcraft.core.communication.command;

import java.util.concurrent.CompletableFuture;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.jstarcraft.core.communication.exception.CommunicationWaitException;

/**
 * 指令执行上下文
 * 
 * @author Birdy
 *
 */
public class CommandContext {

    /** 定义 */
    private CommandDefinition definition;

    /** 序列号 */
    private int sequence;

    /** 任务 */
    private CompletableFuture task;

    CommandContext(CommandDefinition definition, int sequence) {
        this.definition = definition;
        this.sequence = sequence;
        this.task = new CompletableFuture<>();
    }

    public CommandDefinition getDefinition() {
        return definition;
    }

    public int getSequence() {
        return sequence;
    }

    void setValue(Object value) {
        task.complete(value);
    }

    void setException(Exception exception) {
        task.completeExceptionally(exception);
    }

    public Object getValue() {
        try {
            return task.get();
        } catch (Exception exception) {
            throw new CommunicationWaitException(exception);
        }
    }

    @Override
    public boolean equals(Object object) {
        if (this == object)
            return true;
        if (object == null)
            return false;
        if (getClass() != object.getClass())
            return false;
        CommandContext that = (CommandContext) object;
        EqualsBuilder equal = new EqualsBuilder();
        equal.append(this.sequence, that.sequence);
        return equal.isEquals();
    }

    @Override
    public int hashCode() {
        HashCodeBuilder hash = new HashCodeBuilder();
        hash.append(sequence);
        return hash.toHashCode();
    }

}
