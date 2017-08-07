package com.sundy.axon.eventhandling;

import com.sundy.axon.domain.EventMessage;

/**
 * 该接口的实例用于处理事件
 * @author Administrator
 *
 */
public interface EventListener {

	/**
	 * 处理给定的事件，该类的实例将决定是否处理该事件，不建议在处理事件进程中抛出异常
	 * @param event
	 */
	void handle(EventMessage event);
	
}
