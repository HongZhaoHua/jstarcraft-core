package com.jstarcraft.core.transaction.exception;

/**
 * 分布式任务异常
 * 
 * @author Birdy
 */
public class TransactionTaskException extends TransactionException {

    private static final long serialVersionUID = 2725404709235997067L;

    public TransactionTaskException() {
        super();
    }

    public TransactionTaskException(String message, Throwable exception) {
        super(message, exception);
    }

    public TransactionTaskException(String message) {
        super(message);
    }

    public TransactionTaskException(Throwable exception) {
        super(exception);
    }

}
