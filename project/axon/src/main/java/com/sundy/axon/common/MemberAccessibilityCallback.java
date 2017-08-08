package com.sundy.axon.common;

import java.lang.reflect.AccessibleObject;
import java.security.PrivilegedAction;

public class MemberAccessibilityCallback implements PrivilegedAction<Object> {

	private final AccessibleObject member;
	
	public MemberAccessibilityCallback(AccessibleObject member) {
		this.member = member;
	}

	public Object run() {
		member.setAccessible(true);
		return Void.class;
	}

}
