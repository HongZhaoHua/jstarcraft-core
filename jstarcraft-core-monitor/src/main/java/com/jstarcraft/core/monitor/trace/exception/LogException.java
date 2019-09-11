package com.jstarcraft.core.monitor.trace.exception;

import org.apache.logging.log4j.LoggingException;

/**
 * 日志异常
 * 
 * @author Birdy
 */
public class LogException extends LoggingException {

    private static final long serialVersionUID = -6807194955167819578L;

    public LogException(String message, Throwable exception) {
        super(message, exception);
    }

    public LogException(String message) {
        super(message);
    }

    public LogException(Throwable exception) {
        super(exception);
    }

}
