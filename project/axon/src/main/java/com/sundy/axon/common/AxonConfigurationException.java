package com.sundy.axon.common;

public class AxonConfigurationException extends AxonNonTransientException{

	private static final long serialVersionUID = 7622126076556228816L;

	public AxonConfigurationException(String message, Throwable cause) {
		super(message, cause);
	}

	public AxonConfigurationException(String message) {
		super(message);
	}
	
}
