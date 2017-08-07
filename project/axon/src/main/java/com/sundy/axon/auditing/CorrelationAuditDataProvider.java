package com.sundy.axon.auditing;

import java.util.Collections;
import java.util.Map;

import com.sundy.axon.common.Assert;
import com.sundy.axon.domain.CommandMessage;

public class CorrelationAuditDataProvider implements AuditDataProvider {

	public static final String DEFAULT_CORRELATION_KEY = "command-identifier";
	
	private final String correlationIdKey;
	
	public CorrelationAuditDataProvider(){
		this(DEFAULT_CORRELATION_KEY);
	}
	
	public CorrelationAuditDataProvider(String correlationIdKey) {
		Assert.notNull(correlationIdKey, "correlationIdKey may not be null");
		this.correlationIdKey = correlationIdKey;
	}

	public Map<String, Object> provideAuditDataFor(CommandMessage<?> command) {
		return Collections.singletonMap(correlationIdKey, (Object)command.getIdentifier());
	}

}
