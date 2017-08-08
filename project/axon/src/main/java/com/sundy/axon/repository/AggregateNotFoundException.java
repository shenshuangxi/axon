package com.sundy.axon.repository;

import com.sundy.axon.common.AxonNonTransientException;

public class AggregateNotFoundException extends AxonNonTransientException {

	private static final long serialVersionUID = 5501890876652977470L;
	private final Object aggregateIdentifier;

	public AggregateNotFoundException(Object aggregateIdentifier, String message, Throwable cause) {
		super(message, cause);
		this.aggregateIdentifier = aggregateIdentifier;
	}

	public AggregateNotFoundException(Object aggregateIdentifier, String message) {
		super(message);
		this.aggregateIdentifier = aggregateIdentifier;
	}

	public Object getAggregateIdentifier() {
		return aggregateIdentifier;
	}
	
	
	
}
