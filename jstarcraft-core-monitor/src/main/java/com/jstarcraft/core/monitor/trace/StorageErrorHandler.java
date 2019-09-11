package com.jstarcraft.core.monitor.trace;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.ErrorHandler;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.status.StatusLogger;

/**
 * 日志异常处理器
 * 
 * @author Birdy
 *
 */
public class StorageErrorHandler implements ErrorHandler {

    private static final Logger LOGGER = StatusLogger.getLogger();

    StorageErrorHandler() {
    }

    @Override
    public void error(final String message) {
        LOGGER.error(message);
    }

    @Override
    public void error(final String message, final Throwable throwable) {
        LOGGER.error(message, throwable);
    }

    @Override
    public void error(final String message, final LogEvent event, final Throwable throwable) {
        LOGGER.error(message, throwable);
    }

}
