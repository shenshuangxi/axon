package com.sundy.axon.common.lock;

import com.sundy.axon.common.AxonNonTransientException;

public class LockAcquisitionFailedException extends AxonNonTransientException {

	private static final long serialVersionUID = 4453369833513201587L;
	
	public LockAcquisitionFailedException(String message, Throwable cause) {
        super(message, cause);
    }
	
	public LockAcquisitionFailedException(String message) {
        super(message);
    }
	
}
