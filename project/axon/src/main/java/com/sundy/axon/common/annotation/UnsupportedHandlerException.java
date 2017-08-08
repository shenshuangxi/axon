package com.sundy.axon.common.annotation;

import java.lang.reflect.Member;

import com.sundy.axon.common.AxonConfigurationException;

public class UnsupportedHandlerException extends AxonConfigurationException {

	private static final long serialVersionUID = 4322060502038590009L;
	private final Member violatingMethod;


	public UnsupportedHandlerException(String message, Member violatingMethod) {
		super(message);
		this.violatingMethod = violatingMethod;
	}


	public Member getViolatingMethod() {
		return violatingMethod;
	}
	
	
	
}
