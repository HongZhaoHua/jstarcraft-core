package com.jstarcraft.core.orm.berkeley.exception;

import com.jstarcraft.core.orm.exception.OrmException;

public class BerkeleyOperationException extends OrmException  {

	private static final long serialVersionUID = -3668430093940319385L;

	public BerkeleyOperationException(String message, Throwable cause) {
		super(message, cause);
	}

	public BerkeleyOperationException(String message) {
		super(message);
	}

	public BerkeleyOperationException(Throwable cause) {
		super(cause);
	}
	
}