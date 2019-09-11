package com.jstarcraft.core.communication.exception;

/**
 * 通讯配置异常
 * 
 * @author Birdy
 */
public class CommunicationConfigurationException extends CommunicationException {

    private static final long serialVersionUID = 8489014829295689684L;

    public CommunicationConfigurationException() {
        super();
    }

    public CommunicationConfigurationException(String message, Throwable exception) {
        super(message, exception);
    }

    public CommunicationConfigurationException(String message) {
        super(message);
    }

    public CommunicationConfigurationException(Throwable exception) {
        super(exception);
    }

}
