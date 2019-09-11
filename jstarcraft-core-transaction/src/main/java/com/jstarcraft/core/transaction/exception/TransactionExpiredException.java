package com.jstarcraft.core.transaction.exception;

/**
 * 事务过期异常
 * 
 * @author Birdy
 */
public class TransactionExpiredException extends TransactionException {

    private static final long serialVersionUID = -4342551686681422269L;

    public TransactionExpiredException() {
        super();
    }

    public TransactionExpiredException(String message, Throwable exception) {
        super(message, exception);
    }

    public TransactionExpiredException(String message) {
        super(message);
    }

    public TransactionExpiredException(Throwable exception) {
        super(exception);
    }

}
