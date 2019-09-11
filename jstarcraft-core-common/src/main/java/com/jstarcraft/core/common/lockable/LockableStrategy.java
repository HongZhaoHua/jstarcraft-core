package com.jstarcraft.core.common.lockable;

/**
 * 锁管理器
 * 
 * @author Birdy
 *
 */
public interface LockableStrategy {

    /**
     * 获取指定的参数对应的锁
     * 
     * @param arguments
     * @return
     */
    Lockable getLock(Object... arguments);

}
