package com.jstarcraft.core.storage.exception;

/**
 * ORM配置异常
 * 
 * @author Birdy
 */
public class StorageConfigurationException extends StorageException {

    private static final long serialVersionUID = -8396525701135532677L;

    public StorageConfigurationException() {
        super();
    }

    public StorageConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

    public StorageConfigurationException(String message) {
        super(message);
    }

    public StorageConfigurationException(Throwable cause) {
        super(cause);
    }

}
