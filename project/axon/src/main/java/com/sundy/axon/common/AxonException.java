package com.sundy.axon.common;

/**
 * axon框架的基础异常，所有的异常都关联到这个异常
 * @author Administrator
 *
 */
public abstract class AxonException extends RuntimeException {

	private static final long serialVersionUID = -6918866518055091314L;

	public AxonException(String message, Throwable cause) {
		super(message, cause);
	}

	public AxonException(String message) {
		super(message);
	}

	
	
}
