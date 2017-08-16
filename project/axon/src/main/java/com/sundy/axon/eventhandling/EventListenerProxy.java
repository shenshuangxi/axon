package com.sundy.axon.eventhandling;

/**
 * 该接口用于一个监听器实例，将具体的处理过程转发给其他实例来处理
 * @author Administrator
 *
 */
public interface EventListenerProxy extends EventListener {

	/**
	 * 返回此代理委托所有事件处理的实例类型。
	 * @return
	 */
	Class<?> getTargetType();
	
}
