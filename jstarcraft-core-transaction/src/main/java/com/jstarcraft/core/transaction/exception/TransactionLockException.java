package com.jstarcraft.core.transaction.exception;

import com.jstarcraft.core.transaction.TransactionManager;

/**
 * 分布式加锁异常(必须由{@link TransactionManager}具体实现的lock方法使用)
 * 
 * @author Birdy
 */
public class TransactionLockException extends TransactionException {

    private static final long serialVersionUID = -8052326981691061048L;

    public TransactionLockException() {
        super();
    }

    public TransactionLockException(String message, Throwable exception) {
        super(message, exception);
    }

    public TransactionLockException(String message) {
        super(message);
    }

    public TransactionLockException(Throwable exception) {
        super(exception);
    }

}
