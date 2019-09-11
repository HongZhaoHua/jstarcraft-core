package com.jstarcraft.core.communication.exception;

/**
 * 通讯等待异常
 * 
 * @author Birdy
 */
public class CommunicationWaitException extends CommunicationException {

    private static final long serialVersionUID = -8191934167794170443L;

    public CommunicationWaitException() {
        super();
    }

    public CommunicationWaitException(String message, Throwable exception) {
        super(message, exception);
    }

    public CommunicationWaitException(String message) {
        super(message);
    }

    public CommunicationWaitException(Throwable exception) {
        super(exception);
    }

}
