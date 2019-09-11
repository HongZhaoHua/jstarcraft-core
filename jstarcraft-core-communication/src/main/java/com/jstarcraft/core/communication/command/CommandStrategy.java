package com.jstarcraft.core.communication.command;

/**
 * 执行策略
 * 
 * @author Birdy
 *
 */
public interface CommandStrategy {

    /**
     * 使用指定的执行策略
     * 
     * @param task
     */
    void execute(Runnable task);

}
