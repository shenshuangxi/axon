package com.sundy.axon.common.annotation;

import java.lang.reflect.AccessibleObject;

/**
 * 定义一个成员(方法或构造方法)是否为消息处理器，一般认为添加一个注解到该方法上，但也不限于这种方式
 * @author Administrator
 *
 * @param <T> 被认为是可以处理消息的类成员 (方法 构造方法)
 */
public interface HandlerDefinition<T extends AccessibleObject> {

	/**
	 * 判断端类成员是否为消息处理器
	 * @param member
	 * @return
	 */
	boolean isMessageHandler(T member);
	
	/**
	 * 返回类成员变量上显示定义的消息载体
	 * @param member
	 * @return
	 */
	Class<?> resolvePayloadFor(T member);
	
}
