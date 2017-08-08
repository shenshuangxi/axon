package com.sundy.axon.repository;

import com.sundy.axon.common.AxonNonTransientException;

public class ConflictingModificationException extends AxonNonTransientException {

	private static final long serialVersionUID = -4501504661999941076L;

	public ConflictingModificationException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public ConflictingModificationException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	
	
}
