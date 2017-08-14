package com.sundy.axon.eventsourcing;

import com.sundy.axon.common.AxonNonTransientException;

public class IncompatibleAggregateException extends AxonNonTransientException {

	private static final long serialVersionUID = -796046114732894922L;

	public IncompatibleAggregateException(String message) {
		super(message);
	}
	
	public IncompatibleAggregateException(String message, Exception cause) {
        super(message, cause);
    }

}
