package com.sundy.axon.auditing;

import java.util.Collections;
import java.util.Map;

import com.sundy.axon.domain.CommandMessage;

public class EmptyDataProvider implements AuditDataProvider {

	public static final EmptyDataProvider INSTANCE = new EmptyDataProvider();
	
	public Map<String, Object> provideAuditDataFor(CommandMessage<?> command) {
		return Collections.emptyMap();
	}

}
