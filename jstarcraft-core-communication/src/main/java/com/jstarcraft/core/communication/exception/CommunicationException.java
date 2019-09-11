package com.jstarcraft.core.communication.exception;

/**
 * 通讯异常
 * 
 * @author Birdy
 */
public class CommunicationException extends RuntimeException {

    private static final long serialVersionUID = 5690545554687210032L;

    public CommunicationException() {
        super();
    }

    public CommunicationException(String message, Throwable exception) {
        super(message, exception);
    }

    public CommunicationException(String message) {
        super(message);
    }

    public CommunicationException(Throwable exception) {
        super(exception);
    }

}
