package com.sundy.axon.auditing;

import java.util.Map;

import com.sundy.axon.domain.CommandMessage;

public class CommandMetaDataProvider implements AuditDataProvider {

	public Map<String, Object> provideAuditDataFor(CommandMessage<?> command) {
		return command.getMetaData();
	}

}
