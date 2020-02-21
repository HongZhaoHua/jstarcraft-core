package com.jstarcraft.core.orm.berkeley.exception;

import com.jstarcraft.core.orm.exception.OrmException;

/**
 * Berkeley状态异常
 * 
 * @author Birdy
 *
 */
public class BerkeleyStateException extends OrmException {

    private static final long serialVersionUID = -466786536849658568L;

    public BerkeleyStateException() {
        super();
    }

    public BerkeleyStateException(String message, Throwable cause) {
        super(message, cause);
    }

    public BerkeleyStateException(String message) {
        super(message);
    }

    public BerkeleyStateException(Throwable cause) {
        super(cause);
    }

}
