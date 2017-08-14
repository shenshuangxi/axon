package com.sundy.axon.domain;

import com.sundy.axon.common.AxonNonTransientException;

public class AggregateIdentifierNotInitializedException extends
		AxonNonTransientException {
	
	private static final long serialVersionUID = -7720267057828643560L;

    public AggregateIdentifierNotInitializedException(String message) {
        super(message);
    }
	
}


