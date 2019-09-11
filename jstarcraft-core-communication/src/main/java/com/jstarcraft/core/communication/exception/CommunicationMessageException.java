package com.jstarcraft.core.communication.exception;

/**
 * 通讯消息异常
 * 
 * @author Birdy
 */
public class CommunicationMessageException extends CommunicationException {

    private static final long serialVersionUID = -1000873775801821762L;

    public CommunicationMessageException() {
        super();
    }

    public CommunicationMessageException(String message, Throwable exception) {
        super(message, exception);
    }

    public CommunicationMessageException(String message) {
        super(message);
    }

    public CommunicationMessageException(Throwable exception) {
        super(exception);
    }

}
