package com.sundy.axon.domain;

/**
 * 回调，用于允许通知组件，事件别注册到聚合。同时在这些事件到达事件处理器之前能够让组件修改这些事件，
 * @author Administrator
 *
 */
public interface EventRegistrationCallback {

	/**
	 * 当聚合注册事件到发布列表之前执行，
	 * 该接口的简单实现可以直接返回给定事件
	 * @param event 将要注册的事件
	 * @param <T> 载体类型
	 * @return 实际发布的事件
	 */
	<T> DomainEventMessage<T> onRegisteredEvent(DomainEventMessage<T> event);
	
}
