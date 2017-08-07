package com.sundy.axon.eventhandling;

import com.sundy.axon.domain.EventMessage;

/**
 * 一种机制，让事件监听器订阅事件，事件发布器发布事件。
 * 事件总线将所有事件分发到所有订阅该事件的监听器上
 * <p/>
 * 接口实现可能也可能不会将事件发布到分发线程的事件订阅者
 * @author Administrator
 *
 */
public interface EventBus {

	/**
	 * 将事件集发布到事件总线上，这些事件最终将分发到订阅者。
	 * <p/>
	 * 实现可以将给定事件作为单批量处理。并将这个鞋事件分发到所有订阅者手里
	 * @param events
	 */
	void publish(EventMessage... events);
	
	/**
	 * 将事件监听器订阅到总线上。如果已订阅，那么将接收到所有发布到总线上的事件
	 * <p/>
	 * 如果该监听器已经存在于总线上，那么不会发生任何事情
	 * @param eventListener
	 */
	void subscribe(EventListener eventListener);
	
	/**
	 * 取消订阅，如果监听器从总线上取消订阅，那么将不再接收任何事件
	 * @param eventListener
	 */
	void unsubscribe(EventListener eventListener);
	
}
