package com.jstarcraft.core.orm.berkeley.exception;

import com.jstarcraft.core.orm.exception.OrmException;

/**
 * Berkeley迁移异常
 * 
 * @author Birdy
 *
 */
public class BerkeleyMigrationException extends OrmException {

	private static final long serialVersionUID = 9130426400020103627L;

	public BerkeleyMigrationException(String message, Throwable cause) {
		super(message, cause);
	}

	public BerkeleyMigrationException(String message) {
		super(message);
	}

	public BerkeleyMigrationException(Throwable cause) {
		super(cause);
	}

}