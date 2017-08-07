package com.sundy.axon.domain;

/**
 * DomainEventStream 代表一条历史领域事件流。该事件排序代表事件发生的实际时间顺序，该DomainEventStream 提供访问这些事件的所有子集
 * @author Administrator
 *
 */
public interface DomainEventStream {

	/**
	 * 如果为true则表示可以调用next()得到后续事件
	 * @return
	 */
	boolean hasNext();
	
	/**
	 * 返回下一个可用的领域事件。可以使用hasNext()来确保得到领域事件。调用next()将会将指针指向下一个事件。如果已到达末尾
	 * 那么会返回null
	 * @return
	 */
	DomainEventMessage next();
	
	/**
	 * 不会移动指针去获取下一个事件，所以调用如果调用next()返回的事件跟peek()返回的是同一个事件
	 * @return
	 */
	DomainEventMessage peek();
}
