package com.jstarcraft.core.communication.exception;

/**
 * 通讯状态异常
 * 
 * @author Birdy
 */
public class CommunicationStateException extends CommunicationException {

    private static final long serialVersionUID = -1225338702368374913L;

    public CommunicationStateException() {
        super();
    }

    public CommunicationStateException(String message, Throwable exception) {
        super(message, exception);
    }

    public CommunicationStateException(String message) {
        super(message);
    }

    public CommunicationStateException(Throwable exception) {
        super(exception);
    }

}
