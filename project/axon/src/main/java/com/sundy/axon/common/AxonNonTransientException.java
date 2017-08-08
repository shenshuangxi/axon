package com.sundy.axon.common;

public abstract class AxonNonTransientException extends AxonException {

	private static final long serialVersionUID = -1668273165647163665L;

	/**
	 * 判断该异常是否是由AxonNonTransientException异常引起的，或者是由其中一个引起的
	 * @param throwable
	 * @return
	 */
	public static boolean isCauseOf(Throwable throwable){
		return throwable != null && (throwable instanceof AxonNonTransientException || isCauseOf(throwable.getCause()));
	}
	
	public AxonNonTransientException(String message, Throwable cause) {
		super(message, cause);
	}

	public AxonNonTransientException(String message) {
		super(message);
	}

	
	
}
