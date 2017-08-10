package com.sundy.axon.eventsourcing;

import com.sundy.axon.domain.AggregateRoot;
import com.sundy.axon.domain.DomainEventStream;

/**
 * 通过{@link DomainEventStream} 聚合可以被初始化，恢复原状态。
 * 所有可以被初始化的聚合都需要继承该接口
 * @author Administrator
 *
 * @param <I> 聚合类型
 */
public interface EventSourcedAggregateRoot<I> extends AggregateRoot<I> {

	/**
	 * 通过{@link DomainEventStream}中的领域事件流来初始化聚合的状态
	 * 如果调用该方法的聚合已经被出示了，会抛出{@link IllegalStateException}.
	 * @param domainEventStream
	 * 
	 * @throws IllegalStateException 聚合已被初始化
	 */
	void initializeState(DomainEventStream domainEventStream);
	
}
