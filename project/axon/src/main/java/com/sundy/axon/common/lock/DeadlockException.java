package com.sundy.axon.common.lock;

public class DeadlockException extends LockAcquisitionFailedException {

	private static final long serialVersionUID = -5552006099153686607L;
	
	public DeadlockException(String message) {
        super(message);
    }
	
}
