package com.jstarcraft.core.transaction.exception;

import com.jstarcraft.core.transaction.TransactionManager;

/**
 * 分布式解锁异常(必须由{@link TransactionManager}具体实现的unlock方法使用)
 * 
 * @author Birdy
 */
public class TransactionUnlockException extends TransactionException {

    private static final long serialVersionUID = -4971616622290538397L;

    public TransactionUnlockException() {
        super();
    }

    public TransactionUnlockException(String message, Throwable exception) {
        super(message, exception);
    }

    public TransactionUnlockException(String message) {
        super(message);
    }

    public TransactionUnlockException(Throwable exception) {
        super(exception);
    }

}
