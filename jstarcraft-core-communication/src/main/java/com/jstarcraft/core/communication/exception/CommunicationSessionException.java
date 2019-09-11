package com.jstarcraft.core.communication.exception;

/**
 * 通讯会话异常
 * 
 * @author Birdy
 */
public class CommunicationSessionException extends CommunicationException {

    private static final long serialVersionUID = -2967988518844512960L;

    public CommunicationSessionException() {
        super();
    }

    public CommunicationSessionException(String message, Throwable exception) {
        super(message, exception);
    }

    public CommunicationSessionException(String message) {
        super(message);
    }

    public CommunicationSessionException(Throwable exception) {
        super(exception);
    }

}
