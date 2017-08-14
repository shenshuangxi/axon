package com.sundy.axon.eventsourcing;

import com.sundy.axon.domain.DomainEventMessage;

/**
 * 事件源实体的接口，它是聚合的一部分，而不是其根。 
 * 应用于聚合的事件可以传播到该实体，只要它被正确地暴露为聚合根的后代（直接或间接子）。
 * @author Administrator
 *
 */
public interface EventSourcedEntity {

	/**
	 * 使用该实体注册聚合根。 实体必须使用此聚合根来应用领域事件。 聚合根负责跟踪所有应用的事件。
	 * <p/>
	 * 父实体负责在传播事件之前对其子实体调用此方法。
	 * 通常，这意味着在对其执行任何操作之前，所有实体都必须设置其聚合根集
	 * @param aggregateRootToRegister
	 */
	void registerAggregateRoot(AbstractEventSourcedAggregateRoot aggregateRootToRegister);
	
	/**
	 * 向当前实例以及引用该实例的所有的实例报告传入的事件消息
	 * @param eventMessage
	 */
	void handleRecursively(DomainEventMessage eventMessage);
	
}
