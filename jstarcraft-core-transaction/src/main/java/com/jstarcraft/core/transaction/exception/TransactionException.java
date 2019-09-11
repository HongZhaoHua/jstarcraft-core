package com.jstarcraft.core.transaction.exception;

/**
 * 事务异常
 * 
 * @author Birdy
 */
public class TransactionException extends RuntimeException {

    private static final long serialVersionUID = 5755685315477361860L;

    public TransactionException() {
        super();
    }

    public TransactionException(String message, Throwable exception) {
        super(message, exception);
    }

    public TransactionException(String message) {
        super(message);
    }

    public TransactionException(Throwable exception) {
        super(exception);
    }

}
