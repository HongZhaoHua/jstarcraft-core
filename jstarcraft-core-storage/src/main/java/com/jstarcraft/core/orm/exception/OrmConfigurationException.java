package com.jstarcraft.core.orm.exception;

/**
 * ORM配置异常
 * 
 * @author Birdy
 */
public class OrmConfigurationException extends OrmException {

    private static final long serialVersionUID = -8396525701135532677L;

    public OrmConfigurationException() {
        super();
    }

    public OrmConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

    public OrmConfigurationException(String message) {
        super(message);
    }

    public OrmConfigurationException(Throwable cause) {
        super(cause);
    }

}
