package com.sundy.axon.eventhandling.replay;

import com.sundy.axon.common.AxonNonTransientException;

public class EventReplayUnsupportedException extends AxonNonTransientException {

	private static final long serialVersionUID = 8993242695243067652L;

	public EventReplayUnsupportedException(String message) {
        super(message);
    }
	
}
