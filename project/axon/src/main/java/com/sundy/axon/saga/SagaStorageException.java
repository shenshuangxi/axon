package com.sundy.axon.saga;

import com.sundy.axon.common.AxonNonTransientException;

public class SagaStorageException extends AxonNonTransientException {

	private static final long serialVersionUID = 8647774017416996907L;

	public SagaStorageException(String message) {
        super(message);
    }
	
	public SagaStorageException(String message, Throwable cause) {
        super(message, cause);
    }
	
}
