package com.jstarcraft.core.communication.exception;

/**
 * 通讯定义异常
 * 
 * @author Birdy
 */
public class CommunicationDefinitionException extends CommunicationException {

    private static final long serialVersionUID = 7184446088529419066L;

    public CommunicationDefinitionException() {
        super();
    }

    public CommunicationDefinitionException(String message, Throwable exception) {
        super(message, exception);
    }

    public CommunicationDefinitionException(String message) {
        super(message);
    }

    public CommunicationDefinitionException(Throwable exception) {
        super(exception);
    }

}
