package com.sundy.axon.common;

/**
 * 接口用于描述一个组件可以将自己订阅到其他组件，用于接收发布的订阅消息，如将消息订阅到这两个组件{@link CommandBus} {@link EventBus}
 * @author Administrator
 *
 *该组件在新版本已删除 因为如果一个组件可以被订阅，而有包含一个可订阅组件时会出现问题
 */
public interface Subscribable {

	/**
	 * 将本实例从发布组件中移除订阅
	 */
	void unsubscribe();
	
	/**
	 * 将本实例订阅到发布组件
	 */
	void subscribe();
	
}
