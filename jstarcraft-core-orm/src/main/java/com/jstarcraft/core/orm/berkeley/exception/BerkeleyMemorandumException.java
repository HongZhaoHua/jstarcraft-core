package com.jstarcraft.core.orm.berkeley.exception;

import com.jstarcraft.core.orm.exception.OrmException;

/**
 * Berkeley备忘异常
 * 
 * @author Birdy
 *
 */
public class BerkeleyMemorandumException extends OrmException {

	private static final long serialVersionUID = 6634969038060172220L;

	public BerkeleyMemorandumException() {
		super();
	}

	public BerkeleyMemorandumException(String message, Throwable cause) {
		super(message, cause);
	}

	public BerkeleyMemorandumException(String message) {
		super(message);
	}

	public BerkeleyMemorandumException(Throwable cause) {
		super(cause);
	}

}