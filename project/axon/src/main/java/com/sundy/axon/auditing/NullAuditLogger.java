package com.sundy.axon.auditing;

import java.util.List;

import com.sundy.axon.domain.CommandMessage;
import com.sundy.axon.domain.EventMessage;

public class NullAuditLogger implements AuditLogger{

	public static final NullAuditLogger INSTANCE = new NullAuditLogger();
	
	public void logSuccessful(CommandMessage<?> command, Object returnValue,
			List<EventMessage> events) {
		// TODO Auto-generated method stub
		
	}

	public void logFailed(CommandMessage<?> command, Throwable failureCause,
			List<EventMessage> events) {
		// TODO Auto-generated method stub
		
	}

}
